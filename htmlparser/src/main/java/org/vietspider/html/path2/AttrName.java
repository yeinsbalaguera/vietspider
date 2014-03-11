/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.path2;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 15, 2011  
 */
public class AttrName extends INode<String> {
  
  public AttrName(String name){
    super(name, null);
  }

  protected void buildString(StringBuilder builder) {
    builder.append(name);
  }

  @SuppressWarnings("unused")
  public boolean equals(NodeExp nodeExp) {
    return false;
  }

  @SuppressWarnings("unused")
  public boolean equals(Node node) {
    return false;
  }

  public boolean equals(AttrName attrName) {
    return name.equalsIgnoreCase(attrName.name);
  }
  
  

}
