/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.pool;

import java.util.concurrent.ConcurrentHashMap;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Mar 15, 2007
 */
public abstract class Session<V, E extends Status<V>> extends Container<V> {
  
  protected Worker<V,E> [] workers;
  protected ThreadPool<V, E> pool;
  
  public Session(ThreadPool<V, E>  pool, int index) {
    super(index);
    this.pool = pool;
    resources = new ConcurrentHashMap<String, Object>();
  }
  
  @SuppressWarnings("all")
  public void init(int size_, long timeout) throws Throwable {
    workers = new Worker[size_];
   
    for(int i = 0; i < workers.length; i++){
      Task [] tasks = createTasks();
      workers[i] = new Worker<V,E>(this, i, tasks, timeout);
    }
  }
  
  abstract public Task<?> [] createTasks();
  
//*********************************** setter getter data********************************************
  
  public String getStatus(int wIdx) {
    return workers[wIdx] == null ? null : workers[wIdx].getStatus(id);
  }
  
  public Worker<V,E> getWorker(int idx) { return workers[idx]; }
  
  public int size() { return workers.length; }
  
  public abstract void newSession(V v) throws Throwable;
  
  public abstract boolean isEndSession();
  
  public abstract void endSession(boolean abort) ;
  
  public abstract boolean isWorking(String key);
  
  public abstract E nextElement();
  
  public ThreadPool<V, E> getPool() { return pool; }
  

}
