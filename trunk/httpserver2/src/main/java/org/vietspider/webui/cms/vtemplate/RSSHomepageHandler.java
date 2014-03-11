/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.webui.cms.vtemplate;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.vietspider.common.io.LogService;
import org.vietspider.common.text.CalendarUtils;
import org.vietspider.db.database.DatabaseService;
import org.vietspider.db.database.MetaList;
import org.vietspider.db.idm2.EntryReader;
import org.vietspider.db.idm2.IEntryDomain;
import org.vietspider.db.idm2.MergeEntryDomains;
import org.vietspider.db.idm2.SimpleEntryDomain;
import org.vietspider.server.handler.cms.DataNotFound;
import org.vietspider.webui.cms.RequestUtils;


/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Dec 26, 2006
 */
public class RSSHomepageHandler extends VTemplateHandler {
  
  private RSSMetaListRenderer renderer;
  private long lastUpdate = -1;
  private byte[] pcCached;
  private byte[] mobileCached;
  
  public RSSHomepageHandler(String type) {
    super(type); 
    name = "domain"; 
    
    renderer = new RSSMetaListRenderer();
  }

  public void handle(final HttpRequest request, final HttpResponse response,
                     final HttpContext context) throws HttpException, IOException {
    String path = request.getRequestLine().getUri();
    if(path.trim().length() < 2) {
      try {
        List<String> dates = DatabaseService.getLoader().loadDateFromDomain();
        if(dates.size() < 1)  throw new DataNotFound();
        
        HttpRequestData hrd = HttpUtils.createHRD(request, type);
        hrd.setParams(new String[]{"1", dates.get(0).replace('/', '.')});
        
        render(hrd);
        response.setEntity(hrd.createEntity());
      } catch (Exception e) {
        throw new HttpException(e.getMessage());
      }
      return;
    }

    super.handle(request, response, context);
  }
  

  public void render(HttpRequestData hrd) throws Exception {
    if(RequestUtils.isInvalidBot(hrd.getAgent())) {
      invalid(hrd);
      return;
    }
    
    if(RequestUtils.isBot(hrd.getAgent())) {
      LogService.getInstance().setMessage("WEB_CLIENT", null,
          "User Agent: " + hrd.getAgent() + ", is mobile? " + String.valueOf(hrd.isMobile()));
    } else {
      log.setMessage("USER_SEARCH", null,hrd.getUri());
      log.setMessage("USER_SEARCH", null, "User Agent: " + hrd.getAgent());
    }
    hrd.setPageType(HttpRequestData.EVENT);

    if(hrd.isMobile()) {
      if(mobileCached != null && System.currentTimeMillis() - lastUpdate < 30*60*1000l) {
        //      System.out.println(" van dung cached cu ");
        hrd.write(mobileCached);
        return;
      }
    } else {
      if(pcCached != null && System.currentTimeMillis() - lastUpdate < 30*60*1000l) {
        //      System.out.println(" van dung cached cu ");
        hrd.write(pcCached);
        return;
      }
    }
    //    System.out.println("time out roi nhe, cap nhat moi ");

    lastUpdate = System.currentTimeMillis();

    MetaList metas = new MetaList("rss");
    metas.setPageSize(100);
    metas.setCurrentPage(1);

    
    Calendar calendar = Calendar.getInstance();
    String date = CalendarUtils.getDateFormat().format(calendar.getTime());
    
    EntryReader entryReader = new EntryReader();
    IEntryDomain entryDomain = null;
    if(date != null) {
      entryDomain = new SimpleEntryDomain(date, null, null);
    } else {
      entryDomain = MergeEntryDomains.getInstance().getIterator(null, null);
    }
//    System.out.println("thay co "+title);
//    System.out.println(" hihi "+ entryDomain.getFile());
    entryReader.read(entryDomain, metas, -1);
    metas.setTitle("Nik tin tức - RSS Channel");

    /*List<Article> articles = metas.getData();

    List<TopArticles> collection = TopArticleService.getInstance().getCollection();
    for(int i = 0; i < collection.size(); i++) {
      TopArticles topArticles = collection.get(i);
      List<TopArticle> tlist = topArticles.getArticles();

      for(int  j = 0; j < tlist.size(); j++) {
        Article article = loadArticle(tlist.get(j).getId());
        if(article == null) continue; 
        articles.add(article);
      }
    }*/
    
    renderer.write(hrd, metas);
    if(hrd.isMobile()) {
      mobileCached = hrd.getOutput().toByteArray();
    } else {
      pcCached = hrd.getOutput().toByteArray();
    }
  }

 /* private Article loadArticle(String id) {
    ArticleDatabase db = (ArticleDatabase) 
    ArticleDatabases.getInstance().getDatabase(id, true, false);
    if(db == null) return null;
    try {
      Article article = new Article();
      String rawMeta = db.loadMetaAsRawText(Long.parseLong(id));
      if(rawMeta == null) return null;
      if(rawMeta.indexOf(ContentMapper.SEPARATOR) < 0) return null;
      ContentMapper.text2MetaData(article, rawMeta);
      if(article.getMeta() == null) return null;
      
//      article.setMeta(meta) ;
//      
//      Domain domain = db.loadDomain(Long.parseLong(meta.getDomain()));
//      article.setDomain(domain);
      
      db.loadMetaRelation2(article);

      return article;
    } catch (Throwable e) {
      LogService.getInstance().setThrowable(e);
    }
    return null;
  }*/

}
