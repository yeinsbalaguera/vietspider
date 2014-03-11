/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.webui.search.seo;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.vietspider.bean.Article;
import org.vietspider.bean.Meta;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 4, 2009  
 */
class RSSMetaRenderer {
  
  void render(StringBuilder builder, String host, Article article) throws Exception {  
    Meta  meta  = article.getMeta();
    if(meta  == null 
        || meta.getId() == null 
        || meta.getId().equals("null")) return ;
    
    builder.append("<item>");
    builder.append("<title><![CDATA[").append(meta.getTitle()).append("]]></title>");
    builder.append("<description><![CDATA[").append(meta.getDesc()).append("]]></description>");
    builder.append("<link>").append(host).append("/site/cached/");
    builder.append(meta.getId());
    builder.append("/").append(meta.getAlias());
    builder.append("/?query=nhà").append("</link>");
    SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss 'GMT'");
    builder.append("<pubDate>").append(formatter.format(Calendar.getInstance().getTime())).append("</pubDate>");
    builder.append("</item>");
  }
  
 
}
