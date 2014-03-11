/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.html.parser;

import java.util.Iterator;
import java.util.List;

import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.NodeConfig;
import org.vietspider.token.TypeToken;
/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Aug 3, 2006
 */
final class DOMParser { 
  
  private ParserService service;
  
  DOMParser(ParserService service) {
    this.service = service;
  }
  
  final void parse(HTMLDocument document, CharsToken tokens) {    
    if(!tokens.hasNext()) return;
    NodeImpl temp = tokens.pop();    
    
    NodeCreator creator = service.getNodeCreator();
    NodeSetter setter = service.getNodeSetter();
    NodeCloser closer = service.getNodeCloser();
    
    while(temp != null){
      if(temp.isNode(Name.DOCTYPE)) {
        document.setDoctype(temp);
        temp = tokens.pop();
        continue;
      }
      
      NodeConfig config = temp.getConfig();
      
      if(config.hidden()) {
        setter.add(creator.getLast(), temp);
      } else if(temp.getType() == TypeToken.CLOSE) {
        closer.close(config);
      } else if(temp.getType() == TypeToken.TAG) {
        setter.add(temp);      
      } else {
        setter.add(creator.getLast(), temp);
      }
      
      temp = tokens.pop();
    }   
    
    move(service.getRootNode());
    closer.close(service.getRootNode());   
  } 

  /*final void parse(org.vietspider.common.util.Iterator<NodeImpl> iterator) {    
//    org.vietspider.token.TokenParser.Factory.Iterator<NodeImpl> iterator = tokens.iterator();
    if(!iterator.hasNext()) return;
    NodeImpl temp = iterator.next();    
    
    NodeCreator creator = ParserService.getNodeCreator();
    NodeSetter setter = ParserService.getNodeSetter();
    NodeCloser closer = ParserService.getNodeCloser();
    
    while(iterator.hasNext()){
      NodeConfig config = temp.getConfig();
      
      if(config.hidden()) {
        setter.add(creator.getLast(), temp);
      } else if(temp.getType() == TypeToken.CLOSE) {
        closer.close(config);
        iterator.remove();
      } else if(temp.getType() == TypeToken.TAG) {
        setter.add(temp);      
      } else {
        setter.add(creator.getLast(), temp);
      }
      
      temp = iterator.next(); 
    }   
    
    move(ParserService.getRootNode());
    closer.close(ParserService.getRootNode());   
  } */

  private final void move(HTMLNode root){
    List<HTMLNode> children = root.getChildren();
    if(children == null || children.size() < 1) return;
    NodeImpl head = null ;
    NodeImpl body = null;
    for(HTMLNode child : children){
      if(child.isNode(Name.HEAD)) head = (NodeImpl)child;
      if(child.isNode(Name.BODY)) body = (NodeImpl) child;
    }
    if(head == null) head = service.createHeader();      
    if(body == null) body = service.createBody();
    
    Iterator<HTMLNode> iter = children.iterator();
    while(iter.hasNext()){
      HTMLNode ele = iter.next();
      if(ele.isNode(Name.HEAD) || ele.isNode(Name.BODY)) continue;
      if(ele.isNode(Name.SCRIPT)){
        head.addInternalChild(ele);
//        ele.setParent(head);
      } else {
        body.addInternalChild(ele);
//        ele.setParent(body);
      }
      iter.remove();
    }
  }
  
}
