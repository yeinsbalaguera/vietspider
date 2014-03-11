/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawler.leak;

import java.lang.management.ThreadInfo;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 11, 2008  
 */
public class WaitLeak  {

  public static void main(String[] args) throws Exception {
    ThreadWarningSystem tws = new ThreadWarningSystem();
    
    tws.addListener(new ThreadWarningSystem.Listener() {
      public void deadlockDetected(ThreadInfo inf) {
//        System.out.println(inf.getWaitedCount());
        System.out.println("Deadlocked Thread:");
        System.out.println("------------------");
        System.out.println(inf);
//        for (StackTraceElement ste : inf.getStackTrace()) {
//          System.out.println("\t" + ste);
//        }
      }
      public void thresholdExceeded(ThreadInfo[] threads) { }
    });
    
    WaitLeakNotifier notifier = new WaitLeakNotifier(2000);
    (new Thread(notifier)).start();

    int NUMTHREADS = 4;
    for (int i = 0; i < NUMTHREADS; i++) {
      Thread.sleep(1000);
      new ThreadExecutor(notifier).start();
    }
    
    
  }

 

}
