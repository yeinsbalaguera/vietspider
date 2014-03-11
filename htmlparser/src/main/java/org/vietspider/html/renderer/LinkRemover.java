/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.renderer;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.common.text.TextCounter;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.NodeIterator;
import org.vietspider.html.renderer.checker.CheckModel;
import org.vietspider.html.renderer.checker.LinkNodeChecker;
import org.vietspider.token.attribute.Attribute;
import org.vietspider.token.attribute.Attributes;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 17, 2009  
 */
public class LinkRemover {
  
  private  TextCounter textCounter = new TextCounter();
  private LinkNodeChecker linkNodeChecker;
  
  public LinkRemover(LinkNodeChecker linkNodeChecker) {
    this.linkNodeChecker = linkNodeChecker;
  }
  
  public void remove(HTMLNode root, HTMLNode first, HTMLNode last) {
    boolean remove = true;
    
    List<HTMLNode> removes = new ArrayList<HTMLNode>();
    NodeIterator nodeIterator = root.iterator();
//    System.out.println(new String(first.getValue()));
    while(nodeIterator.hasNext()) {
      HTMLNode node = nodeIterator.next();
      switch (node.getName()) {
      case UL:
        if(isLinkContainer(node)) removes.add(node); 
        break;
      case DIV:
      case TD:
        if(isLinkDiv(node)) removes.add(node); 
        break;
      case CONTENT:
//        System.out.println(new String(iterNode.getValue()));
        if(node == first) remove = false;
        if(remove) {
          removes.add(node);
//          System.out.println(new String(node.getValue()));
        }
        if(node == last) remove = true;
        break;
      default:
        break;
      }
    }
//    System.out.println(new String(last.getValue()));
    
    for(int i = 0; i < removes.size(); i++) {
      HTMLNode node = removes.get(i);
      HTMLNode parent = node.getParent();
//      System.out.println(parent.getTextValue());
      while(parent != null) {
//        System.out.println(parent.getTextValue());
        node.setValue(new char[]{});
        parent.removeChild(node);
        
        int word = countWord(parent);
        if(word > 15) break;
        node = parent;
        parent = node.getParent();
      }
    }
  }
  
  private int countWord(HTMLNode node) {
    if(node == null) return 0;
    NodeIterator nodeIterator = node.iterator();
    int word = 0;
    while(nodeIterator.hasNext()) {
      HTMLNode iterNode = nodeIterator.next();
      if(getAncestor(iterNode, Name.A, 0, 5) != null) continue;
      if(iterNode.isNode(Name.CONTENT)) {
        String text = iterNode.getTextValue();
        word += textCounter.countWord(text, 0, text.length());
      }
    }
    return word;
  }
  
  private HTMLNode getAncestor(HTMLNode node, Name name, int level, int max){
    if(level > max || node == null) return null;
    if(node.isNode(name)) return node;
    return getAncestor(node.getParent(), name, level+1, max);
  }
  
  private boolean isLinkContainer(HTMLNode node) {
    List<HTMLNode> children = node.getChildren();
    if(children == null) return false;
    if(isListNode(children)) {
      NodeIterator nodeIterator = node.iterator();
      int counter = 0;
      while(nodeIterator.hasNext()) {
        HTMLNode iterNode = nodeIterator.next();
        if(iterNode.isNode(Name.A)) counter++;
      }
      return counter >= children.size() - 3;
    } 
    
    for(int i = 0; i < children.size(); i++) {
      if(isLinkContainer(children.get(i))) return true;
    }
    
    return false;
  }
  
  public boolean isListNode(List<HTMLNode> nodes) {
    if(nodes.size() < 1) return false;
    HTMLNode node = nodes.get(0);
    for(int i = 1;  i < nodes.size(); i++) {
      if(!equalsFormat(node, nodes.get(i))) return false;
    }
    return true;
  }
  
  private boolean equalsFormat(HTMLNode node1, HTMLNode node2) {
    Name name1 = node1.getName();
    Name name2 = node2.getName();
    if(name1 != name2) return false;
    Attributes attributes1 = node1.getAttributes();
    Attributes attributes2 = node2.getAttributes();
    if(attributes1.size() != attributes2.size()) return false;
    for(int i = 0;  i < attributes1.size(); i++) {
      Attribute attribute1 = attributes1.get(i);
      Attribute attribute2 = attributes2.get(i);
      if(attribute1 == null && attribute2 != null)  return false;
      if(attribute1 != null && attribute2 == null)  return false;
      if(attribute1 != null && attribute2 != null 
          && !attribute1.getName().equalsIgnoreCase(attribute2.getName()))  return false;
    }
    
    List<HTMLNode> children1 = node1.getChildren();
    List<HTMLNode> children2 = node2.getChildren();
    if(children1 == null && children2 == null) return true;
    if(children1 == null && children2 != null) return false;
    if(children1 != null && children2 == null) return false;
    if(children1.size() != children2.size()) return false;
    for(int i = 0;  i < children1.size(); i++) {
      if(!equalsFormat(children1.get(i), children2.get(i))) return false;
    }
    return true;
  }
  
  private boolean isLinkDiv(HTMLNode node) {
    if(node.getChildren() == null) return false;
    
    List<HTMLNode> ignores = new ArrayList<HTMLNode>();
    NodeIterator iterator = node.iterator();
    while(iterator.hasNext()) {
      HTMLNode n = iterator.next();
      if(n.isNode(Name.A) 
          && !linkNodeChecker.isValid(new CheckModel(n), 0)) ignores.add(n);
    }
    
    int counter = 0;
    iterator = node.iterator(ignores);
    while(iterator.hasNext()) {
      HTMLNode n = iterator.next();
      if(n.isNode(Name.CONTENT)) { 
        counter += countWord(n);
      }
    }
    return counter < 5 && ignores.size() > 1;
  }
}

