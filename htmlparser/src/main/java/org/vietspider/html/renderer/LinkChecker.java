/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.renderer;

import org.vietspider.chars.URLEncoder;
import org.vietspider.html.HTMLNode;
import org.vietspider.token.attribute.Attribute;
import org.vietspider.token.attribute.Attributes;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 19, 2009  
 */
public class LinkChecker {
  
  private String url;
  private URLEncoder urlEncoder = new URLEncoder();
  
  public LinkChecker(String url_) {
    if(url_ == null) return;
    this.url = urlEncoder.encode(url_);
  }
  
  public boolean isValid(HTMLNode node) {
    String link = getLinkAttribute(node);
    if(link == null) return true;
    link =  urlEncoder.encode(link);
    //      System.out.println(link);
    //      System.out.println(url+"\n\n");
    if(link.equalsIgnoreCase(url)) return true;
    return false;
  }
  
  private String getLinkAttribute(HTMLNode node) {
    Attributes attributes = node.getAttributes(); 
    Attribute attribute = attributes.get("href");
    if(attribute != null) {
      String link = attribute.getValue();
      if(link != null && !(link = link.trim()).isEmpty()) {
        if(link.charAt(0) == '#') return null;
        return link;
      }
    }
    attribute = attributes.get("onclick");
    if(attribute == null) return  null;
    String link = attribute.getValue();
    if(link == null 
        || (link = link.trim()).isEmpty() 
        || link.charAt(0) == '#') return null;
    return link;
  }
}
