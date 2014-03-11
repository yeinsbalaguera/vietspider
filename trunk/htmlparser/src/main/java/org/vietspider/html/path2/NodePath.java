/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.html.path2;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Aug 15, 2006
 */
public class NodePath {

  protected INode<?> [] nodes;
  
  public NodePath(INode<?>[] nodes) {
    this.nodes = nodes;
  }
  
  public INode<?>[] getNodes() { return nodes;   }
   
  public String toString(){
    StringBuilder builder = new StringBuilder();
    for(INode<?> node : nodes){
      if(builder.length()> 0) builder.append('.');
      node.buildString(builder);
    }
    return builder.toString();
  }
  
  public boolean equals(NodePath nodePath) {
    INode<?> [] newINodes = nodePath.getNodes();
    if(newINodes.length != nodes.length) return false;
    for(int i = 0; i < nodes.length; i++) {
      if(!nodes[i].equals(newINodes[i])) return false;
    }
    return true;
  }
  
}
