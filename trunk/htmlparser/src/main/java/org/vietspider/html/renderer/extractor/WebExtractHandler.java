/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.renderer.extractor;

import org.vietspider.html.HTMLNode;
import org.vietspider.html.renderer.checker.ContentChecker;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 10, 2009  
 */
public class WebExtractHandler {
  
  protected ContentChecker contentChecker = new ContentChecker();
  
  public void removeNode(HTMLNode node) {
    if(node == null) return;
    HTMLNode parent = node.getParent();
    if(parent == null || !parent.hasChildren()) return ;
    parent.removeChild(node);
//    if(parent.getChildren().size() < 1) removeNode(parent);
  }
  
  protected boolean isBlockElement(HTMLNode node) {
    switch (node.getName()) {
    case DIV:
    case TABLE:
      return true;
    default:
      return false;
    }
  }
  
  protected boolean isTextElement(HTMLNode node) {
    switch (node.getName()) {
    case CONTENT:
    case SPAN:
    case P:
      return contentChecker.isTextBlock(node, false, 10, 2);
    default:
      return false;
    }
  }
}
