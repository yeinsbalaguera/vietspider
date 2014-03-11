/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.renderer.checker;

import org.vietspider.html.Name;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 9, 2009  
 */
public class TABLENodeChecker extends BlockNodeChecker {
  
  public TABLENodeChecker(int level) {
    super(Name.TABLE, level);
  }
  
  /*private boolean isLinkTable(HTMLNode node) {
    NodeIterator iterator = node.iterator();
    int counter = 0;

    while(iterator.hasNext()) {
      HTMLNode n = iterator.next();
      int type = isLinkTr(n);
      if(type > 0) counter += type;
      else if(type == -1) return false;
    }

    return counter > 3;
  }*/

  /*private int isLinkTr(HTMLNode node) {
    if(!node.isNode(Name.TR)) return 0;
    if(node.getChildren() == null) return 0;
    if(node.getChildren().size() != 1) return 0;
    NodeIterator iterator = node.iterator();
    int counter = 0;
    while(iterator.hasNext()) {
      HTMLNode n = iterator.next();
      if(n.isNode(Name.A)) counter++;
      if(n.isNode(Name.TABLE)) return -1;
    }
    return counter;
  }*/
  

}
