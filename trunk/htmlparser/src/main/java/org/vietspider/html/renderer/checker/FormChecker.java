/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.renderer.checker;

import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.NodeIterator;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 10, 2009  
 */
public class FormChecker {
  
  boolean hasForm(HTMLNode node) {
    NodeIterator iterator = node.iterator();
    while(iterator.hasNext()) {
      HTMLNode n = iterator.next();
      if(n.isNode(Name.FORM)) return true;
      if(isFormElement(n)) return true;
    }
    return false;
  }
  
  private boolean isFormElement(HTMLNode node) {
    switch (node.getName()) {
    case INPUT:
    case TEXTAREA:
    case SELECT:
    case LABEL:
    case BUTTON:
      return true;
    default:
      return false;
    }
  }
}
