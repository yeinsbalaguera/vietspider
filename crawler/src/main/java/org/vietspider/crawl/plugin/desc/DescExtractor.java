/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.plugin.desc;

import java.util.List;

import org.vietspider.db.SystemProperties;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.path2.HTMLExtractor;
import org.vietspider.html.util.NodeHandler;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 30, 2008  
 */
public abstract class DescExtractor extends NodeUtils {
  
  protected HTMLExtractor extractor;
  protected boolean remove = true;
  
  public DescExtractor(HTMLExtractor extractor, NodeHandler nodeHandler) {
    super(nodeHandler);
    this.extractor = extractor;
    SystemProperties system = SystemProperties.getInstance();
    remove = "true".equals(system.getValue("desc.extractor.remove"));
  }
  
  public boolean isRemove() { return remove; }
  
  /*public void removeContentsNode(HTMLNode parent) {
    List<HTMLNode> children = null;
    if(parent == null || (children = parent.getChildren()) == null) return ;
    List<HTMLNode> removeNodes = new LinkedList<HTMLNode>();
    for(HTMLNode node : children) {
      if(node.isNode(Name.CONTENT)) {
        removeNodes.add(node);
        continue;
      } else if (node.isNode(Name.SCRIPT) || 
          node.isNode(Name.STYLE) || node.isNode(Name.COMMENT)) continue;
      removeContentsNode(node);
    }
    
    for(HTMLNode removeNode : removeNodes) {
      nodeHandler.removeNode(removeNode);
    }
  }*/
  
  /*protected StringBuilder buildContent(StringBuilder builder, List<HTMLNode> contents, HTMLNode node) {
    NodeIterator iterator = node.iterator(); 
    while(iterator.hasNext()) {
      HTMLNode n = iterator.next();
      if(n.isNode(Name.CONTENT) && contents.contains(n)) {
        builder.append(' ').append(n.getValue());
        return builder;
      }
    }
    return builder;
    
//    if(node.isNode(Name.CONTENT) && contents.contains(node)) {
//      builder.append(' ').append(node.getValue());
//      return builder;
//    }
//    if(node.isNode(Name.SCRIPT) 
//        || node.isNode(Name.STYLE) || node.isNode(Name.UNKNOWN)) return builder;
//    List<HTMLNode> children  = node.getChildren();
//    if(children == null) return builder;
//    for(HTMLNode ele : children) buildContent(builder, contents, ele);
//    return builder;
  }*/

  /*protected HTMLNode upParentContentNode(HTMLNode node) {
    if(nodeHandler.count(new String(node.getValue())) > 20) return node;
    HTMLNode parent = node.getParent();
    if(parent == null) return node;
    if(isStyleNode(parent)) return upParentContentNode(parent);
    return isStyleNode(node) ? parent : node;
  }*/
  
  abstract public String extract(HTMLNode root, List<HTMLNode> contents);
  
  /*protected boolean isStyleNode(HTMLNode node) {
    switch(node.getName()) {
      case EM:
      case B : 
      case I:
      case U:
  
      case SUP:
      case SUB:
        
      return true;
    }
    return false;
  }*/
}
