/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.bean;


/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Feb 21, 2007
 */
public interface Bean<T> {
  
  public void setField(RSField  field, Object value) throws Exception;
  
  public RSField getField();
}
