/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.token;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Aug 6, 2006
 */
public interface INameValue<T, V> {
  
  public T getName();
  
  public String name();
  
  public V getValue();
  
  public void setValue(V value);  
  
}
