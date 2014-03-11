/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.browser;

import java.util.List;

import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.parser.HTMLParser2;
import org.vietspider.html.parser.NodeImpl;
import org.vietspider.html.path2.HTMLExtractor;
import org.vietspider.html.path2.NodePath;
import org.vietspider.html.path2.NodePathParser;
import org.vietspider.html.renderer.TextRenderer;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 13, 2008  
 */
class LoginUtils {
  
  private String [] prevs;
  private String [] afters;
  
  public LoginUtils() {
  }
  
  void setPrevLogin(List<NodeImpl> tokens) throws Exception {
    prevs = build(tokens);
  }
  
  void setAfterLogin(List<NodeImpl> tokens) throws Exception {
    afters = build(tokens);
  }
  
  private String [] build(List<NodeImpl> tokens) throws Exception {
    HTMLDocument doc = new HTMLParser2().createDocument(tokens);
    NodePathParser pathParser = new NodePathParser();
    HTMLExtractor extractor  = new HTMLExtractor();
    
    NodePath nodePath  = pathParser.toPath("BODY");
    HTMLNode body = extractor.lookNode(doc.getRoot(), nodePath);
    
    TextRenderer renderer = new TextRenderer(body, TextRenderer.HANDLER);
    String value = renderer.getTextValue().toString();
    return value.trim().split("\n");
  }
  
  String getError() {
    StringBuilder builder = new StringBuilder();
    for(String ele : afters) {
      if(builder.length() > 0 
          && builder.charAt(builder.length()-1) != '\n') builder.append('\n');
      if(!exists(ele)) builder.append(ele);
    }
    return builder.toString().trim();
  }
  
  private boolean exists(String pattern) {
    for(String ele : prevs) {
      if(ele.trim().equalsIgnoreCase(pattern.trim())) return true;
    }
    return false;
  }
  

}
