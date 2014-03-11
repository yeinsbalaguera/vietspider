/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.document.util;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.common.text.TextCounter;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.renderer.TextRenderer;
import org.vietspider.html.renderer.checker.CheckModel;
import org.vietspider.html.renderer.checker.LinkNodeChecker;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 29, 2008  
 */
public class OtherLinkRemover2 extends NodeRemover {
  
  private boolean checkFromStart = false;
  
  public OtherLinkRemover2() {
  }
  
  public OtherLinkRemover2(boolean checkFromStart) {
    this.checkFromStart = checkFromStart;
  }
  
  public List<HTMLNode> removeLinks(HTMLNode root, LinkNodeChecker linkNodeChecker) {
    List<HTMLNode> values = new ArrayList<HTMLNode>();
    
    TextRenderer renderer = new TextRenderer(root, TextRenderer.RENDERER);
    StringBuilder builder = renderer.getTextValue();
    
    int start = 0;
    if(!checkFromStart) start = builder.indexOf("\n\n");
    int end = builder.indexOf("\n\n", start+2);
    
    while(end > -1) {
      List<HTMLNode> nodes = handle(linkNodeChecker, renderer.getNodePositions(start, end));
      if(nodes != null) values.addAll(nodes);
      start = end;
      end = builder.indexOf("\n\n", start+2);
    }
    List<HTMLNode> nodes = handle(linkNodeChecker, renderer.getNodePositions(start, builder.length()));
    if(nodes != null) values.addAll(nodes);
    
    return values;
  }
  
  private List<HTMLNode> handle(LinkNodeChecker linkNodeChecker, List<HTMLNode> nodes) {
    StringBuilder builder = new StringBuilder();
    
    List<HTMLNode> links = new ArrayList<HTMLNode>();
    
    for(int i = 0; i < nodes.size(); i++) {
      HTMLNode node = nodes.get(i);
   
      if(isLinkContent(linkNodeChecker, node, 0)) {
        links.add(node);
        if(isValidNode(node)) continue;
      }
      
      if(builder.length() > 0) builder.append(' ');
      builder.append(node.getValue());
    }
    
    if(links.size() < 1) {
      return null;
    }

    TextCounter counter = new TextCounter();
    if(counter.countSentence(builder) > 3) return null;
    if(counter.countWords(builder) > 30) return null;
    
    return nodes;
  }
  
  private boolean isLinkContent(LinkNodeChecker linkNodeChecker, HTMLNode node, int level) {
    if(node == null || level > 3) return false;
    if(node.isNode(Name.A)) {
      if(linkNodeChecker != null 
          && linkNodeChecker.isValid(new CheckModel(node), 0)) return false;
      return true;
    }
    return isLinkContent(linkNodeChecker, node.getParent(), level+1);
  }
  
  private boolean isValidNode(HTMLNode node) {
    List<HTMLNode> links = nodeUtil.search(node, Name.CONTENT);
    if(links.size() != 1) return false;
    String text = new String(links.get(0).getValue());
    text = text.trim();
    char ch = text.charAt(0);
    if(Character.isLetter(ch) && Character.isLowerCase(ch)) return false;
    
    TextCounter counter = new TextCounter();
    if(counter.countSentence(text) > 1) return false;
    
    return true;
  }
  
}
