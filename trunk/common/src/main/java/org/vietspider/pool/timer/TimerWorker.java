/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.pool.timer;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 20, 2009  
 */
public interface TimerWorker  {

  public long getTimeout() ;
  
  public void abort();
  
  public boolean isFree();
  
  public void newSession();
  
  public boolean isPause();
  
  public boolean isLive();
  
  public void destroy();
  
  
}
