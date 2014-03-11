/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.document.util;

import java.util.List;

import org.vietspider.common.text.TextCounter;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.renderer.TextRenderer;
import org.vietspider.html.util.HTMLNodeUtil;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 29, 2008  
 */
abstract class NodeRemover {
  
  protected  HTMLNodeUtil nodeUtil = new HTMLNodeUtil();
  
  protected boolean isSuper(HTMLNode node, HTMLNode child) {
    if(node == child) return true;
    List<HTMLNode> children  = node.getChildren();
    if(children == null) return false;
    for(int i = 0; i < children.size(); i++) {
      if(children.get(i) == child) {
        return true;
      } if(isSuper(children.get(i), child)) {
        return true;
      }
    }
    return false;
  }
  
  public HTMLNode searchUpper(HTMLNode node, Name...names) {
    HTMLNode parent  = node.getParent();
    if(parent == null) return null;
    for(Name name  : names) {
      if(parent.isNode(name)) return parent;
    }
//    if(parent.isNode(Name.TABLE)
//        || parent.isNode(Name.DIV)
//        || parent.isNode(Name.CENTER)) {
//      return parent; 
//    }
    return searchUpper(parent, names);
  }
  
  public boolean isValidText(HTMLNode node, int size) {
    TextRenderer textRenderer = new TextRenderer(node, null);
    TextCounter counter = new TextCounter();
//    System.out.println(textRenderer.getTextValue());
//    System.out.println(counter.count(textRenderer.getTextValue()));
    if(counter.countSentence(textRenderer.getTextValue()) > size) return false;
    return true;
  }
  
}
