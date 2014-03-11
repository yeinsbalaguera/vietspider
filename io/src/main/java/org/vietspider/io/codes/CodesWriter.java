/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.io.codes;

import java.io.File;

import org.vietspider.common.io.LogService;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 12, 2008  
 */
abstract public class CodesWriter<T> implements Runnable {
  
  protected File file;
  protected T codes;
  
  @SuppressWarnings("hiding")
  public void write(File file, T codes) {
    this.file = file;
    this.codes = codes;
  }
  
  public synchronized void run() {
    try {
      if(file != null 
          && codes != null) {
        save();
      }
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    
    codes = null;
    file = null;
    notifyAll(); // wake up waiters
  }
  
  public boolean isRunning() {
     return Thread.currentThread().isAlive() && file != null; 
  }
  
  abstract public void save();
  
}
