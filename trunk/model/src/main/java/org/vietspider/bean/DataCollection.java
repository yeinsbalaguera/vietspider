/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.bean;

import java.util.List;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 26, 2010  
 */

public interface DataCollection<T> {
  
  public void set(List<T> list) ;
  
  public List<T> get() ;

}
