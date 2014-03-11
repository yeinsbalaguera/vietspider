/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.io.forum;

import java.io.File;
import java.util.Calendar;
import java.util.concurrent.ConcurrentHashMap;

import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 11, 2008  
 */
class CachePostTracker extends PostTracker<CachePostTracker.PostValue> {
  
  private boolean isModified = false;
  
  public CachePostTracker() {
    writer = new CachePostWriter();
    data = new ConcurrentHashMap<Integer, PostValue>();
    
    iShutdown = new Application.IShutdown() {
      
      public int getPriority() { return 2; }
      
      public void execute() {
        execute = false;
        synchronizedWriter();
        if(!isModified) return;
        writer.write(data, file);
        writer.save();
      }
    };
    
    Application.addShutdown(iShutdown);
    start();
  }
  
  public void run() {
    while(execute) {
      synchronizedWriter();
      
      if(isModified) {
        writer.write(data, file);
        isModified = false;
        new Thread(writer).start();
      }
      
      try {
        Thread.sleep(3*60*1000);
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);;
      }
    }
    
    Application.removeShutdown(iShutdown);
    if(!isModified) {
      data.clear();
      return;
    }
    
    synchronizedWriter();
    writer.write(data, file);
    writer.save();
    isModified = false;
    data.clear();
  }
  
  public void write(int accessCode, int post) {
//    synchronizedWriter();
    long updateTime = Calendar.getInstance().getTimeInMillis();
    data.put(accessCode, new PostValue(post, updateTime));
    isModified = true;
  }
  
  public int read(int addressCode) {
    PostValue value = data.get(addressCode);
    return value == null ? -1 : value.post; 
  }
  
  public void setFile(File f) {
    synchronizedWriter();
    this.file = f;
    data = new ConcurrentHashMap<Integer, PostValue>();
    new CachePostLoader().load(file, data);
  }
  
  
  static class PostValue {
    
    int post;
    long updateTime;
    
    PostValue(int total, long time){
      this.post = total;
      this.updateTime = time;
    }
    
  }
}
