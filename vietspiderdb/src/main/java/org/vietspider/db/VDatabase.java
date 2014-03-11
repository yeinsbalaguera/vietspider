/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 5, 2009  
 */
public interface VDatabase {
  
  public boolean isClose() ;
  
  public long lastAccess() ;
  
  public void close();
}
