/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.io.forum;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 25, 2007  
 */
public class FilePostTracker extends PostTracker<Integer> {
  
  private FilePostReader reader;
  
  public FilePostTracker() {
    writer = new FilePostWriter();
    reader = new FilePostReader();
    
    data = new ConcurrentHashMap<Integer, Integer>();
    
    iShutdown = new Application.IShutdown() {
      
      public int getPriority() { return 2; }
      
      public void execute() {
        execute = false;
        synchronizedWriter();
        if(data.size() < 1) return;
        writer.write(data, file);
        writer.save();
      }
    };
    Application.addShutdown(iShutdown);
    
    start();
  }

  @Override
  public void run() {
    while(execute) {
      synchronizedWriter();
      synchronizedReader();
      
      if(data.size() > 0) {
        writer.write(data, file);
        new Thread(writer).start();
      }
      
      try {
        Thread.sleep(2*60*1000);
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
    
    Application.removeShutdown(iShutdown);
    if(data.size() < 1) return;
    synchronizedWriter();
    writer.write(data, file);
    writer.save();
    data.clear();
  }

  public void setFile(File file) {
    synchronizedWriter();
    this.file = file;
    this.data = new ConcurrentHashMap<Integer, Integer>();
  }

  public void write(int addressCode, int post) {
//    synchronizedWriter();
    data.put(addressCode, post);
  }
  
  public synchronized int read(int addressCode) {
    if(file == null) return -1; 
//    System.out.println(this + " : " + reader);
    synchronizedWriter();
    reader.read(file, addressCode);
    synchronizedReader();
    return reader.getValue();
  }

  private void synchronizedReader() {
    synchronized (reader) {
      while(reader.isRunning()) {
        try {
          reader.wait(); 
        } catch (InterruptedException e) {
        }
      }
    }
  }

}
