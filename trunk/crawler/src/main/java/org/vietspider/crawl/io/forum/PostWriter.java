/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.io.forum;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

import org.vietspider.common.io.LogService;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 11, 2008  
 */
abstract class PostWriter<T> implements Runnable {
    
    protected volatile ConcurrentHashMap<Integer, T> data = null;
    protected volatile File file;
    
    public synchronized void run() {
      try {
        if(file != null 
            && data != null 
            && data.size() > 0) save();
      } catch (Throwable e) {
        LogService.getInstance().setThrowable(e);
      }
      data = null;
      file = null;
      notifyAll(); // wake up waiters
    }
    
    void write(ConcurrentHashMap<Integer, T> codes, File f) {
      this.data = codes;
      this.file = f;
    }
    
    boolean isRunning() { return file != null; }
    
    abstract void save() ;
}
