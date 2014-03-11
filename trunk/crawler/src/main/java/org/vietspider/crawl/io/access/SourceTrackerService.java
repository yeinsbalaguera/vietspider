/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.io.access;

import java.io.File;

import org.vietspider.common.io.LogService;
import org.vietspider.common.io.UtilFile;
import org.vietspider.model.Source;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 22, 2008  
 */
public class SourceTrackerService implements TrackerService {
  
  final static long MAX_TOTAL_CODE = 20*1000;
  
  private final static long MAX_FILE_SIZE = MAX_TOTAL_CODE*12;
  
  private volatile static SourceTrackerService INSTANCE;
  
  public synchronized static  SourceTrackerService getInstance() {
    if(INSTANCE == null) INSTANCE = new SourceTrackerService();
    return INSTANCE;
  }

  private TrackerHandler tracker;
  
  public SourceTrackerService() {
    createService();
  }

  public void createService() {
    File file = UtilFile.getFile("system", "last-update");
    try {
      if(!file.exists()) file.createNewFile();
    } catch (Exception e) {
      LogService.getInstance().setThrowable("APPLICATION", e);
      file = null;
    }
    
    if(file.length() >= MAX_FILE_SIZE) {
      tracker = new FileTrackerHandler(this, file);
    } else {
      tracker = new CacheTrackerHandler(this, file);
    }
  }
  
  public void write(Source source) {
    if(tracker.isOutOfSize()) {
      tracker.endSession();
      createService();
    }
    tracker.write(source);
  }
  
  public int hashCode(Source source) {
    StringBuilder builder = new StringBuilder(source.getName()).append('.');
    builder.append(source.getGroup()).append('.').append(source.getCategory());
    return builder.toString().hashCode();
  }
  
  public synchronized long search(Source source) {
    int addressCode = hashCode(source);
    return tracker.search(addressCode);
  }
  
  public synchronized long search(String name) {
    return tracker.search(name.hashCode());
  }
}
