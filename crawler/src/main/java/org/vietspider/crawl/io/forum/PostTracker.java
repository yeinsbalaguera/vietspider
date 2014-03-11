/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.io.forum;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

import org.vietspider.common.Application;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 11, 2008  
 */
public abstract class PostTracker<T> extends Thread {
  
  public final static long EXPIRE = 15*24*60*60*1000; 
  
  protected Application.IShutdown iShutdown;
  
  protected ConcurrentHashMap<Integer, T> data;
  
  protected File file;
  
  protected PostWriter<T> writer;

  protected volatile boolean execute = true;
  
  public PostTracker() {
  }

  abstract public int read(int accessCode);
  
  abstract public void write(int accessCode, int post);
  
  abstract public void setFile(File file);
  
  public void endSession() {
//    synchronizedWriter();
    execute = false; 
  }
    
  void synchronizedWriter() {
//    synchronized (writer) {
//      while(writer.isRunning()) {
//        try {
//          Thread.sleep(100);
//        } catch (InterruptedException e) {
//        }
//      }
//    }
    
    synchronized (writer) {
      while(writer.isRunning()) {
        try {
          writer.wait(); 
        } catch (InterruptedException e) {
        }
      }
    }
  }
  
}

