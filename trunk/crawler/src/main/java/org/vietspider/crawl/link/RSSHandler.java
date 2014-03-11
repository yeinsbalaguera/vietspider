/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.link;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.parser.rss2.IMetaItem;
import org.vietspider.parser.rss2.MetaDocument;
import org.vietspider.parser.rss2.MetaLink;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 29, 2007  
 */
public class RSSHandler {
  
  private final static char [] CHANNEL = "<channel".toCharArray();
  private final static char [] ITEM = "<item".toCharArray();
  
  private final static char [] FEED = "<feed".toCharArray();
  private final static char [] ENTRY = "<entry".toCharArray();
  
  public boolean isRssDocument(char [] chars) {
    return (indexOf(chars, CHANNEL) && indexOf(chars, ITEM)) 
              || (indexOf(chars, FEED) && indexOf(chars, ENTRY)) ;
  }
  
  public List<Link> handle(MetaDocument document) {
    if(document == null) return new ArrayList<Link>(0);
    List<? extends IMetaItem> items = document.getItems();
    List<Link> collection = new ArrayList<Link>(items.size());
    for(Object item : items){
      IMetaItem rssItem = (IMetaItem)item;
      List<MetaLink> metaLinks = rssItem.getLinks();
      if(metaLinks == null || metaLinks.size() < 1) continue;
      for(MetaLink metaLink : metaLinks) {
        if(metaLink == null) continue;
        String href = extract(metaLink.getRealHref());
        if(href == null || href.trim().isEmpty()) continue;
        collection.add(new Link(href, null));
      }
    }
    return collection;
  }
  
  private String extract(String link) {
    int index =0;
    int length  = link.length();
    if(length < 15) return link;
    while(index < length) {
      char c = link.charAt(index);
      if(Character.isWhitespace(c) 
          || Character.isSpaceChar(c)) {
        index++;
        continue;
      }
      break;
    }
    
    if(length < index+15) return link;
    if(
        link.charAt(index) == '<' &&
        link.charAt(index+1) == '!' &&
        link.charAt(index+2) == '[' &&
        link.charAt(index+3) == 'C' &&
        link.charAt(index+4) == 'D' &&
        link.charAt(index+5) == 'A' &&
        link.charAt(index+6) == 'T' &&
        link.charAt(index+7) == 'A' &&
        link.charAt(index+8) == '[' 
        ) {
      int start  = index + 9;
      int end = link.lastIndexOf("]]>");
      if(end > 0)  return link.substring(start, end);
      
    }
    return link;
  }
  
  private boolean indexOf(char[] value, char [] c) {
    boolean is = false;
    for(int i = 0; i < value.length; i++){
      is = true;
      for(int j = 0; j< c.length; j++){
        while(j > 0 && i+j < value.length && Character.isWhitespace(value[i+j])){
          i++;
        }
        if(i+j < value.length && (c[j] ==  value[i+j]) ) continue;
        is = false;
        break;
      }      
      if(is) return true;
    }
    return false;
  }
  
}
