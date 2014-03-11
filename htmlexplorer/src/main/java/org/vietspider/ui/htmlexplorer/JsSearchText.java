/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.ui.htmlexplorer;

import org.eclipse.swt.browser.Browser;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 31, 2009  
 */
public interface JsSearchText {
  
  public void search(String text);
  
  public Browser getBrowser();
  
  public String getSearchName();

}
