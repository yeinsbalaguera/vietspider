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
public class NodeExp extends INode<Name> {
  
  private String pattern;
  
  public NodeExp(Name name, String pattern, Attribute [] attributes) {
    super(name, attributes);
    this.pattern = pattern;
  }

  public String getPattern() { return pattern; }

  public void setPattern(String pattern) { this.pattern = pattern; }
  
  protected void buildString(StringBuilder builder) {
    builder.append(name.toString()).append('[').append(pattern).append(']');
    if(attributes != null && attributes.length > 0) buildAttributes(builder);
  }
  
  public boolean equals(NodeExp nodeExp) {
    return name == nodeExp.getName() 
            && nodeExp.getPattern().equals(pattern)
            && equals(nodeExp.getAttributes());
  }

  public boolean equals(Node node) {
    NodeMatcher matcher = new NodeMatcher();
    return name == node.getName() 
            && matcher.match(pattern, node.getIndex())
            && equals(node.getAttributes());
  }
  
  @SuppressWarnings("unused")
  public boolean equals(AttrName attrName) {
    return false;
  }
  
}
