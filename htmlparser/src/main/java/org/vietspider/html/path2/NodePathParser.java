/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.html.path2;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.token.attribute.Attribute;
import org.vietspider.token.attribute.AttributeParser;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Aug 15, 2006
 */
public class NodePathParser {
  
  private final static char START_VALUE = '[';
  private final static char END_VALUE = ']';
  
//  private final static String NODE_SEPARATOR = "\\.";
  
  public NodePath [] toNodePath(String[] paths) throws Exception {
    NodePath [] values = new NodePath[paths.length];
    for(int i = 0; i < paths.length; i++) {
      values[i] = toPath(paths[i]);
    }
    return values;
  }
  
  public NodePath toPath(String text) throws Exception{
    return new NodePath(toNodes(text));
  }
  
  public INode<?>[] toNodes(String value) throws Exception {
    int i = 0;
    int start = 0;
    List<INode<?>> list = new ArrayList<INode<?>>();
    while(i < value.length()) {
      if(i > 0 && value.charAt(i) == '.') {
        list.add(toNode(value.substring(start, i)));
        start = i+1;
      }
      i++;
    }
    if(start < value.length()) list.add(toNode(value.substring(start, i)));
    return list.toArray(new INode[list.size()]);
  }
  
  public NodePath toPath(HTMLNode htmlNode) {
    return new NodePath(toNodes(htmlNode));
  }
  
  public Node[] toNodes(HTMLNode htmlNode) {
    if(htmlNode == null) return new Node[0];
    HTMLNode htmlParent = htmlNode.getParent();
    List<Node> list = new ArrayList<Node>();
    while(htmlParent != null){
      list.add(toNode(htmlParent, htmlNode));
      htmlNode = htmlParent;
      htmlParent = htmlNode.getParent();
    }

    Node [] nodes = new Node[list.size()];
    for(int i = list.size() - 1; i > -1; i--) {
      nodes[list.size() - i - 1] = list.get(i);
    }
    return nodes;
  }
  
  private Node toNode(HTMLNode htmlParent, HTMLNode htmlNode){
    List<HTMLNode> htmlChildren  = htmlParent.getChildren();
    int counter = 0;
    Name name = htmlNode.getName();
    for(int i = 0; i < htmlChildren.size(); i++) {
      if(htmlChildren.get(i) == htmlNode) break;
      if(htmlChildren.get(i).getName() == name) counter++;
    }
    return new Node(htmlNode.getName(), counter, null);
  }
  
  private INode<?> toNode(String element) throws Exception {
    int bracketStart = element.indexOf(START_VALUE);
    int bracketEnd  = element.indexOf(END_VALUE);
    
    if(bracketStart < 0 && bracketEnd < 0
        && !"BODY".equalsIgnoreCase(element)
        && !"HEAD".equalsIgnoreCase(element)) {
//      System.out.println(element);
      return new AttrName(element.toUpperCase());
    }
    
    if(bracketStart < 0 || bracketEnd < 0) {
      try {
        return new Node(Name.valueOf(element.toUpperCase()), 0, null);
      } catch (Exception e) {
//        System.out.println(" =================  >" + element);
        return new AttrName(element.toUpperCase());
      }
    }
    
    String value = element.substring(0, bracketStart).trim();
    String index = element.substring(bracketStart+1, bracketEnd).trim();
    bracketStart = element.indexOf(START_VALUE, bracketEnd);
    bracketEnd  = bracketStart < 1 ? -1 : element.indexOf(END_VALUE, bracketStart);
    
    Attribute [] attributes = null;
    if(bracketStart > -1 && bracketEnd > -1) {
      String attr = element.substring(bracketStart+1, bracketEnd).trim();
      attributes = AttributeParser.getInstance().get(attr);
    }
    
    boolean anyNode = value.length() == 1 && value.charAt(0) == ExpComputor.C_ANY; 
    value = value.toUpperCase();
    Name name = anyNode ? Name.ANY : Name.valueOf(value);
    if(attributes != null) {
      return new NodeExp(name, index.trim(), attributes); 
    } else if(isNumber(index)) {
      return new Node(name, Integer.parseInt(index), attributes);
    }
    return new NodeExp(name, index.trim(), attributes);
  }
  
  private boolean isNumber(String value) {
    int i = 0;
    while(i < value.length()) {
      if(!Character.isDigit(value.charAt(i))) return false;
      i++;
    }
    return true;
  }
  
}
