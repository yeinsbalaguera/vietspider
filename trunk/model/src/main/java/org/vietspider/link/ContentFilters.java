/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.link;

import java.util.List;

import org.vietspider.html.HTMLNode;
import org.vietspider.model.Source;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 3, 2008  
 */
//Thuannd update code
public final class ContentFilters {
  
  private ContentFilter [] filters;
  
  private String tag = "Content Filter";

  public ContentFilters(Source source, String [] elements) {
    filters = new ContentFilter[elements.length];
    for(int i = 0; i < elements.length; i++) {
      elements[i] = elements[i].trim().toLowerCase();
      filters[i] = new ContentFilter(source, elements[i].split("\\+"));
    }
  }
  
  public boolean check(List<HTMLNode> nodes) {
    for(int i = 0; i < filters.length; i++) {
      if(filters[i].check(nodes) == -1) return false; 
    }
    
    return true;
  }
  
  public void setTag(String tag) { this.tag = tag; }
  
  public String getTag() { return tag; }

  public boolean mark(List<HTMLNode> nodes) {
    boolean _found = false;
    for(int i = 0; i < filters.length; i++) {
      if(filters[i].mark(nodes)) _found = true;
    }
    
    return _found;
  }
  
}
