/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.plugin.handler;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.html.HTMLNode;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 5, 2008  
 */
public final class MergeTextNode {
  
  public List<String> mergeText(List<HTMLNode> list) {
    List<String> values = new ArrayList<String>();
    for(int i = 0; i < list.size(); i++) {
      HTMLNode node = list.get(i);
      if(!isStyleNode(node)) continue;
      if(isStyleNode(node.getParent())) node = node.getParent();
      HTMLNode preNode = i > 0 ? list.get(i-1) : null;
      HTMLNode nextNode = i < list.size()-1 ? list.get(i+1) : null;
      List<Integer> indexs = isSameParent(preNode, node.getParent(), nextNode);
//      System.out.println(node.getTextValue() + " : " + indexs.size() );
//      System.out.println(nextNode.getTextValue());
//      System.out.println("-----------------------------------------------");
      if(indexs.size() < 2) continue;
      i = merge(list, indexs, i);
    }
    
    return values;
  }
  
  private int merge(List<HTMLNode> list, List<Integer> indexs, int index) {
    StringBuilder builder = new StringBuilder();
    for(int i = 0; i < indexs.size(); i++) {
      if(builder.length() > 0) builder.append(' ');
      try {
        builder.append(list.get(index+indexs.get(i)).getValue());
      } catch (Exception e) {
//        System.out.println(index + " : "+ indexs.get(i));
      }
    }
    HTMLNode node = list.get(index+indexs.get(0));
    node.setValue(builder.toString().toCharArray());
    
    List<HTMLNode> removes = new ArrayList<HTMLNode>();
    for(int i = 1; i < indexs.size(); i++) {
      removes.add(list.get(index+indexs.get(i)));
    }
    for(int i = 0; i < removes.size(); i++) {
      list.remove(removes.get(i));
    }
    
    list.set(index+indexs.get(0), node);
    return index + indexs.get(0);
    
  }
  
  private List<Integer> isSameParent(HTMLNode... nodes) {
    List<Integer> list = new ArrayList<Integer>();
    if(nodes[0] != null && nodes[1] != null) {
      if(nodes[0].getParent() == nodes[1].getParent()) {
        list.add(-1);
        list.add(0);
      }
    } 
    
    if(nodes[1] != null && nodes[2] != null) {
      if(nodes[1].getParent() == nodes[2].getParent()) {
        if(list.size() < 1) list.add(0);
        list.add(1);
      }
    }
    return list;
  }
  
  private boolean isStyleNode(HTMLNode textNode) {
    if(textNode == null) return false;
    HTMLNode node = textNode.getParent();
    if(node == null) return false;
    switch(node.getName()) {
      case EM:
      case B : 
      case I:
      case U:
  
      case SUP:
      case SUB:
        
      case A:
//      case FONT:
        
      return true;
    }
    return false;
  }
  
}
