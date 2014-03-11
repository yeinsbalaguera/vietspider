/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.pool.timer;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 20, 2009  
 */
public class TimerMonitor implements Runnable {
  
  private int elapsed;
  private int rate = 500;
  
  private TimerWorker worker;
  
  protected Thread thread;

  public TimerMonitor(TimerWorker worker) {
    this.worker = worker;
    this.elapsed = 0;
    this.thread = new Thread(this);
  }
  
  public void startSession () {
    if(thread.isAlive()) return;
    worker.newSession();
    thread.start();
  }

  public void run() {
    while(worker.isLive()){
      if(worker.isPause()) {
        try { 
          Thread.sleep(5*1000l);
        } catch (InterruptedException ioe) {
        }
        continue;
      }
      
      try { 
        Thread.sleep(rate);
      } catch (InterruptedException ioe) {
      }
      
      if(worker.isFree()) {
        worker.newSession();
        elapsed = 0;
        continue;
      }

      // Use 'synchronized' to prevent conflicts
      synchronized (this) {
        elapsed += rate;

        // Check to see if the time has been exceeded
        if (elapsed > worker.getTimeout()) worker.abort();
      }
    }
    
    worker.destroy();
  }

}
