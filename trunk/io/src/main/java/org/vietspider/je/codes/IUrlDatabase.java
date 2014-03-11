/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.je.codes;

import org.vietspider.common.io.MD5Hash;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 30, 2009  
 */
public interface IUrlDatabase {
  
  public void close() throws Throwable ;
  
  public boolean isClose() ;
  
  public void save(MD5Hash key, Integer value) throws Throwable;
  
  public int search(MD5Hash key) throws Throwable;
  
  public void remove(MD5Hash key) throws Throwable;

  public String getName() ;
  
  public long size();
  
  public void commit(boolean check) throws Throwable ;
  
}

