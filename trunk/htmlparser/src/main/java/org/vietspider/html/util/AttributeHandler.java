/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.util;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.chars.ValueVerifier;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.NodeIterator;
import org.vietspider.token.attribute.Attribute;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 14, 2008  
 */
public abstract class AttributeHandler {
  
  protected List<String> list;
  protected ValueVerifier verifier;
  
  public AttributeHandler(List<String> list_, ValueVerifier verifier) {
    this.list = list_;
    this.verifier = verifier;
    if(this.list == null) this.list = new ArrayList<String>();
  }

  public List<String> getValues() { return list; } 
  
  public void handle(HTMLNode node) {
    if(node == null) return;
    NodeIterator iterator = node.iterator();
    while(iterator.hasNext()) {
      HTMLNode n = iterator.next();
      
      handleNode(n);
      
//      System.out.println(new String(n.getValue()));
      
      if(!n.isTag()) continue;
      Attribute attr = getAttribute(n); 
      if(attr == null) continue;
//      System.out.println(attr.getName() +  " : " + attr.getValue());
      String attrValue = attr.getValue();
      if(attrValue == null) continue;
      if (verifier == null || verifier.verify(attrValue)) list.add(attrValue);
    }
  }
  
  abstract void handleNode(HTMLNode node);
  
  abstract Attribute getAttribute(HTMLNode node);

  
}
