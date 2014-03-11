/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.renderer.checker;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.chars.URLEncoder;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.token.attribute.Attribute;
import org.vietspider.token.attribute.Attributes;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 19, 2009  
 */
public class LinkNodeChecker extends NodeChecker {
  
  private String url;
  private URLEncoder urlEncoder = new URLEncoder();
  
  private List<HTMLNode> validNodes = new ArrayList<HTMLNode>();
  
  public LinkNodeChecker(String url_, int level) {
    super(Name.A, level);
    if(url_ == null) return;
    this.url = urlEncoder.encode(url_);
  }
  
  protected boolean check(CheckModel model) {
    HTMLNode node = model.getNode();
    
    if(model.hasRawData()) {
      validNodes.add(node);
      return true;
    }
    
    String link = getLinkAttribute(node);
    if(link == null) {
      validNodes.add(node);
      return true;
    }
    
    link =  urlEncoder.encode(link);
    //      System.out.println(link);
    //      System.out.println(url+"\n\n");
    if(link.equalsIgnoreCase(url)) {
      validNodes.add(node);
      return true;
    }
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
  
  public List<HTMLNode> getValidNodes() {
    return validNodes;
  }
}
