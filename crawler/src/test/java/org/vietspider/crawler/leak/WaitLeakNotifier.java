/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawler.leak;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 11, 2008  
 */
public  class WaitLeakNotifier implements Runnable {
  
  long waittime;
  
  public WaitLeakNotifier(long time) {
    waittime = time;
  }

  public synchronized void run() {
    long now = System.currentTimeMillis();
    long diff = 0;
    while( (diff = System.currentTimeMillis() - now) < waittime) {
      System.out.println(" thay co diff "+ diff);
      try {
        Thread.sleep(waittime - diff);
      } catch(InterruptedException e){}
    }
    
    System.out.println(" chuan bi synchronized");
    notifyAll();
  }
}
