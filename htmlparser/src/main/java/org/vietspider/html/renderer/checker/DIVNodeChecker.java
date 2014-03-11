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
public class DIVNodeChecker extends BlockNodeChecker {
  
  public DIVNodeChecker(int level) {
    super(Name.DIV, level);
  }
  
 /* private boolean isContainer(HTMLNode node) {
    NodeIterator iterator = node.iterator();
    
    while(iterator.hasNext()) {
      HTMLNode n = iterator.next();
      if(n.isNode(Name.IFRAME)) return true;
    }

    return false;
  }*/

}
