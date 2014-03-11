/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.plugin.index;

import java.util.List;

import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.parser.HTMLParser2;
import org.vietspider.html.util.NodeHandler;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 14, 2010  
 */
public class RtRenderder {

  public RtRenderder() {
  }

  public final String build(String value) throws Exception {
    HTMLDocument document = new HTMLParser2().createDocument(value);
    return build(new StringBuilder(), document);
  }
  
  public final String build(StringBuilder builder, String value) throws Exception {
    HTMLDocument document = new HTMLParser2().createDocument(value);
    return build(builder, document);
  }

  public final String build(StringBuilder builder, HTMLDocument document) throws Exception {
    build(builder, document.getRoot());
    return builder.toString();
  }

  private final void build(StringBuilder builder, HTMLNode node) throws Exception {
    List<HTMLNode> children  = node.getChildren();
    switch (node.getName()) {
    case CONTENT:
      char [] chars = node.getValue();
      if(!isEmpty(chars)) {
        if(notEndWith(builder, '\n')) builder.append('\n');
        String text = new String(chars).trim();
        if(notEndWith(text, ':')) builder.append(chars);
      } 
      break;
    case P:
      if(notEndWith(builder, '\n')) builder.append('\n');
      break;
    case H1:
    case H2:
    case H3:
    case H4:
    case H5:
    case H6:
    case TD:    
    case DIV:
    case BR:
      if(notEndWith(builder, '\n')) builder.append('\n');
      break;
    case TBODY:
      buildTable(builder, node);
      return;
    case A:
      return;
    default:
      if(builder.length() > 0) {
        char c = builder.charAt(builder.length()-1);
        if(!(Character.isWhitespace(c) 
            || Character.isSpaceChar(c))) {
          builder.append(' ');
        }
      }
      break;
    }

    if(children == null) return;
    for(int i = 0; i < children.size(); i++) {
      build(builder, children.get(i));
    }
  }
  
  private boolean notEndWith(CharSequence text, char c) {
    if(text.length() < 1) return true;
    return text.charAt(text.length() - 1) != c;
  }

  private void buildTable(StringBuilder builder, HTMLNode root)  throws Exception {
    List<HTMLNode> nodes = root.getChildren();
    if(nodes.size() < 3) {
      for(int i = 0; i < nodes.size(); i++) {
        build(builder, nodes.get(i));
      }
      return;
    }
    for(int i = 0; i < nodes.size(); i++) {
      HTMLNode node = nodes.get(i);
      if(node.isNode(Name.TR))  {
        buildTr(builder, node);
      } 
    }
  }
  
  private void buildTr(StringBuilder builder, HTMLNode root) throws Exception {
    List<HTMLNode> nodes = root.getChildren();
    for(int i = 0; i < nodes.size(); i++) {
      if(i%2 == 1) {
        build(builder, nodes.get(i));
      } else {
        int counter = countWord(nodes.get(i));
//        System.out.println( "ta co "+counter);
        if(counter > 15) build(builder, nodes.get(i));
      }
    }
  }
  
  private final boolean isEmpty(char [] chars) {
    int i = 0;
    while(i < chars.length) {
      if(Character.isWhitespace(chars[i]) 
          || Character.isSpaceChar(chars[i])) {
        i++;
        continue;
      }
      return false;
    }
    return true;
  }
  
  private int countWord(HTMLNode node) {
    int counter = 0;
    
    NodeHandler nodeHandler = new NodeHandler();
    if(node.isNode(Name.CONTENT)) {
      counter += nodeHandler.count(new String(node.getValue()));
//      System.out.println(new String(node.getValue()) + " : "+ counter);
      return counter;
    }
    
    List<HTMLNode> children = node.getChildren();
    if(children == null) return counter;
    
    for(int i = 0; i < children.size(); i++) {
      counter += countWord(children.get(i));
    }
    return counter; 
  }


}
