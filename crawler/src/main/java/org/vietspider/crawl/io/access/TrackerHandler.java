/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.io.access;

import java.io.File;
import java.util.Calendar;
import java.util.concurrent.ConcurrentSkipListMap;

import org.vietspider.model.Source;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 17, 2007  
 */
abstract class TrackerHandler extends Thread {
  
  protected TrackerWriter writer;
  
  protected TrackerService trackerService;
  protected File file;
  
  protected ConcurrentSkipListMap<Integer, Long> codes = null;
  
  protected boolean execute = true;
  
  TrackerHandler(TrackerService tracker, File f) {
    this.trackerService = tracker;
    this.file = f;
    codes = new ConcurrentSkipListMap<Integer, Long>();
  }
  
  abstract long search(int code) ;
  
  boolean isOutOfSize() { return false; }
  
  synchronized void write(Source source) {
    if(source == null) return;
//    StringBuilder builder = new StringBuilder(source.getName()).append('.');
//    builder.append(source.getGroup()).append('.').append(source.getCategory());
    int hashCode = trackerService.hashCode(source);
//    System.out.println(hashCode+ "  : " + Calendar.getInstance().getTimeInMillis());
    codes.put(hashCode, Calendar.getInstance().getTimeInMillis());
  }
  
  void endSession() {
//    synchronizedWriter();
    execute = false;
  }
  
//  void synchronizedWriter() {
//    synchronized (writer) {
//      while(writer.isRunning()) {
//        try {
//          writer.wait(); 
//        } catch (InterruptedException e) {
//        }
//      }
//    }
//  }
  
}
