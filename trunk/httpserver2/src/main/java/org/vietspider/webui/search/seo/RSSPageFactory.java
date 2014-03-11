package org.vietspider.webui.search.seo;


import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.vietspider.bean.Article;
import org.vietspider.common.Application;
import org.vietspider.common.text.SWProtocol;
import org.vietspider.db.SystemProperties;
import org.vietspider.db.database.MetaList;
import org.vietspider.webui.cms.CMSService;
import org.vietspider.webui.cms.render.RSSRenderer;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Feb 12, 2007
 */
public class RSSPageFactory implements RSSRenderer {
  
  private RSSMetaRenderer metaRenderer = new RSSMetaRenderer();
  private byte[] cached;
  private long lastAccess;
  
//  private String keyword = "nha";
  
//  public void setKeyword(/*String keyword*/) {
//    this.keyword = keyword;
//  }

  @SuppressWarnings("unused")
  public void write(OutputStream output_, String viewer, MetaList metas) throws Exception {
    Calendar calendar = Calendar.getInstance();
    if(cached != null 
        && calendar.getTimeInMillis() - lastAccess < 2*60*1000l) {
      output_.write(cached);
      return;
    }
    
    lastAccess = calendar.getTimeInMillis();
    
    metas = new MetaList("rss");
    metas.setPageSize(100);
    
//    ClassifiedSearchQuery query = new ClassifiedSearchQuery(keyword);
//    if(DatabaseService.isMode(DatabaseService.SEARCH)) {
//      DatabaseService.getLoader().search(metas, query);
//    }
    
    //working with entry
//    EntryReader entryReader = new EntryReader();
//    SimpleDateFormat dateFormat = CalendarUtils.getDateFormat();
//    String date = dateFormat.format(calendar.getTime());
//    IEntryDomain entryDomain = new SimpleEntryDomain(date, null, null);
//    entryReader.read(entryDomain, metas, -1);

    metas.setTitle("RSS Channel");
    
    CMSService cms = CMSService.INSTANCE;
    SystemProperties systemProperties = SystemProperties.getInstance();

    StringBuilder buildHost = new StringBuilder(SWProtocol.HTTP);
    buildHost.append(CMSService.INSTANCE.getHost());
    if(cms.getWebPort() != 80) buildHost.append(':').append(cms.getWebPort());
    String host = buildHost.toString();
    
    StringBuilder builder = new StringBuilder();

    builder.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
    builder.append("<rss version=\"2.0\" xmlns:vietspider=\"");
    builder.append(host);
    builder.append("/site/rss/\">\n<channel>\n<title>");

    builder.append(systemProperties.getValue(Application.APPLICATION_NAME)).append("</title>");
    builder.append("\n<link>").append(host).append("/site/rss/</link>");
    builder.append("\n<description>Description</description>");
    builder.append("\n<language>vi</language>");
    builder.append("\n<copyright>").append(systemProperties.getValue(Application.APPLICATION_NAME)).append("</copyright>");
    SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss 'GMT'");
    builder.append("n<pubDate>").append(formatter.format(calendar.getTime())).append("</pubDate>");

//    RefsDecoder decoder = new RefsDecoder();

//    for(int p = 0 ; p < 10; p++) {
//      metas.setCurrentPage(p);
    LastAccessData.getInstance().loadArticles(metas);
    List<Article> list = metas.getData();
//    System.out.println(p + "  :  size "+ list.size());
//    if(list.size() < 1) break;
    SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
    for(int i = 0; i < list.size(); i++){
      Article ele  = list.get(i);
      if(ele == null) continue;
      metaRenderer.render(builder, host, ele);   
    }
//      list.clear();
//    }
    builder.append("\n</channel>\n</rss>");
    
    cached = builder.toString().getBytes(Application.CHARSET);
//    System.out.println(" truoc "+ cached.length);
//    cached = new GZipIO().zip(cached);
//    System.out.println(" sau "+ cached.length);
    output_.write(cached);
  }
  
}
