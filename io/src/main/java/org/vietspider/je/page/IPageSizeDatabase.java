/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.je.page;

import org.vietspider.common.io.MD5Hash;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 30, 2009  
 */
public interface IPageSizeDatabase {
  
  public void close() throws Throwable ;
  
  public boolean isClose() ;
  
  public void update(MD5Hash key, Long value) throws Throwable;
  
  public long search(MD5Hash key) throws Throwable;

  public String getName() ;
  
  public long size();
  
  public void commit(boolean check) throws Throwable ;
  
}

