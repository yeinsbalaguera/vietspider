/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.price.index;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.vietspider.bean.Article;
import org.vietspider.bean.NLPRecord;
import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.db.content.CommonDatabase;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 22, 2011  
 */
public class PriceIndexDatabases extends Thread {
  
  private static volatile PriceIndexDatabases INSTANCE;
  
  public static synchronized PriceIndexDatabases getInstance() {
    if(INSTANCE == null) INSTANCE = new PriceIndexDatabases();
    return INSTANCE;    
  }

  private volatile static long TIMEOUT = 15*60*1000L;

  private Map<String, PriceIndexDatabase> holder = new ConcurrentHashMap<String, PriceIndexDatabase>();

  protected volatile java.util.Queue<TempBean> tempQueue = new ConcurrentLinkedQueue<TempBean>();

  private volatile boolean execute = true;

  public PriceIndexDatabases() {
    Application.addShutdown(new Application.IShutdown() {

      public String getMessage() { return "Close Price Index Database";}

      public void execute() {
        execute = false;
        try {
          commit();
        } catch (Throwable e) {
          LogService.getInstance().setThrowable(e);
        }
        //        sync.close();
        closes();
      }
    });
    
    this.start();
  }

  public void run() {
    LogService.getInstance().setMessage(null, "Start Price Index Databases ");
    while(execute) {
//      System.out.println(" thay co "+ tempQueue.size());
      try {
        commit();
      } catch (Throwable e) {
        LogService.getInstance().setThrowable(e);
      }
//      System.out.println(" thay co "+ tempQueue.size());

      try {
        closeExpires();
      } catch (Throwable e) {
        LogService.getInstance().setThrowable(e);
      }


      try {
        Thread.sleep(5*1000l);
      } catch (Exception e) {
      }
    }
  }
  
  public void save(Article article) {
    TempBean bean  = TempBean.create(article);
    if(bean == null) return;
    tempQueue.add(bean);
  }
  
  public void save(NLPRecord nlp) {
    TempBean bean = TempBean.create(nlp);
    if(bean == null) return;
    tempQueue.add(bean);
  }

  synchronized void commit() throws Throwable {
    int counter = 0;
    while(!tempQueue.isEmpty()) {
      TempBean bean = tempQueue.poll();
      PriceIndexDatabase database = getDatabase(bean);
      if(database == null) continue;
      database.save(bean.action, bean.index);
      counter++;
    }
    
    if(counter > 0) { 
      LogService.getInstance().setMessage(null, "Price Index Databases commit " + counter);
    }
  }

  public synchronized PriceIndexDatabase getDatabase(TempBean bean) {
    if(bean == null || bean.city == null) return null;
    PriceIndexDatabase tracker = holder.get(bean.city);
    if(tracker != null && !tracker.isClose()) return tracker;
    try {
      tracker = new PriceIndexDatabase(bean.city);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(bean.city, e);
    }
    if(tracker != null) holder.put(bean.city, tracker);
    return tracker;
  }
  
  public synchronized CommonDatabase getDatabase(String city, String action) throws Exception {
    PriceIndexDatabase tracker = holder.get(city);
    if(tracker != null && !tracker.isClose()) {
      return tracker.getDatabase(action);
    }
    try {
      tracker = new PriceIndexDatabase(city);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(city, e);
    }
    if(tracker != null) holder.put(city, tracker);
    return tracker.getDatabase(action);
  }

  public void closeExpires() throws Exception  {
    Iterator<Map.Entry<String, PriceIndexDatabase>> iterator = holder.entrySet().iterator();
    while(iterator.hasNext()) {
      PriceIndexDatabase tracker = iterator.next().getValue();
      if(System.currentTimeMillis() - tracker.getLastAccess() >= TIMEOUT) {
        tracker.close();
        iterator.remove();
      } 
    }
  }

  void closes() {
    Iterator<Map.Entry<String, PriceIndexDatabase>> iterator = holder.entrySet().iterator();
    while(iterator.hasNext()) {
      PriceIndexDatabase tracker = iterator.next().getValue();
      tracker.close();
      iterator.remove();
    }
  }
}
