/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.browser;

import java.util.List;

import org.vietspider.chars.refs.RefsDecoder;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.NodeIterator;

/**
 * Author : Thuannd
 *         nhudinhthuan@yahoo.com
 * Apr 21, 2006
 */
public class TextHandler { 
  
   public HTMLNode findByText(HTMLNode node, String txt, RefsDecoder decoder){
    String value = getTextContent(node).toString();
    if(decoder != null) value = new String(decoder.decode(value.toCharArray()));
    value = trim(value); 
//    System.out.println(value);
//    System.out.println("==================");
//    System.out.println(txt);
//    System.out.println("==================");
//    System.out.println(value.indexOf(txt));
    if(value.indexOf(txt) < 0)  return null;
    List<HTMLNode> children  = node.getChildrenNode();
    for(HTMLNode ele : children){
      HTMLNode newNode = findByText(ele, txt, decoder);
      if(newNode != null) return newNode;
    }
    return node;
  }

  public StringBuilder getTextContent(HTMLNode node){
    StringBuilder value = new StringBuilder();
    NodeIterator iterator = node.iterator();
    while(iterator.hasNext()) {
      HTMLNode n = iterator.next();
      if(n.isNode(Name.CONTENT)) {
        value.append(n.getValue());    
      }    
    }
    /*if(node.getConfig().name() == Name.CONTENT){
      value.append(node.getValue());    
    }    
    List<HTMLNode> children = node.getChildrenNode();
    for(HTMLNode ele : children){
      if(node.getConfig().name() == Name.CONTENT)
        value.append(ele.getValue());
      else 
        value.append(getTextContent(ele));
    }
*/
    return value;
  }

  public String trim(String value){
    value = value.replace('\t', ' ');
    value = value.replace('\n', ' ');
    value = value.replace('\b', ' ');
    value = value.replace('\f', ' ');
    value = value.replace('\r', ' ');
    value = value.replaceAll(" ", "");
    return value;
  }

  public String findStartText(HTMLNode node) {
    List<HTMLNode> children = node.getChildren();
    for(HTMLNode ele : children){
      if(node.getConfig().name() == Name.CONTENT){  
        String value = new String(ele.getValue()).trim();
        if(value.length() > 0) return value;
      }else {
        String value = findStartText(ele);
        if(value != null) return value;
      }
    }
    return null;
  }  

  public String findEndText(HTMLNode node) {
    List<HTMLNode> children = node.getChildren();
    
    for(int i = children.size() - 1; i>-1; i--){
      if(node.getConfig().name() == Name.CONTENT){  
        String value = new String(children.get(i).getValue()).trim();
        if(value.length() > 0) return value;
      }else{
        String value = findEndText(children.get(i));
        if(value != null) return value;
      }
    }
    return null;
  }   
}