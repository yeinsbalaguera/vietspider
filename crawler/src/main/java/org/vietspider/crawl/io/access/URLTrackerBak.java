/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.io.access;

import java.io.File;

import org.vietspider.common.io.LogService;
import org.vietspider.common.io.UtilFile;
import org.vietspider.common.text.NameConverter;
import org.vietspider.model.Source;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 22, 2008  
 */
final class URLTrackerBak implements TrackerService {
  
  final static long MAX_TOTAL_CODE = 50*1000;
  
  private final static long MAX_FILE_SIZE = MAX_TOTAL_CODE*12;
  
  private TrackerHandler tracker;
  private String name;
  
  URLTrackerBak(Source source) {
    StringBuilder builder = new StringBuilder();
    NameConverter converter = new NameConverter();
    builder.append(NameConverter.encode(source.getGroup()));
    builder.append('.').append(NameConverter.encode(source.getCategory()));
    builder.append('.').append(NameConverter.encode(source.getName()));
    createService(builder.toString());
  }
  
  public URLTrackerBak(String name_) {
    createService(name_);
  }

  public void createService(String name_) {
    this.name = name_;
    File file = UtilFile.getFile("sources/homepages/tracker", name);
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
      
      StringBuilder builder = new StringBuilder();
      NameConverter converter = new NameConverter();
      builder.append(NameConverter.encode(source.getGroup()));
      builder.append('.').append(NameConverter.encode(source.getCategory()));
      builder.append('.').append(NameConverter.encode(source.getName()));
      createService(builder.toString());
    }
    tracker.write(source);
  }
  
  public void endSession() {
    if(tracker == null) return;
    tracker.endSession();
  }
  
  public int hashCode(Source source) {
    String [] address = source.getHome();
    if(address == null || address.length < 1) return -1;
    return address[0].hashCode();
  }
  
  public synchronized long search(int addressCode) {
    return tracker.search(addressCode);
  }

  public String getName() { return name;  }
}
