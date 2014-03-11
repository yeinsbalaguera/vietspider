/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.renderer.checker;

import java.util.List;

import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 10, 2009  
 */
public class EmptyNodeChecker extends NodeChecker {
  
  
  public EmptyNodeChecker(int level) {
    super(Name.UNKNOWN, level);
  }
  
  @Override
  public boolean isValid(CheckModel model, int lvl) {
    if(lvl != level) return true;
    if(!isElement(model.getNode())) return true;
    List<HTMLNode> children = model.getNode().getChildren();
    return children != null && children.size() > 0 ;
  }
  
  private boolean isElement(HTMLNode node) {
    if(node == null) return false;
    switch (node.getName()) {
    case FORM:
    case INPUT:
    case TEXTAREA:
    case SELECT:
    case LABEL:
    case BUTTON:
      
    case A:
    case DIV:
    case SPAN:
      
    case TR:
    case TD:
    case TH:
    case TBODY:
    case TABLE:
      return true;

    default:
      return false;
    }
  }
  
  @SuppressWarnings("unused")
  boolean check(CheckModel model) {
    return true;
  }
  
}
