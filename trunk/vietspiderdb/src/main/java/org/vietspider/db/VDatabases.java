/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.vietspider.common.Application;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 5, 2009  
 */
public abstract class VDatabases<T extends VDatabase> implements Runnable {
  
  protected volatile Map<String, T> holder = new ConcurrentHashMap<String, T>();
  protected volatile boolean execute  = true;
  
  protected volatile long timeout = 5*60*1000l;
  protected volatile long sleep = 1000;
  
  public VDatabases(long _timeout) {
    this.timeout = _timeout;
    Application.addShutdown(new Application.IShutdown() {

      public String getMessage() { return "Close " + VDatabases.this.getClass() + " Database";}

      public int getPriority() { return 2; }

      public void execute() {
        execute = false;
        commit(true);
        closes();
      }
    });
  }
  
  public VDatabases(long _timeout, long _sleep) {
    this(_timeout);
    this.sleep =_sleep;
    new Thread(this).start();
  }
  
  public abstract void commit(boolean exit)  ;

  public void closeExpires()  {
    Iterator<String> iterator = holder.keySet().iterator();

    while(iterator.hasNext()) {
      String key = iterator.next();
      T tracker = holder.get(key);
      if(System.currentTimeMillis() - tracker.lastAccess() >= 5*60*1000L) {
        tracker.close();
        iterator.remove();
      } else if(tracker.isClose()) {
        iterator.remove();
      } 
    }
  }
  
  public void run() {
    while(execute) {
      commit(false);
      closeExpires();
      try {
        Thread.sleep(5*1000);
      } catch (Exception e) {
      }
    }
  }

  public void closes()  {
    Iterator<String> iterator = holder.keySet().iterator();

    while(iterator.hasNext()) {
      String key = iterator.next();
      T tracker = holder.get(key);
      tracker.close();
      iterator.remove();
    }
  }
  
}
