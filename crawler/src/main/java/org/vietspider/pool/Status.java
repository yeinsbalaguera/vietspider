/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.pool;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 31, 2007  
 */
public interface Status<V> {

  public String buildStatus(int id);
  
  public V getSourceFullName();
  
}
