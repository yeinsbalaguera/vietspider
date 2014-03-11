/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.renderer.extractor;

import java.util.List;

import org.vietspider.html.HTMLNode;
import org.vietspider.html.renderer.checker.CheckModel;
import org.vietspider.html.renderer.checker.NodeChecker;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 9, 2009  
 */
public class WebPageFilter extends WebExtractHandler {

  public void filter(HTMLNode node, List<NodeChecker> checkers) {
    int maxLevel = 0;
    for(int i = 0 ; i < checkers.size(); i++) {
      int level = checkers.get(i).getLevel();
      if(level > maxLevel) maxLevel = level; 
    }
    filter(node, checkers, maxLevel);
  }

  public void filter(HTMLNode node, List<NodeChecker> checkers, int max) {
    //System.out.println(new String(node.getValue()));
    CheckModel model = new CheckModel(node);

    int level = 0; 
    while(level <= max) {
      for(int i = 0 ; i < checkers.size(); i++) {
        if(!checkers.get(i).isValid(model, level)) {
          if(model.getRemoveNode().getTextValue().indexOf("XUÂN YÊU") > -1) { 
            System.out.println("===================================================");
            System.out.println(checkers.get(i).getClass());
//            System.out.println(node.getTextValue());
          }
          removeNode(model.getRemoveNode());
          return;
        }
      }
      level++;
    }

    List<HTMLNode> children = node.getChildren();
    if(children == null) return;
    HTMLNode[] nodes = new HTMLNode[children.size()];
    for(int i = 0; i < children.size(); i++) {
      nodes[i] = children.get(i);
    }

    for(int i = 0; i < nodes.length; i++) {
      filter(nodes[i], checkers);
    }
  }

}
