/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.io.access;

import java.io.File;

import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.model.Source;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 11, 2008  
 */
class CacheTrackerHandler extends TrackerHandler {
  
  private volatile boolean isModified = false;
  
  private Application.IShutdown iShutdown;
  
  public CacheTrackerHandler(TrackerService tracker, File f) {
    super(tracker, f);
    writer = new CacheTrackerWriter();
    
    iShutdown = new Application.IShutdown() {
      public int getPriority() { return 2; }
      public void execute() {
//        if(!isModified) return;
//        writer.write(file, codes);
//        writer.save();
      }
    };
    Application.addShutdown(iShutdown);
    
    new CacheTrackerLoader().load(file, codes);
    
    start();
  }
  
  public void run() {
    while(execute) {
//      synchronizedWriter();
      
      if(isModified) {
        writer.write(file, codes);
        isModified = false;
//        new Thread(writer).start();
      }
      
      try {
        Thread.sleep(2*60*1000);
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
    if(!isModified) return;
//    synchronizedWriter();
    writer.write(file, codes);
//    writer.save();
    isModified = false;
  }
  
  synchronized void write(Source source) {
    super.write(source);
    isModified = true;
  }
  
  long search(int code) {
    Long value = codes.get(code);
    return value == null ? -1 : value; 
  }
  
  boolean isOutOfSize() { 
    return codes.size() >= SourceTrackerService.MAX_TOTAL_CODE; 
  }
  
}
