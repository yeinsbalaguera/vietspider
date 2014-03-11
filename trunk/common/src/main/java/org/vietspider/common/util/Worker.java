/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.common.util;



/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 3, 2008  
 */
public abstract class Worker {

  private int isRunning = -1;
  
  protected Worker[] plugins;

  public Worker(Worker...workers) {
    this.plugins = workers;
  }

  public void executeBefore() {
    isRunning = 0;
    before();
  }

  public void executeTask() {
    execute();
    isRunning = 1;
  }

  public void abortTask() {
    abort();
    isRunning = -2;
  }
  
  public Worker[] getPlugins() { return plugins; }

  public boolean isRunning() { return isRunning == 0; }

  public boolean isComplete() {return isRunning == 1; }

  public abstract void before() ;

  public abstract void execute();

  public abstract void abort();

  public abstract void after();
  
}
