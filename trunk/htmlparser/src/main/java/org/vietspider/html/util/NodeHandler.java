/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.NodeIterator;
import org.vietspider.token.attribute.Attribute;
import org.vietspider.token.attribute.AttributeParser;
import org.vietspider.token.attribute.Attributes;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 9, 2007  
 */
public final class NodeHandler {
  
  private Pattern wordPattern = Pattern.compile("\\b[\\p{L}\\p{Digit}]");
  
  public void removeNode(HTMLNode node) {
    if(node == null) return;
    HTMLNode parent = node.getParent();
    if(parent == null || !parent.hasChildren()) return ;
    parent.removeChild(node);
//    node.setParent(null);
//    node.setValue(new char[]{});
  }
  
//  public StringBuilder buildContent(StringBuilder builder, HTMLNode node) {
//    NodeIterator iterator = node.iterator();
//    while(iterator.hasNext()) {
//      HTMLNode n = iterator.next();
//      if(n.isNode(Name.CONTENT)) {
//        builder.append(' ').append(node.getValue());
//      }
//    }
//    return builder;
  
//    if(node.isNode(Name.CONTENT)) {
//      builder.append(' ').append(node.getValue());
//      return builder;
//    }
//    if(node.isNode(Name.SCRIPT) 
//        || node.isNode(Name.STYLE) || node.isNode(Name.UNKNOWN)) return builder;
//    List<HTMLNode> children  = node.getChildren();
//    if(children == null) return builder;
//    for(HTMLNode ele : children) buildContent(builder, ele);
//    return builder;
//  }
  
  public void removeContent(NodeIterator iterator, List<HTMLNode> contents) {
    while(iterator.hasNext()) {
      HTMLNode n = iterator.next();
      if(n.isNode(Name.CONTENT) && contents.contains(n)) {
        contents.remove(n);
      }
    }
//    if(node.isNode(Name.CONTENT) && contents.contains(node)) {
//      contents.remove(node);
//      return;
//    }
//    if(node.isNode(Name.SCRIPT) 
//        || node.isNode(Name.STYLE) || node.isNode(Name.UNKNOWN)) return;
//    List<HTMLNode> children  = node.getChildren();
//    if(children == null) return;
//    for(HTMLNode ele : children) {
//      removeContent(ele, contents);
//    }
  }
  
 //check isStyleNode(node) ? parent : node;
  public HTMLNode upParentContentNode(HTMLNode node) {
    HTMLNode parent = node.getParent();
    if(parent == null) return node;
    if(isStyleNode(parent)) return upParentContentNode(parent);
    return isStyleNode(node) ? parent : node;
  }
  
  protected boolean isStyleNode(HTMLNode node) {
    switch(node.getName()) {
    case EM:
    case STRONG:
    case CITE:
    case DFN:
    case CODE:
    case SAMP:
    case KBD:
    case VAR:
    case ABBR:
    case ACRONYM:

    case SUB:
    case SUP:

    case BIG:
    case B : 
    case I:
    case SMALL:
    case TT:
    case STRIKE:
    case S:
    case U:

    case FONT:
    case BASEFONT:

    case INS:
    case DEL:
      return true;
    }
    return false;
  }
  
  public int count(CharSequence charSeq){
    int start = 0;
    int counter = 0;
    Matcher matcher = wordPattern.matcher(charSeq);
    while(matcher.find(start)) {
      start = matcher.start() + 1;
      counter++;
    }
    return counter;
  }
  
  /*public void searchTextNode(HTMLNode node, final List<HTMLNode> contents){
    NodeIterator iterator = node.iterator();
    while(iterator.hasNext()) {
      HTMLNode n = iterator.next();
      if(!n.isNode(Name.CONTENT)) continue;
      if(!isEmpty(n.getValue())) contents.add(n);
    }
  }*/
  
  /*public List<HTMLNode> searchTextNode(HTMLNode root, List<HTMLNode> contents){
    if(root == null) return contents;
    if(root.isNode(Name.SCRIPT) 
        || root.isNode(Name.STYLE) || root.isNode(Name.UNKNOWN)) return contents;
    
    if(root.isNode(Name.CONTENT)) {
      if(!isEmpty(root.getValue())) contents.add(root);
      return contents;
    }
    
    List<HTMLNode> childen = root.getChildren();
    if (childen == null)  return  contents;
    for(int i = 0; i < childen.size(); i++) {
      searchTextNode(childen.get(i), contents);      
    }
    return contents;
  }*/
  
  /*public List<HTMLNode> searchTextNode(HTMLDocument document, List<HTMLNode> contents) {
    CharsToken tokens = document.getTokens();
    Iterator<NodeImpl> iterator = tokens.iterator();
    while(iterator.hasNext()) {
      HTMLNode node = iterator.next();
      if(!node.isNode(Name.CONTENT)) continue;
      if(!isEmpty(node.getValue())) contents.add(node);
    }  
    return contents;
  }*/
  
