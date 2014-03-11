/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.paging;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.vietspider.common.Application;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 13, 2009  
 */
public class PageIOs extends Thread {
  
  private volatile static PageIOs instance;
  
  public synchronized static PageIOs getInstance() {
    if(instance == null) {
      instance = new PageIOs();
      instance.start();
    }
    return instance;
  }
  
  private volatile boolean execute = true;
  private ConcurrentHashMap<String, PageIO<?>> holder = new ConcurrentHashMap<String, PageIO<?>>();
  
  public PageIOs() {
    Application.addShutdown(new Application.IShutdown() {

      public String getMessage() { return "Close URL Tracker Database"; }

      public int getPriority() { return 2; }

      public void execute() {
        execute = false;
        Iterator<Map.Entry<String, PageIO<?>>> iterator = holder.entrySet().iterator();
        while(iterator.hasNext()) {
          Map.Entry<String, PageIO<?>> entry = iterator.next();
          PageIO<?> tracker = entry.getValue();
          tracker.commit();
        }
      }
    });
  }
  
  public void commit() {
    Iterator<Map.Entry<String, PageIO<?>>> iterator = holder.entrySet().iterator();
    while(iterator.hasNext()) {
      Map.Entry<String, PageIO<?>> entry = iterator.next();
      PageIO<?> tracker = entry.getValue();
      tracker.commit();
      if(tracker.isTimeout()) iterator.remove();
    }
  }
  
  public void run() {
    while(execute) {
      commit();
//      System.out.println(" sau khi xu ly "+ holder.size() + " \n\n");
      
      try {
        Thread.sleep(5*1000) ;
      } catch(Exception ex) {
      }
    }
  }
  
  public <T extends Entry> void write(File [] files, T entry) {
    if(files == null) return;
    for(int i = 0;  i< files.length; i++) {
      write(files[i], entry);
    }
  }
  
  public <T extends Entry> void write(File file, T entry) {
    String path  = file.getAbsolutePath();
    PageIO<?> pageIO = holder.get(path);
    if(pageIO == null) {
      pageIO = entry.createIO(file);
      holder.put(path, pageIO);
    }
    pageIO.write(entry);
  }
  
  public PageIO<?> getPageIO(File file) {
    String path  = file.getAbsolutePath();
    return holder.get(path);
  }
  
  public void putPageIO(File file, PageIO<?> pageIO) {
    if(pageIO == null) return;
    String path  = file.getAbsolutePath();
    holder.put(path, pageIO);
  }

}
