/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.path2;

import org.vietspider.token.attribute.Attribute;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 6, 2007  
 */
public abstract class INode<T> {
  
  protected T name;
  
  protected Attribute [] attributes;
  
  public INode(T name, Attribute [] attrs) {
    this.name = name;
    this.attributes = attrs;
  }
  
  public T getName() { return name; }
  public void setName(T name) { this.name = name; }
  
  public Attribute[] getAttributes() { return attributes; }
  public void setAttributes(Attribute[] attributes) { this.attributes = attributes; }
  
  protected abstract void buildString(StringBuilder builder);
  
  protected void buildAttributes(StringBuilder builder) {
    builder.append('[');
    for(int i = 0; i < attributes.length; i++) {
      if(i > 1) builder.append(' ');
      builder.append(attributes[i].getName()).append('=');
      builder.append('\"').append(attributes[i].getValue()).append('\"');
    }
    builder.append(']');
  }
  
  public String toString() {
    StringBuilder builder = new StringBuilder();
    buildString(builder);
    return builder.toString();
  }
  
  protected boolean equals(Attribute [] attrs) {
    if(attrs == null && attributes == null) return true;
    if(attrs == null && attributes != null) return false;
    if(attrs != null && attributes == null) return false;
   
    if(attrs.length != attributes.length) return false;
    for(int i = 0; i < attrs.length; i++) {
      if(!attrs[i].getName().equalsIgnoreCase(attributes[i].getName())) return false;
      if(!attrs[i].getValue().equalsIgnoreCase(attributes[i].getValue())) return false;
    }
    return true; 
  }
  
  @Override
  public boolean equals(Object obj) {
    if(obj instanceof Node) {
      return equals((Node)obj);
    } else if(obj instanceof NodeExp) {
      return equals((NodeExp)obj);
    }
    return equals(obj);
  }
  
  public abstract boolean equals(NodeExp nodeExp);
  
  public abstract boolean equals(AttrName attrName);
  
  public abstract boolean equals(Node node);
  
}
