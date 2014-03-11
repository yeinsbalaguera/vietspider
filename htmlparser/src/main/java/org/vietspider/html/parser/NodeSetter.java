/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.html.parser;

import java.util.List;

import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.Tag;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Aug 13, 2006
 */
final class NodeSetter {
  
  private ParserService service;
  
  NodeSetter(ParserService service) {
    this.service = service;
  }
  
  final void add(NodeImpl node){
    if(node.getConfig().only()){
      set(node);
      return;
    }

    NodeImpl parent = service.getNodeCreator().getOpenParent(node.getConfig(), true);    
    if(parent  != null && 
        parent.getConfig().end() == Tag.OPTIONAL  && HTML.isEndType(node, parent.getConfig()) ) {
      service.getNodeCloser().close(parent);     
      parent = service.getNodeCreator().getOpenParent(node.getConfig(), true);   
    }  
    
    //close all older children in parent #Bug 28/11 
    List<HTMLNode> children = parent.getChildren();
    if(children.size() > 0) {
      service.getNodeCloser().close((NodeImpl)children.get(children.size() - 1));
    }

    add(parent, node);    
    if(node.getConfig().end() != Tag.FORBIDDEN){    
      if(node.isOpen()) service.getNodeCreator().getOpens().add(node);      
    }    

  }

  final HTMLNode add(NodeImpl node, NodeImpl ele){      
//    ele.setParent(node);
    node.addInternalChild(ele);
    if(ele.getConfig().end() != Tag.FORBIDDEN) return ele;
    return node;
  }

  final NodeImpl set(NodeImpl node){ 
    NodeImpl root = service.getRootNode();
    if(node.getName() == Name.HTML) return root;
    List<HTMLNode> children = root.getChildren(); 

    for(HTMLNode ele : children){
      if(ele == null) continue;
      if(ele.getConfig().name() != node.getConfig().name()) continue;
      ele.setValue(node.getValue());
      return (NodeImpl)ele;
    }

    if(node.getName() == Name.BODY){
      add(root, node);
      service.getNodeCreator().getOpens().add(1, node);  
      return node;
    }

    root.addInternalChild(0, node);
//    children.add(0, node);    
//    node.setInternalParent(ParserService.getRootNode());
    node.setIsOpen(false);
    return node;
  } 
}
