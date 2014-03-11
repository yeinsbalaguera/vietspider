/***************************************************************************
 * Copyright 2001-2003 VietSpider         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.vietspider.pool;

import java.util.HashMap;

import org.vietspider.common.io.LogService;
import org.vietspider.crawl.CrawlService;

/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Mar 13, 2007
 */
public final class Worker<V, E extends Status<V>> extends Container<E> implements Runnable {
 
  private volatile Task<E> [] tasks; 
  private Session<V, E> executor;
  
  private Timer timer;
  private Thread thread;
  
  public Worker(Session<V, E> executor, int index, Task<E> [] tasks, long timeout){
    super(index);
    
    this.executor = executor;
    this.tasks = tasks;
    this.timer = new Timer(this, (int)timeout);
    
    for(int i = 0; i < tasks.length; i++) {
      tasks[i].setWorker(this);
    }
    
    resources = new HashMap<String, Object>();
    
    Workers.getInstance().addWork(this);
  }
  
  public void newSession() {
    try {
      if(thread !=  null && thread.isAlive()) thread.interrupt();
    } catch (Exception e) {
      LogService.getInstance().setThrowable(executor.getValue(), e, e.getMessage());
    }
    thread = new Thread(this);
    thread.start();
  }
  
  public void run() {
    timer.startSession();
    
    value = executor.nextElement();
    if(value == null) {
      try {
        if(id == 0) monitorExecutor();
      } catch (OutOfMemoryError e) {
        LogService.getInstance().setMessage(executor.getValue(), null, e.toString());
        CrawlService.getInstance().getThreadPool().setPause(true);
        Runtime.getRuntime().gc();
      } catch(Throwable exp) {
        LogService.getInstance().setThrowable(executor.getValue(), exp, exp.getMessage());
      }
      return;
    }
    
    for(int i = 0; i < tasks.length; i++) {
      try {
        if(tasks[i].execute(value) == null) break;
      } catch (OutOfMemoryError e) {
        LogService.getInstance().setMessage(executor.getValue(), null, value.toString());
        LogService.getInstance().setMessage(executor.getValue(), null, e.toString());
        LogService.getInstance().setThrowable(executor.getValue(), e);
        CrawlService.getInstance().getThreadPool().setPause(true);
        Runtime.getRuntime().gc();
        break;
      } catch(Throwable exp) {
//      exp.printStackTrace();
        LogService.getInstance().setThrowable(executor.getValue(), exp, exp.getMessage());
        break;
      }
    }
    value = null;
  }
  
  private void monitorExecutor() throws Throwable {
    if(!executor.isEndSession()) return;
    executor.endSession(false);
    executor.getPool().newSession(executor);
  }

//**********************************************data***********************************************
  
  public String getStatus(int groupId) {
    return value != null ?  value.buildStatus(groupId) : null; 
  }
  
  public boolean isFree() { 
    if(value != null) return false;
    return thread == null || !thread.isAlive() || thread.isInterrupted(); 
  }
  
  public void abort() {
    for(int i = 0; i < tasks.length; i++) {
      tasks[i].abort();
    }
    value = null; 
    thread.interrupt();
  }
  
  @SuppressWarnings("unchecked")
  public <X extends Session<V, E>> X getExecutor() { return (X)executor; }
  
  public Timer getTimer() { return timer; }
  
  //***************************************** handlers **********************************************
}
