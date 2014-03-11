/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.plugin.handler;

import java.util.List;

import org.vietspider.bean.Content;
import org.vietspider.bean.Meta;
import org.vietspider.html.HTMLNode;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 11, 2009  
 */
public class ContentMetaHandler {
  
  public void cutMetaData(Meta meta, Content content) throws Exception { 
    StringBuilder builder = new StringBuilder();
    
    String address = meta.getSource();
    if(address.length() > 1000) {
      builder.append(address.substring(0, 995)).append("...");
      meta.setSource(builder.toString());
    }
    
    if(meta.getTitle() == null || meta.getTitle().trim().isEmpty()) {
      meta.setTitle("...");
    }
    
    StringBuilder title  = new StringBuilder(meta.getTitle());
    StringBuilder desc = new StringBuilder(meta.getDesc());
    
    String value = cutData(title, desc, 150, 100);
    if(value != null) {
      meta.setTitle(value);
      meta.setDesc(desc.toString());
    }
    
    desc = new StringBuilder(meta.getDesc());
    StringBuilder contentValue = new StringBuilder();
    if(content != null) contentValue.append(content.getContent());
    value = cutData(desc, contentValue, 800, 400);
    if(value != null) {
      meta.setDesc(value);
      if(content != null) content.setContent(contentValue.toString());
    }
    
  }
  
  public String cutData(StringBuilder value, StringBuilder append, int max, int from) {
    StringBuilder builder = new StringBuilder();
    if(value.length() <= max) return null;
    int index = from;
    builder.append(value.substring(0, from));
    while(index < max) {
      char c = value.charAt(index);
      if(Character.isLetterOrDigit(c)) {
        index++;
        continue;
      }
      builder.append(value.substring(from, index));
      append.insert(0, ' ');
      append.insert(0, value.substring(index));
      return builder.toString();
    }
    append.insert(0, ' ');
    append.insert(0, value.substring(from));
    return builder.toString();
  }
  
  public Content buildContent(List<HTMLNode> children, Meta meta, String date) throws Exception {
    StringBuilder buildContent = new StringBuilder();
    for(HTMLNode ele : children) {
      ele.buildValue(buildContent);
    }
    return new Content(meta.getId(), date, buildContent.toString());
    
   /* for detect website
    *  buildContent.append(' ').append(meta.getDes());
    buildContent.append(' ').append(meta.getTitle());
    
    String text = buildContent.toString();
    //detect blog template
//    text  = HomepageWriters.getInstance().extract(text);
    //detect website
    if(CrawlService.getInstance().isDetectWebsite()) {
      websiteScanner.detectWebsite(link, text);
    }*/
  }
  
}
