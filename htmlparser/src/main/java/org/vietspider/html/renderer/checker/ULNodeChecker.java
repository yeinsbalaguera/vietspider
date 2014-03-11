/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.renderer.checker;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.NodeIterator;
import org.vietspider.html.renderer.NodeComparator;
import org.vietspider.html.renderer.RenderNodeUtils;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 9, 2009  
 */
public class ULNodeChecker extends NodeChecker {
  
  public ULNodeChecker(int level) {
    super(Name.UL, level);
  }

  boolean check(CheckModel model) {
//    HTMLNode node = model.getNode();
    if(model.hasRawData()) return true;
    
   /* if(model.getTextBlockStatus() == CheckModel.UNCHECK) {
      boolean isTextBlock = contentChecker.isTextBlock(node, true, 50, 5);
      model.setTextBlockStatus( isTextBlock ? CheckModel.RIGHT : CheckModel.NOT);
    }
    if(model.hasTextBlock()) return true;*/
    
    HTMLNode node = model.getNode();
    if(linkBlockChecker.isLink(model)) {
      toContainerAncestor(model);
      return false;
    }
    if(isLinkList(node)) {
      toContainerAncestor(model);
      return false;
    }
    if(model.getTotalOfLink() > 1 
        && !linkBlockChecker.hasParagraph(node, 1)) {
      toContainerAncestor(model);
      return false;
    }
    return true;
//    return RenderNodeUtils.countWord(node) > 30;
  }
  
  public boolean isLinkList(HTMLNode root) {
    List<HTMLNode> children = root.getChildren();
    if(children == null) return true;
    List<HTMLNode> list = new ArrayList<HTMLNode>();
    for(int i = 0; i < children.size(); i++) {
      if(!children.get(i).isNode(Name.LI)) continue;
      if(hasLink(children.get(i))) list.add(children.get(i));
    }
    if(list.size() < 3) return false;
    HTMLNode node = list.get(0);
    
    NodeComparator nodeComparator = new NodeComparator();
    int counter = 1;
    int onlyLinkCounter = onlyLink(node) ? 1 : 0; 
    for(int i = 1; i < list.size(); i++) {
      HTMLNode n = list.get(i);
      if(onlyLink(n)) onlyLinkCounter++;
      if(!nodeComparator.compare(node, n)) continue;
      counter++;
    }
    
    int rate = (counter*100)/list.size();
    if(rate >= 85) return true;
    return (onlyLinkCounter*100)/list.size() > 85;
  }
  
  private boolean hasLink(HTMLNode node) {
    NodeIterator iterator = node.iterator();
    while(iterator.hasNext()) {
      HTMLNode n = iterator.next();
      if(n.isNode(Name.A)) return true;
    }
    return false;
  }
  
  private boolean onlyLink(HTMLNode node) {
    List<HTMLNode> children = node.getChildren(); 
    if(children == null || children.size() != 1) return false;
    return children.get(0).isNode(Name.A);
  }
  
  private void toContainerAncestor(CheckModel model) {
    HTMLNode node = model.getNode();
    HTMLNode div = RenderNodeUtils.getAncestor(node, Name.DIV, 0, 3);
    if(div != null) {
      if(linkBlockChecker.hasParagraph(div, 1)) return;
      model.setRemoveNode(div);
      return;
    }
    
    HTMLNode tr = RenderNodeUtils.getAncestor(node, Name.TABLE, 0, 5);
    if(tr != null) {
      if(linkBlockChecker.hasParagraph(tr, 1)) return;
      model.setRemoveNode(tr);
      return;
    }
    
  }
  
  /*private boolean isLinkListBlock(HTMLNode root) {
    List<HTMLNode> children = root.getChildren();
    if(children == null) return true;
    int counter = 0;
    HTMLNode sample = null;
    for(int i = 0; i < children.size(); i++) {
      if(sample  == null) {
        sample = children.get(i);
      } else {
        if(!compare(sample, children.get(i))) return false;
      }
      NodeIterator iterator = children.get(i).iterator();
      boolean hasLink = false;
      while(iterator.hasNext()) {
        HTMLNode n = iterator.next();
        if(!n.isNode(Name.A)) continue;
        hasLink = true;
        counter++;
      }
      if(!hasLink) return false;
    }
    if(children.size() == 0) return false;
    return (counter*100)/children.size() > 75;
  }
  
  private boolean compare(HTMLNode node1, HTMLNode node2) {
    if(node1.getName() != node2.getName()) return false;
    List<HTMLNode> children1 = node1.getChildren();
    List<HTMLNode> children2 = node2.getChildren();
    if(children1 == null) {
      if(children2 == null) return true;
      return false;
    } else if(children2 == null) {
      return false;
    }
    if(children1.size() != children2.size()) return false;
    for(int i = 0;  i < children1.size(); i++) {
      if(!compare(children1.get(i), children2.get(i))) return false;
    }
    return true;
  }*/

}
