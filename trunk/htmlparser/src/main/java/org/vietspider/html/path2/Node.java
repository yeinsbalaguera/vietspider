/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.path2;

import org.vietspider.html.Name;
import org.vietspider.token.attribute.Attribute;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 6, 2007  
 */
public class Node extends INode<Name> {
  
  private int index;
  
  public Node(Name name, int index, Attribute [] attributes){
    super(name, attributes);
    this.index = index; 
  }

  public int getIndex() { return index; }
  
  public void setIndex(int idx) { this.index = idx; }
  
  protected void buildString(StringBuilder builder) {
    builder.append(name.toString()).append('[').append(index).append(']');
    if(attributes != null && attributes.length > 0) buildAttributes(builder);
  }
  
  public boolean equals(Node node) {
    return name == node.getName() && node.getIndex() == index && equals(node.getAttributes());
  }
  
  public boolean equals(NodeExp nodeExp) {
    NodeMatcher matcher = new NodeMatcher();
    return name == nodeExp.getName() 
            && matcher.match(nodeExp.getPattern(), index)
            && equals(nodeExp.getAttributes());
  }
  
  @SuppressWarnings("unused")
  public boolean equals(AttrName attrName) {
    return false;
  }
  
}