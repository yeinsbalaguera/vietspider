/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.util;

import java.util.List;

import org.vietspider.chars.ValueVerifier;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.NodeIterator;
import org.vietspider.token.attribute.Attribute;
import org.vietspider.token.attribute.Attributes;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 14, 2008  
 */
public class IdentifierAttributeHandler extends AttributeHandler {
  
  private String nodeName;
  private String attrName;
  
  public IdentifierAttributeHandler(List<String> list, 
      ValueVerifier verifier, String nodeName, String attrName) {
    super(list, verifier);
    this.nodeName = nodeName;
    this.attrName = attrName;
  }
  
  public Attribute getAttribute(HTMLNode node){
    if(node.isNode(nodeName) || (nodeName.length() == 1 && nodeName.charAt(0) == '*')){
      Attributes attrs = node.getAttributes();   
      int idx = attrs.indexOf(attrName);
      if(idx > -1) return attrs.get(idx);
    }
    return null;
  }
  
  public String getAttributeValue(HTMLNode node) {   
    NodeIterator iterator = node.iterator();
    while(iterator.hasNext()) {
      Attribute attr = getAttribute(iterator.next());    
      if(attr != null 
        && (verifier == null || verifier.verify(attr.getValue()))) return attr.getValue();  
    }
    return null;
  }
  
  @SuppressWarnings("unused")
  public void handleNode(HTMLNode node) {
    
  }
  
}
