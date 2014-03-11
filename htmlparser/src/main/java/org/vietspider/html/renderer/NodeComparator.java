/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.renderer;

import java.util.List;

import org.vietspider.html.HTMLNode;
import org.vietspider.token.attribute.Attribute;
import org.vietspider.token.attribute.Attributes;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 10, 2009  
 */
public class NodeComparator {
  
  public boolean compare(HTMLNode node1, HTMLNode node2) {
    if(node1.getName() != node2.getName()) return false;
    Attributes attributes1 = node1.getAttributes();
    Attributes attributes2 = node2.getAttributes();
    if(!compare(attributes1, attributes2)) return false; 
    return compare(node1.getChildren(), node2.getChildren());
  }
  
  private boolean compare(Attributes attributes1, Attributes attributes2) {
    if(attributes1.size() != attributes2.size()) return false;
    for(int i = 0; i < attributes1.size(); i++) {
      Attribute attr1 = attributes1.get(i);
      Attribute attr2 = attributes2.get(i);
      if(!attr1.getName().equalsIgnoreCase(attr2.getName())) return false;
    }
    return true;
  }
  
  private boolean compare(List<HTMLNode> children1, List<HTMLNode> children2) {
    if(children1  == null) {
      if(children2 == null) return true;
      return false;
    }
    if(children2 == null) return false;
    if(children1.size() != children2.size()) return false;
    for(int i = 0; i < children1.size(); i++) {
      if(!compare(children1.get(i), children2.get(i))) return false;
    }
    return true;
  }
}
