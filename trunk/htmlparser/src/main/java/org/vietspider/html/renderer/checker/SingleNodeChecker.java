/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.renderer.checker;

import org.vietspider.html.Name;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 10, 2009  
 */
public class SingleNodeChecker extends NodeChecker {
  
  public SingleNodeChecker(Name name, int level) {
    super(name, level);
  }
  
  @SuppressWarnings("unused")
  boolean check(CheckModel model) {
    return false;
  }

}
