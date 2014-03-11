/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.html.parser;

import java.util.Iterator;
import java.util.List;

import org.vietspider.html.HTMLNode;
import org.vietspider.html.MoveType;
import org.vietspider.html.Name;
import org.vietspider.html.NodeConfig;
/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Aug 13, 2006
 */
final class NodeCloser {
  
  private ParserService service;
  
  NodeCloser(ParserService service) {
    this.service = service;
  }

  final void close(NodeConfig config){    
    if(config.only()) return;
    
    if(config.parent().length > 0){
      NodeImpl parent = service.getNodeCreator().getOpenParent(config, false); 
      if(parent == null) return; 
      
      /*List<HTMLNode> children = parent.getChildrenNode();
      for(int j = children.size()-1; j > -1; j--){        
        if(((NodeImpl)children.get(j)).isOpen()){
          close((NodeImpl)children.get(j));         
          break;
        }
      }*/
      List<HTMLNode> children = parent.getChildren();
      if(children != null) {
        for(int j = children.size()-1; j > -1; j--){
          if(!children.get(j).isTag()) continue;
          NodeImpl childImpl = (NodeImpl)children.get(j);
          if(childImpl.isOpen()) {
            close(childImpl);         
            break;
          }
        }
      }
      
      return;
    }

    List<NodeImpl> opens = service.getNodeCreator().getOpens();
    for(int i = opens.size() - 1; i > -1; i--){
      if(opens.get(i).getConfig().name() != config.name()){
        if(opens.get(i).getConfig().block()) break;
        continue;
      }
      close(opens.get(i));
      break;
    }
  }

  final void close(NodeImpl node) {
    if(!node.isOpen()) return;
    node.setIsOpen(false);
    service.getNodeCreator().getOpens().remove(node);

//    List<HTMLNode> children = node.getChildrenNode();    
//    for(HTMLNode ele : children) close((NodeImpl)ele);
    List<HTMLNode> children = node.getChildren();
    if(children != null) {
      for(int i = 0; i < children.size(); i++) {
        NodeImpl childImpl = (NodeImpl)children.get(i);
        if(childImpl.isTag())  {
          close(childImpl);
        }
      }
    }

    NodeConfig config = node.getConfig(); 
    if(config.children().length > 0 || config.children_types().length > 0){
      Iterator<HTMLNode> iter =  node.childIterator();//node.getChildren().iterator();
      while(iter.hasNext()){
        HTMLNode child = iter.next();        
        if(HTML.isChild(node, child.getConfig())) continue;        
        iter.remove();
        if(config.move() == MoveType.INSERT) insert(node, child);
//        if(config.move() == MoveType.ADD) node.getParent().addChild(child);
      }
    }    

    if(config.move() != MoveType.HEADER) return; 
    
    NodeImpl header = null;
    if(service.getRootNode().getChildren().size() > 0){
      header = (NodeImpl)service.getRootNode().getChildren().get(0);
    }
    if(header  == null || !header.isNode(Name.HEAD)){
      header = service.createHeader();
    }
    NodeList nodeList = (NodeList)node.getParent().getChildren(); 
    nodeList.removeElement(node);
    header.addInternalChild(node);
//    node.setParent(header);
  }

  private final void insert(HTMLNode node, HTMLNode element){  
    NodeImpl parent = (NodeImpl)node.getParent();    
    List<HTMLNode> children = parent.getChildren();
    parent.addInternalChild(children.indexOf(node), element);
    /*int i = children.indexOf(node);
    if(i < 0) return;
    children.add(i, element);
    element.setParent(parent); */         
  } 

}
