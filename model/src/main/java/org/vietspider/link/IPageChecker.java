/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.link;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 1, 2008  
 */
public interface IPageChecker {
  
  public final static short ALL = 0;
  public final static short LOCALE = 1;
  public final static short TEXT = 2;
  
  public boolean check(String value);
  
  public boolean onlyCheckData();
  
  public void setCheckMode(short mode);
  
}
