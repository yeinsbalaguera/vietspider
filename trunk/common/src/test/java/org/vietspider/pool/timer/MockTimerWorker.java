/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.pool.timer;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 20, 2009  
 */
public class MockTimerWorker implements TimerWorker, Runnable {
  

  private Thread thread;
  
  private int id;
  
  public MockTimerWorker(int id) {
    this.id = id;
  }
  
  public boolean isLive() { return true; }

  public void abort() {
    System.err.println("mock id "+ id + "  thread " + thread.hashCode() + " aborted " );
    thread.interrupt();
  }

  public long getTimeout() { return 10*1000; }

  public boolean isFree() { return thread  == null || !thread.isAlive(); }

  public boolean isPause() { return false; }

  public void newSession() {
    thread  = new Thread(this);
    thread.start();
  }
  
  public void run() {
    long sleep = (long)(Math.random()*20*1000);
    System.out.println("mock id "+ id + "  thread " + thread.hashCode() + " sleep "+ sleep + " start " );
    try {
      Thread.sleep(sleep);
    } catch (Exception e) {
    }
    System.out.println("mock id "+ id + "  thread " + thread.hashCode() + " end " );
  }
  
  public void destroy() {
  }


}
