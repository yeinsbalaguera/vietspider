/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.pool;

import org.vietspider.net.client.HttpResponseReader;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 7, 2009  
 */
public class Timer implements Runnable {
  
  private long timeout = 2*60*1000;

  private int elapsed;
  private int rate = 500;
  
  private Worker<?, ?> worker;
  
  protected boolean live = true;
  protected Pausable pausable;

  Timer(Worker<?, ?> worker, long time) {
    this.worker = worker;
    this.timeout = time + HttpResponseReader.TIMEOUT;
    
    this.pausable = worker.getExecutor().getPool();
    this.pausable.addTimer(this);

    this.elapsed = 0;
  }
  
  public void startSession () {
    this.elapsed = 0;
  }

  public void run() {
    while(live){
      if(pausable.isPause()) {
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
        continue;
      }

      // Use 'synchronized' to prevent conflicts
      synchronized (this) {
        elapsed += rate;

        // Check to see if the time has been exceeded
        if (elapsed > timeout) {
//          System.out.println(" bi timeout roi "+ elapsed);
          worker.abort();
        } 
      }

    }
  }

  void setLive(boolean live) { this.live = live; }
  
  void setRate(int r) { this.rate = r; }

}
