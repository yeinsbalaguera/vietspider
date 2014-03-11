/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.html;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.vietspider.html.parser.HTML;
import org.vietspider.html.parser.NodeList;
import org.vietspider.token.Node;
import org.vietspider.token.attribute.AttributeParser;
import org.vietspider.token.attribute.Attributes;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Aug 3, 2006
 */
@SuppressWarnings("serial")
public abstract class HTMLNode implements Node<Name>, Serializable {

//  protected char [] value ;
  protected Name name;

  protected HTMLNode parent = null;  
  protected NodeList children ;
  
  protected Attributes attributes;
  
  protected HTMLNode( Name name){
//    this.value = value;
    this.name = name;
  }

  public abstract char[] getValue(); //{ return value;  }
  public abstract void setValue(char[] value); //{ this.value = value; }

  public NodeConfig getConfig() { 
    return HTML.getConfig(name); 
  }

  public boolean isNode(String nodeName){
    return name.toString().equalsIgnoreCase(nodeName);
  }
  public boolean isNode(Name n){ return name == n; } 
  public Name getName(){  return name ; }
  public void setName(Name name){  this.name = name; }

  public HTMLNode getParent() { return parent;  }
  
  @Deprecated()
  public void setParent(HTMLNode parent) { this.parent = parent; }
  
  public abstract void addChild(HTMLNode node);
  
  public abstract void replaceChild(HTMLNode node, List<HTMLNode> list);
  
  public abstract void addChild(int i, HTMLNode node);
  
  public abstract void setChild(int i, HTMLNode node);
  
  public HTMLNode getChild(int i) { return children.get(i); }
  
  public abstract void removeChild(HTMLNode node);
  
  public abstract void clearChildren();

  public List<HTMLNode> getChildren() { return children; }
  
  public boolean hasChildren() { return children != null ; }
  
  public int totalOfChildren() {
    return children == null ? 0 : children.size(); 
  }

//   Deprecated()
  public List<HTMLNode> getChildrenNode(){
    List<HTMLNode> list = new LinkedList<HTMLNode>();
    if(children  == null) return list;
    for(HTMLNode child : children){
      if(child.isTag()) list.add(child);
    }
    return list;
  } 

  public String getTextValue(){
    StringBuilder builder = new StringBuilder();
    buildValue(builder);
    return builder.toString();
  }  
  
  public int indexOfChild(HTMLNode node) {
    for(int i = 0; i < children.size(); i++) {
      if(children.get(i) == node) return i;
    }
    return -1;
  }
  
  public Attributes getAttributes() {
    if(attributes == null) {
      attributes = AttributeParser.getInstance().get(this);
    }
    return attributes;
  }
  
  abstract public StringBuilder buildValue(StringBuilder builder);
  
//  abstract public void buildTokens(List<HTMLNode> tokens) ;
  
  public String toString() { return new String(getValue());  }
  
  abstract public void clone(HTMLNode nodeParent);

  abstract public void traverse(NodeChildHandler handler);
  
  abstract public NodeIterator iterator() throws Error;
  
  abstract public NodeIterator iterator(List<HTMLNode> ignores);
  
}
