/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.ui.htmlexplorer;

import org.eclipse.swt.browser.BrowserFunction;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 31, 2009  
 */
public class JsSearchTextFunction extends BrowserFunction {
  
  JsSearchText viewer;
  
  JsSearchTextFunction(JsSearchText viewer) {
    super (viewer.getBrowser(), viewer.getSearchName());
    this.viewer = viewer;
  }
  
  public Object function(Object[] arguments) {
//    System.out.println("vao day roi "  + arguments);
    String value = "";
    for (int i = 0; i < arguments.length; i++) {
      Object arg = arguments[i];
      if (arg == null) continue;
      value = arg.toString();
    }
//    System.out.println(" ===  >"+ value);
    viewer.search(value);
    return value;
  }
}
