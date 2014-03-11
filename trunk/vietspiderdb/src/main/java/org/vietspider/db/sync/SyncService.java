/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db.sync;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.Header;
import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 22, 2009  
 */
public class SyncService implements Runnable {
  
  private final static SyncService instance = new SyncService();
  
  public static SyncService getInstance() { return instance; }
  
  private Map<String, SyncHandler<?>> holder;
  
  protected boolean execute = true;
  
  public SyncService() {
    holder = new  ConcurrentHashMap<String, SyncHandler<?>>();
    Application.addShutdown(new Application.IShutdown() {
      public String getMessage() { return "Close Sync Service";}
      public void execute() {
        execute = false;
        Iterator<Map.Entry<String, SyncHandler<?>>> iterator = holder.entrySet().iterator();
        while(iterator.hasNext()) {
          iterator.next().getValue().storeTemp();
        }
      }
    });
    new Thread(this).start();
  }
  
  public void run() {
    while(execute) {
      Iterator<Map.Entry<String, SyncHandler<?>>> iterator = holder.entrySet().iterator();
      boolean wait = true;
      while(iterator.hasNext()) {
        Map.Entry<String, SyncHandler<?>> entry = iterator.next();
        if(entry.getValue().sync()) wait = false;
      }
      
      try {
        if(wait) {
          Thread.sleep(15*1000l);
        } else {
          Thread.sleep(500);
        }
      } catch (Exception e) {
      }
    }
  }
  
  public <T extends Serializable> void sync(Class<? extends SyncHandler<T>> clazz, T object) {
    try {
      SyncHandler<T> handler = getHandler(clazz);
//      System.out.println(handler + " : "+ object);
      handler.add(object);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }
  
  @SuppressWarnings("unchecked")
  public <E extends Serializable> E load(Class<? extends SyncHandler<?>> clazz,
      String id, String action, Header...headers) {
    try {
      SyncHandler<?> handler = holder.get(clazz.getName());
      if(handler == null) handler = createHandler2(clazz);
      return (E)handler.load(id, action, headers);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    return null;
  }
  
  @SuppressWarnings("unchecked")
  private <T extends Serializable> SyncHandler<T> getHandler(
      Class<? extends SyncHandler<T>> clazz) throws Exception {
    SyncHandler<T> handler = (SyncHandler<T>)holder.get(clazz.getName());
    if(handler != null) return handler;
    return createHandler(clazz);
  }
  
  @SuppressWarnings("unchecked")
  public synchronized <T extends Serializable> SyncHandler<T> createHandler(
      Class<? extends SyncHandler<T>> clazz) throws Exception {
    SyncHandler<T> handler = (SyncHandler<T>)holder.get(clazz.getName());
    if(handler != null) return handler;
    handler = clazz.newInstance();
    holder.put(clazz.getName(), handler);
    return handler;
  }
  
  private  synchronized SyncHandler<?> createHandler2(
      Class<? extends SyncHandler<?>> clazz) throws Exception {
    SyncHandler<?> handler = holder.get(clazz.getName());
    if(handler != null) return handler;
    handler = clazz.newInstance();
    holder.put(clazz.getName(), handler);
    return handler;
  }
  
}
