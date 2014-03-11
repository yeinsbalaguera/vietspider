/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.webui.cms.vtemplate;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.vietspider.bean.Article;
import org.vietspider.bean.Meta;
import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.db.SystemProperties;
import org.vietspider.db.database.MetaList;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 10, 2011  
 */
public class RSSMetaListRenderer {
  
  private String host = "http://nik.vn/tin";

  public RSSMetaListRenderer() {
    File file = UtilFile.getFile("system/cms/vtemplate", "host.txt");
    try {
      String text = new String(RWData.getInstance().load(file), Application.CHARSET);
      String [] elements = text.trim().split("\n");
      if(elements.length > 0) host = elements[0];
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }

  public void write(HttpRequestData hrd, MetaList data) {
//    hrd.write(CommonRenderer.getInstance().renderHeader(hrd, data.getTitle()));
    
    StringBuilder builder = new StringBuilder();
    
    builder.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
    builder.append("<rss version=\"2.0\" xmlns:vietspider=\"");
    builder.append(host);
    builder.append("/site/rss/\">\n<channel>\n<title>");
    
    SystemProperties systemProperties = SystemProperties.getInstance();
    
    Calendar calendar = Calendar.getInstance();

    builder.append(systemProperties.getValue(Application.APPLICATION_NAME)).append("</title>");
    builder.append("\n<link>").append(host).append("/site/rss/</link>");
    builder.append("\n<description>Description</description>");
    builder.append("\n<language>vi</language>");
    builder.append("\n<copyright>").append(systemProperties.getValue(Application.APPLICATION_NAME)).append("</copyright>");
    SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss 'GMT'");
    builder.append("n<pubDate>").append(formatter.format(calendar.getTime())).append("</pubDate>");
    
    List<Article> list = data.getData();
    for(int i = 0; i < list.size(); i++){
      Article ele  = list.get(i);
      if(ele == null) continue;
      try {
        render(builder, host, ele);
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
    
    builder.append("\n</channel>\n</rss>");

    hrd.write(builder.toString());
    
    hrd.setContentType("text/xml");
  }
  
  void render(StringBuilder builder, String _host, Article article) throws Exception {  
    Meta  meta  = article.getMeta();
    if(meta  == null 
        || meta.getId() == null 
        || meta.getId().equals("null")) return ;
    
    builder.append("<item>");
    builder.append("<title><![CDATA[").append(meta.getTitle()).append("]]></title>");
    builder.append("<description><![CDATA[").append(meta.getDesc()).append("]]></description>");
    builder.append("<link>").append(_host).append("/detail/");
    builder.append(meta.getId());
    builder.append("/").append(meta.getAlias());
    builder.append("</link>");
    SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss 'GMT'");
    builder.append("<pubDate>").append(formatter.format(Calendar.getInstance().getTime())).append("</pubDate>");
    builder.append("</item>");
  }


}
