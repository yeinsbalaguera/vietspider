/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.browser;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 28, 2008  
 */
public interface ErrorDetector {
  
  public String getMessage();
  
  public boolean isError(byte [] bytes);

}
