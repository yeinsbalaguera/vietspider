/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.io.codes;

import java.io.File;

import org.vietspider.common.io.ConcurrentSetInt;
import org.vietspider.common.io.LogService;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 11, 2008  
 */
abstract public class CodesTracker<T> extends Thread {
  
  protected CodesWriter<T> writer;
  
  protected volatile long lastAccess = System.currentTimeMillis();

  protected volatile boolean execute = true;
  
  public CodesTracker() {
  }

  abstract public boolean search(int code, boolean resave);
  
  abstract public void write(int code);
  
  public void endSession() {
    synchronizedWriter();
    execute = false; 
  }
  
  abstract public boolean isEmptyTemp();
  
  public boolean isEndSession() { return !execute; }
    
  public void synchronizedWriter() {
    synchronized (writer) {
      while(writer.isRunning()) {
        try {
          writer.wait(); 
        } catch (InterruptedException e) {
        }
      }
    }
  }
  
  protected void saveTemp(File folder, String name, ConcurrentSetInt codes) {
    File f = new File(folder, name);
    if(f.exists()) f.delete();
    try {
      f.createNewFile();
    }catch (Exception e) {
      LogService.getInstance().setThrowable(e);
      return;
    }
//    System.out.println(" \n\n\n  = >"+f.getAbsolutePath()+ " : "+ f.exists()+ "\n\n");
    new CacheCodesLoader().save(f, codes);
    codes.clear();
  }

  public long getLastAccess() { return lastAccess; }
  public void setLastAccess(long lastAccess) { this.lastAccess = lastAccess; }
}

