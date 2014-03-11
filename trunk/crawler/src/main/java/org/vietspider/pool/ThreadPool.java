/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.pool;

import java.util.List;

import org.vietspider.crawl.crepo.SessionStores;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 10, 2007  
 */
public abstract class ThreadPool<V, E extends Status<V>> extends Pausable {
  
  protected List<Session<V,E>>  executors;
  protected CrawlQueue<V> crawlQueue;
//  protected QueueEntry<V> queue;
  
  public abstract void nextElement(String key, Object...params);
  
  public abstract void newSession(Session<V, E> executor);
  
  public void abortExecutor(String key) {
    if(key == null || executors == null) return ;
    for(Session<?,?> executor : executors) {
      if(executor.isWorking(key)) executor.endSession(true);
    }
  }
  
  public int countExecutor(String key) {
    if(this.executors == null) return 0;
    int counter = 0;
    for(Session<V,E> executor : executors) {
      if(executor.isEndSession() 
          || !executor.isWorking(key)) continue;
      counter++;
    }
    return counter;
  }
  
  public void removeElement(String key) {
    crawlQueue.removeElement(key);
//    queue.removeElement(key);
    
    for(Session<V, E> executor : executors) {
      if(executor.isEndSession() 
          || !executor.isWorking(key)) continue;
      executor.endSession(true);
    }
  }
  
  /*public boolean isExecuting(SessionEntry<V> entry) {
    for(Executor<V, E> executor : executors) {
      if(executor.isEndSession()) continue;
      if(entry.equalsValue(executor.getValue())) return true;
    }
    return false;
  }*/
  
  public List<Session<V,E>> getExecutors() { return executors; }
  
  public void setExecutors(List<Session<V, E>> executors) {
    this.executors = executors;
    if(this.executors == null) return;
    int size = executors.size();
    crawlQueue.setCapacity(executors.size()+10);
//    queue.setCapacity(executors.size()+10);
    
    int rate  = 500;
    if(size >= 50) {
      rate = 300;
    } else if (size >= 30 && size < 50) {
      rate = 500;
    } else if (size >= 20 && size < 30) {
      rate = 800;
    } else if (size >= 10 && size < 20) {
      rate = 1000;
    } else {
      rate = 2000;
    }
    
    for(int i = 0; i < timers.size(); i++) {
      timers.get(i).setRate(rate);
    }
    
  }
  
  public QueueEntry<V> getQueueEntry()  { return crawlQueue.getQueueEntry(); }
  
  public void destroy() {
    for(int i = 0; i < timers.size(); i++) {
      timers.get(i).setLive(false);
    }
    
    SessionStores.getInstance().destroy();
    
    for(Session<V,E> executor : executors) {
      executor.endSession(true);
    }
  }
  
}
