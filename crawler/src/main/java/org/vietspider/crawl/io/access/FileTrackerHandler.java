/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.io.access;

import java.io.File;

import org.vietspider.common.io.LogService;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 17, 2007  
 */
class FileTrackerHandler extends TrackerHandler {
  
  private FileTrackerReader reader;
  
  FileTrackerHandler(TrackerService tracker, File f) {
    super(tracker, f);
    
    writer = new FileTrackerWriter();
    reader = new FileTrackerReader();
    
    start();
  }
  
  public void run() {
    while(execute) {
//      synchronizedWriter();
      if(codes.size() > 0) {
        writer.write(file, codes);
//        new Thread(writer).start();
      }

      try {
        Thread.sleep(2*60*1000);
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
    if(codes.size() < 1) return;
//    synchronizedWriter();
    writer.write(file, codes);
//    writer.save();
  }
  
  synchronized long search(int code) {
//    synchronizedWriter();
    return reader.search(file, code); 
  }
  
}
