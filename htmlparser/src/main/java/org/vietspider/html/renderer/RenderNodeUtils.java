/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.renderer;

import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.token.attribute.Attribute;
import org.vietspider.token.attribute.Attributes;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 9, 2009  
 */
public class RenderNodeUtils {
  
  public static int getIntAttrValue(HTMLNode node, String name) {
    Attributes attributes = node.getAttributes(); 
    return getIntAttrValue(attributes, name);
  }
  
  public static int getIntAttrValue(Attributes attributes, String name) {
    return getIntAttrValue(attributes.get(name));
  }
  
  public static int getIntAttrValue(Attribute attribute) {
    if(attribute == null) return -1;
    try {
      return Integer.parseInt(attribute.getValue().trim());
    } catch (Exception e) {
      return -1;
    }
  }
  
  public static HTMLNode getAncestor(HTMLNode node, Name name_, int level, int max){
    if(level > max || node == null) return null;
    if(node.isNode(name_)) return node;
    return getAncestor(node.getParent(), name_, level+1, max);
  }

  
}
