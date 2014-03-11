/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.util;

import java.util.List;

import org.vietspider.chars.refs.RefsDecoder;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.NodeIterator;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 17, 2008  
 */
public class HTMLText {
  
  public void buildText(StringBuilder builder, HTMLNode node, Verify verify) {
    if(verify == null) {
      buildText(builder, node);
      return;
    }
    
    if(node == null) return;
    NodeIterator iterator = node.iterator();
    
    while(iterator.hasNext()) {
      HTMLNode n = iterator.next();
      if(!n.isNode(Name.CONTENT)) continue;
      char [] chars = n.getValue();
      if(!verify.isValid(chars)) continue;
      if(builder.length() > 0) builder.append(' ');
      builder.append(chars);
    }
    
   /* if(node.isNode(Name.CONTENT)) {
      if(builder.length() > 0) builder.append(' ');
      builder.append(node.getValue());
      return;
    }
    
    List<HTMLNode> children  = node.getChildren();
    if(children == null) return;
    for (int i = 0; i < children.size(); i++) {
      extractText(builder, children.get(i));
    }*/
  }
  
  public void buildText(StringBuilder builder, HTMLNode node) {
    if(node == null) return;
    NodeIterator iterator = node.iterator();
    
    while(iterator.hasNext()) {
      HTMLNode n = iterator.next();
      if(!n.isNode(Name.CONTENT)) continue;
      if(builder.length() > 0) builder.append(' ');
      builder.append(n.getValue());
    }
  }
  
  public void buildText(StringBuilder builder, HTMLNode node, RefsDecoder decoder) {
    if(node == null) return;
    NodeIterator iterator = node.iterator();
    
    while(iterator.hasNext()) {
      HTMLNode n = iterator.next();
      if(!n.isNode(Name.CONTENT)) continue;
      if(builder.length() > 0) builder.append(' ');
      builder.append(decoder.decode(n.getValue()));
    }
  }
  
  public void searchText(List<HTMLNode> values, HTMLNode node) {
    if(node == null) return;
    NodeIterator iterator = node.iterator();
    
    while(iterator.hasNext()) {
      HTMLNode n = iterator.next();
      if(n.isNode(Name.CONTENT)) values.add(n);
    }
  }
  
  public void searchText(List<HTMLNode> values, HTMLNode node, Verify verify) {
    if(verify == null) {
      searchText(values, node);
      return;
    }
    
    if(node == null) return;
    NodeIterator iterator = node.iterator();
    
    while(iterator.hasNext()) {
      HTMLNode n = iterator.next();
      if(!n.isNode(Name.CONTENT)) continue;
      char [] chars = n.getValue();
      if(verify.isValid(chars)) values.add(n);
    }
  }
  
  public static interface Verify {
    public boolean isValid(char [] chars); 
  }
  
  public static class EmptyVerify implements Verify {
    public boolean isValid(char[] chars) {
      for(char ele : chars) {
        if(Character.isWhitespace(ele) 
            || Character.isSpaceChar(ele)) continue;
        return true;
      }
      return false;
    }
  };
  
}
