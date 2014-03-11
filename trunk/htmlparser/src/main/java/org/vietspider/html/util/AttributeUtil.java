/***************************************************************************
 * Copyright 2001-2003 The VietSpider Studio        All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.vietspider.html.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.vietspider.chars.ValueVerifier;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.token.attribute.Attribute;
import org.vietspider.token.attribute.Attributes;
/**
 * Created by VietSpider Studio
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 8, 2006
 */
@Deprecated()
public class AttributeUtil {
  
  public List<String> getAttributes(HTMLNode node, Map<String, String> map) {
    return getAttributes(node, null, map, null);   
  }
  
  public List<String> getAttributes(HTMLNode node, Map<String, String> map, ValueVerifier verifier) {
    return getAttributes(node, null, map, verifier);   
  }
  
  public String getAttribute(HTMLNode node, Map<String, String> map, ValueVerifier verifier) {   
    Attribute attr = getAttribute(node, map);    
    if(attr != null
      && (verifier == null || verifier.verify(attr.getValue()))) return  attr.getValue();
    
    List<HTMLNode> children = node.getChildrenNode();
    for(int i = 0; i < children.size(); i++) {
      String link = getAttribute(children.get(i), map, verifier);
      if(link != null) return link;
    }
    return null;
  }  
  
  public String getAttribute(HTMLNode node, String nodeName, String attrName, ValueVerifier verifier) {   
    Attribute attr = getAttribute(node, nodeName, attrName);    
    if(attr != null 
      && (verifier == null || verifier.verify(attr.getValue()))) return attr.getValue();
       
    List<HTMLNode> children = node.getChildrenNode();
    for(int i = 0; i < children.size(); i++) {
      String link = getAttribute(children.get(i), nodeName, attrName, verifier);
      if(link != null) return link;
    }
    return null;
  }
  
  public List<String> getAttributes(HTMLNode node, List<String> list, 
                                    Map<String, String> map, ValueVerifier verifier){   
    if(list == null) list  = new ArrayList<String>();
    Attribute attr = getAttribute(node, map);    
    if(attr != null
        && (verifier == null || verifier.verify(attr.getValue()))) list.add(attr.getValue());
    
    List<HTMLNode> children = node.getChildrenNode();
    for(int i = 0; i < children.size(); i++) {
      getAttributes(children.get(i), list, map, verifier);
    }
    return list;
  } 
  
  public List<String> getAttributes(HTMLNode node, List<String> list, 
                        String nodeName, String attrName, ValueVerifier verifier){   
    if(list == null) list  = new ArrayList<String>();
    Attribute attr = getAttribute(node, nodeName, attrName);    
    if(attr != null
       && (verifier == null || verifier.verify(attr.getValue()))) list.add(attr.getValue());
    
    List<HTMLNode> children = node.getChildrenNode();
    for(int i = 0; i < children.size(); i++) {
      getAttributes(children.get(i), list, nodeName, attrName, verifier);       
    }
    
    return list;
  } 
  
  
  public List<String> getAttributes(HTMLDocument document, List<String> list, 
                                    Map<String, String> map, ValueVerifier verifier){   
//    if(list == null) list  = new ArrayList<String>();
//    CharsToken tokens = document.getTokens();
//    org.vietspider.common.util.Iterator<NodeImpl> iterator = tokens.iterator();
//    
//    while(iterator.hasNext()) {
//      NodeImpl nodeImpl = iterator.next();
//      int type = nodeImpl.getType();
//      if(type != TypeToken.TAG && type != TypeToken.SINGLE) continue;
//      Attribute attr = getAttribute(nodeImpl, map);    
//      if(attr != null
//          && (verifier == null || verifier.verify(attr.getValue()))) list.add(attr.getValue());
//    }
    
    return list;
  } 
  
  public List<String> getAttributes(HTMLDocument document, List<String> list, 
                                    String nodeName, String attrName, ValueVerifier verifier) {
    
    if(list == null) list  = new ArrayList<String>();
    
//    CharsToken tokens = document.getTokens();
//    org.vietspider.common.util.Iterator<NodeImpl> iterator = tokens.iterator();
//    
//    while(iterator.hasNext()) {
//      NodeImpl nodeImpl = iterator.next();
//      int type = nodeImpl.getType();
//      if(type != TypeToken.TAG && type != TypeToken.SINGLE) continue;
//      Attribute attr = getAttribute(nodeImpl, nodeName, attrName);  
//      if(attr != null
//          && (verifier == null || verifier.verify(attr.getValue()))) list.add(attr.getValue());
//    }
    
    return list;
  } 
  
  public Attribute getAttribute(HTMLNode node, String nodeName, String attrName){
    if(node.isNode(nodeName) || (nodeName.length() == 1 && nodeName.charAt(0) == '*')){
      Attributes attrs = node.getAttributes();   
      int idx = attrs.indexOf(attrName);
      if(idx > -1) return attrs.get(idx);
    }
    return null;
  }
  
  public Attribute getAttribute(HTMLNode node, Map<String, String> map){
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
  
}
