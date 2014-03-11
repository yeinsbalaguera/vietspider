/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawler.leak;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 11, 2008  
 */
public class ThreadExecutor extends Thread {
  
  private WaitLeakNotifier notifier;
  
  public ThreadExecutor(WaitLeakNotifier notifier) {
    this.notifier = notifier;
  }
  
  public void run() {

    System.out.println("Starting thread " + Thread.currentThread());

    synchronized(notifier) {
      try{
        notifier.wait();
      } catch(InterruptedException e) {}
    }
    System.out.println("Terminating thread " + Thread.currentThread());
  }
    
}
