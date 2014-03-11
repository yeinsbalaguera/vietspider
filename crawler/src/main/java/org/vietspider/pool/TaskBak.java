/***************************************************************************
 * Copyright 2001-2003 VietSpider         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.vietspider.pool;

/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Mar 13, 2007
 */
// V kieu du lieu lam viec, W gia tri lam viec 
public interface TaskBak<E extends Status<?>> {
  
  public E execute(E e) throws Throwable ;
  
  public TaskBak<E> cloneInstance();
  
  public void setWorker(Worker<?, E> worker) ;
  
  public void abort();
  
//  public void end(E e);
  
}