  /*public List<HTMLNode> searchTextNode(List<NodeImpl> nodes, List<HTMLNode> contents){
    if(nodes == null) return contents;
    
    for(int i = 0; i < nodes.size(); i++) {
      HTMLNode node = nodes.get(i);
      if(node.isNode(Name.CONTENT) 
          && !isEmpty(node.getValue())) contents.add(node);
    }
    return contents;
  }*/
  
  public HTMLNode searchImageNode(NodeIterator iterator){
    while(iterator.hasNext()) {
      HTMLNode n = iterator.next();
      if(n.isNode(Name.IMG)) return n;
    }
    return null;
//    if(node == null) return null;
//    if(node.isNode(Name.IMG)) return node;
//    
//    if(node.isNode(Name.SCRIPT)
//        || node.isNode(Name.CONTENT)
//        || node.isNode(Name.STYLE) 
//        || node.isNode(Name.UNKNOWN)) return null;
//    
//    
//    List<HTMLNode> childen = node.getChildren();
//    if (childen == null)  return null;
//    for(HTMLNode ele : childen) {
//      HTMLNode value = searchImageNode(ele);
//      if(value != null) return value;
//    }
//    return null;
  }
  
  public void searchNodes(NodeIterator iterator, List<HTMLNode> nodes, Name name) {
    while(iterator.hasNext()) {
      HTMLNode n = iterator.next();
      if(n.isNode(name)) nodes.add(n);
    }
//    if(root.isNode(name)) {
//      nodes.add(root);
//      return nodes;
//    }
//    List<HTMLNode> children = root.getChildren();
//    if(children == null) return nodes;
//    for(HTMLNode ele : children) {
//      searchNodes(ele, nodes, name);
//    }
//    return nodes;
  }
  
  public boolean isShortContents(List<HTMLNode> nodes, int length){
    int count  = 0;
    for(HTMLNode node : nodes) {
      count += count(new String(node.getValue()));
      if(count >= length) return false;
    }
//    System.out.println(count + " : "+ min);
    return count <= length;
  }
  
  /*boolean isEmpty(char [] chars) {
    for(char ele : chars) {
      if(Character.isWhitespace(ele) || Character.isSpaceChar(ele)) continue;
      return false;
    }
    return true;
  }*/
  
  public List<HTMLNode> clone(List<HTMLNode> list) {
    List<HTMLNode> newList = new ArrayList<HTMLNode>();
    for(int i = 0; i < list.size(); i++) {
      newList.add(list.get(i));
    }
    return newList;
  }
  
  public void replaceNode(HTMLNode oldNode, HTMLNode newNode) {
    HTMLNode parent = oldNode.getParent();
    if(parent == null) return ;
    
    int index = parent.indexOfChild(oldNode);
    if(index  > -1) parent.setChild(index, newNode);
//    newNode.setParent(parent);
  }
  
  public void searchImageNodes(NodeIterator iterator, List<HTMLNode> images) {
    while(iterator.hasNext()) {
      HTMLNode n = iterator.next();
      if(n.isNode(Name.IMG)) images.add(n);
      else if (n.isNode(Name.A)) {
        Attributes attributes = AttributeParser.getInstance().get(n);
        Attribute attr = attributes.get("href");
        if(attr == null) continue;
        String href = attr.getValue();
        if(href == null) continue;
        if(href.endsWith(".jpg")
            || href.endsWith(".gif")
            || href.endsWith(".jpeg")
            || href.endsWith(".png")
            || href.endsWith(".bmp")) {
          images.add(n);
        }
        
      }
    }
//    if(node.isNode(Name.IMG)) {
//      images.add(node);
//      return images;
//    }
//    
//    if(node.isNode(Name.SCRIPT) || 
//        node.isNode(Name.CONTENT) || 
//        node.isNode(Name.STYLE) || 
//        node.isNode(Name.UNKNOWN) || node.isNode(Name.COMMENT)) return images;
//    
//    List<HTMLNode> children  = node.getChildren();
//    if(children == null) return images;
//    for(HTMLNode child : children) {
//      searchImageNodes(child, images);
//    }
//    
//    return images;
  }
  
  public String getCharset(HTMLDocument document) {
    HTMLNode root = document.getRoot();
    NodeIterator iterator = root.iterator();
    while(iterator.hasNext()) {
      HTMLNode n = iterator.next();
      if(!n.isNode(Name.META)) continue;
      if(n.isNode(Name.BODY)) break;
      Attributes attributes = n.getAttributes();
      Attribute attribute = attributes.get("http-equiv");
      if(attribute == null || attribute.getValue() == null) continue;

      if(!"content-type".equalsIgnoreCase(attribute.getValue().trim())) continue ;

      attribute = attributes.get("content");
      if(attribute == null) continue;
      String link = attribute.getValue();
      if(link == null) continue;
      int index = link.toLowerCase().indexOf("=");

      return link.substring(index+1);
    }
    return null;
  }

  public Pattern getWordPattern() { return wordPattern; }
  
 /* public static void main(String[] args) {
    NodeHandler handler = new NodeHandler();
    String value = " .  ";
    System.out.println(handler.count(value));
    
  }*/
  
}
