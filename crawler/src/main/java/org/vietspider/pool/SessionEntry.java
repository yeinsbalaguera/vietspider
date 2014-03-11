/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.pool;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 8, 2009  
 */
public abstract class SessionEntry<V>  {
  
  public abstract boolean equalsValue(V value);
  
  public abstract boolean equalsKey(String key);
  
}
