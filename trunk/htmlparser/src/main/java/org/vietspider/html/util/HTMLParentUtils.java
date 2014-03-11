/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.util;

import java.util.List;

import org.vietspider.html.HTMLNode;
import org.vietspider.html.NodeIterator;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 30, 2008  
 */
public class HTMLParentUtils {
  
  public HTMLNode getUpParent(List<HTMLNode> nodes) {
    if(nodes.size() < 1) return null;
    HTMLNode parent  = nodes.get(0);
    while(parent != null) {
      if(isChild(parent, nodes, 1)) {
        return parent;
      }
      parent = parent.getParent();
    }
    return null;
  }
  
  private boolean isChild(HTMLNode parent, List<HTMLNode> nodes, int start) {
    for(int i = start; i < nodes.size(); i++) {
      if(!isChild(parent, nodes.get(i))) return false;
    }
    return true;
  }
  
  private boolean isChild(HTMLNode parent, HTMLNode node) {
    NodeIterator iterator = parent.iterator();
    while(iterator.hasNext()) {
      HTMLNode n = iterator.next();
      if(n == node) return true;
    }
//    if(node == parent) return true;
//    List<HTMLNode> children  = parent.getChildren();
//    if(children == null || children.size() < 1) return false;
//    for(int i = 0; i < children.size(); i++) {
//      if(isChild(children.get(i), node)) return true;
//    }
    return false;
  }
}
