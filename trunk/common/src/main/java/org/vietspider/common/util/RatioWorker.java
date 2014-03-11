/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.common.util;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 3, 2008  
 */
public abstract class RatioWorker extends Worker {

  private volatile int ratio = 0;
  private volatile int total = 1; 
  
  public RatioWorker() {
  }
  
  public int getRatio() { return ratio; }

  public void increaseRatio(int value) { this.ratio += value; }
  
  public void setRatio(int value) { this.ratio = value; }

  public int getTotal() { return total; }

  public void setTotal(int total) { this.total = total; }

}
