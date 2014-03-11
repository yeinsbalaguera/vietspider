/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.pool.timer;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 20, 2009  
 */
public class TestTimer {
  public static void main(String[] args) {
    MockTimerWorker [] timerWorkers = new MockTimerWorker[3];
    for(int i = 0; i < timerWorkers.length; i++) {
      timerWorkers[i] = new MockTimerWorker(i);
      TimerMonitor timerMonitor = new TimerMonitor(timerWorkers[i]);
      timerMonitor.startSession();
    }
  }
}
