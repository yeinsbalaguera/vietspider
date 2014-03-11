/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.util;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.vietspider.chars.ValueVerifier;
import org.vietspider.html.HTMLNode;
import org.vietspider.token.attribute.Attribute;
import org.vietspider.token.attribute.Attributes;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 14, 2008  
 */
public class MapAttributeHandler extends AttributeHandler {
  
  private Map<String, String> map;
  
  public MapAttributeHandler(List<String> list, ValueVerifier verifier, Map<String, String> map) {
    super(list, verifier);
    this.map = map;
  }
  
  public Attribute getAttribute(HTMLNode node) {
    Set<String> keys = map.keySet();
    Iterator<String> iter = keys.iterator();
    while(iter.hasNext()){
      String key = iter.next();
      if(node.isNode(key) || (key.length() == 1 && key.charAt(0) == '*')){
        Attributes attrs = node.getAttributes();   
        int idx = attrs.indexOf(map.get(key));
        if(idx > -1) return attrs.get(idx);
      }
    }
    return null;
  }
  
  @SuppressWarnings("unused")
  public void handleNode(HTMLNode node) {
    
  }
  
}
