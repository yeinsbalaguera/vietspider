/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.plugin.desc;

import java.util.List;

import org.vietspider.html.HTMLNode;
import org.vietspider.html.util.NodeHandler;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 3, 2008  
 */
abstract class NodeUtils {
  
  protected NodeHandler nodeHandler;
  
  public NodeUtils(NodeHandler nodeHandler) {
    this.nodeHandler = nodeHandler;
  }
  
  void removeNodes(List<HTMLNode> contents, List<HTMLNode> removes) {
    for(int i = 0; i < removes.size(); i++) {
      HTMLNode node = removes.get(i);
      removeNode(node);
      nodeHandler.removeContent(node.iterator(), contents);
    }
  }
  
  void removeNode(HTMLNode node) {
    HTMLNode parent = node.getParent();
    if(parent == null) return;
    parent.removeChild(node);
//    parent.getChildren().remove(node);
    if(parent.totalOfChildren() < 1) removeNode(parent);
  }
}
