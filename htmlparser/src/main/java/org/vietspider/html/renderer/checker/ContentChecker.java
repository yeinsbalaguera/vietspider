/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.renderer.checker;

import java.util.List;

import org.vietspider.common.text.TextCounter;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.NodeIterator;
import org.vietspider.html.renderer.RenderNodeUtils;
import org.vietspider.token.attribute.Attributes;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 10, 2009  
 */
public class ContentChecker {
  
  private TextCounter txtCounter = new TextCounter();
  
  public boolean isEmptyBlock(HTMLNode node, boolean checkLink) {
    if(isTextBlock(node, checkLink, 1, 1)) return false;
    return true;
  }
  
  public boolean isTextBlock(HTMLNode node, boolean checkLink, int w_size, int s_size) {
    NodeIterator nodeIterator = node.iterator();
    while(nodeIterator.hasNext()) {
      HTMLNode iterNode = nodeIterator.next();
      if(checkLink) {
        if(RenderNodeUtils.getAncestor(iterNode, Name.A, 0, 5) != null) continue;
      }
      if(iterNode.isNode(Name.CONTENT)) {
        String text = iterNode.getTextValue();
        int word = txtCounter.countWords(text);
        if(word >= w_size) return true;
        int sentence = txtCounter.countSentence(text);
        if(sentence >= s_size) return true;
      }
    }
    return false;
  }
  
  public boolean isRawBlock(HTMLNode node) {
    switch (node.getName()) {
    case OBJECT:
    case APPLET:
      if(isValidImage(node, 400, 250)) return true;
      return false;
    case IMG:
      if(isValidImage(node, 400, 250)) return true;
      return false;
    case MARQUEE:
    case SELECT:
    case TEXTAREA:     
      return false;
    default:
      break;
    }
  
    List<HTMLNode> children = node.getChildren();
    if(children == null) return false;
    for(int i = 0; i < children.size(); i++) {
      if(isRawBlock(children.get(i))) return true; 
    }
    return false;
  }
  
  boolean isValidImage(HTMLNode node, int width, int height) {
    Attributes attributes = node.getAttributes(); 
    if(RenderNodeUtils.getIntAttrValue(attributes, "width") >= width) return true;
    if(RenderNodeUtils.getIntAttrValue(attributes, "height") >= height) return true;
    return false;
  }

  public TextCounter getTextCounter() { return txtCounter; }
}
