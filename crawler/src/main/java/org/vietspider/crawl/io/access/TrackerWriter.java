/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.io.access;

import java.io.File;
import java.util.concurrent.ConcurrentSkipListMap;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 11, 2008  
 */
abstract class TrackerWriter {
  
  protected ConcurrentSkipListMap<Integer, Long> codes = null;
  protected File file;
  
//  public synchronized void run() {
//    if(codes != null 
//        && codes.size() > 0)  {
//      try {
//        save();
//      } catch (Throwable e) {
//        LogService.getInstance().setThrowable(e);
//      }
//    }
//    codes = null;
//    notifyAll(); // wake up waiters
//  }
  
  void write(File f, ConcurrentSkipListMap<Integer, Long> c) {
    this.file = f;
    this.codes = c;
    save();
  }
  
//  boolean isRunning() { return codes != null; }
  
  abstract void save() ;

}
