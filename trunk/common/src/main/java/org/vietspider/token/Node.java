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
public interface Node<T> extends INameValue<T, char[]> {
  
  public boolean isTag();
  
}
