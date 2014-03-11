/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db.url;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.vietspider.common.io.LogService;
import org.vietspider.db.VDatabases;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 14, 2009  
 */
public class UrlDatabases extends VDatabases<UrlDatabase> {

  private volatile static UrlDatabases INSTANCE;

  public static synchronized UrlDatabases getInstance() {
    if(INSTANCE == null) {
      INSTANCE  = new UrlDatabases();
    }
    return INSTANCE;    
  }

//  private final static long TIMEOUT = 5*60*1000L;

//  private volatile boolean execute = true;
//  private volatile long sleep = 15*1000l;

  protected volatile java.util.Queue<UrlData> waitData = new ConcurrentLinkedQueue<UrlData>();

  public UrlDatabases() {
    super(5*60*1000L, 15*1000l);
  }

  public void save(UrlData data) { waitData.add(data);}


  public synchronized void commit(boolean exit)  {
    if(waitData == null 
        || waitData.size() < 1 || exit) return;

    while(!waitData.isEmpty()) {
      UrlData data = waitData.poll();

      UrlDatabase database = getDatabase(data.getFolder());
      if(database == null) continue;

      try {
        if(data.getType() == UrlData.INSERT) {
          database.save(data.getMd5Hash(), data.getUrl());
        } else if(data.getType() == UrlData.REMOVE) {
          database.remove(data.getMd5Hash());
        }
      } catch (Throwable e) {
        LogService.getInstance().setThrowable("URLDATABASE", e);
        break;        
      }
    }
  }

  public synchronized UrlDatabase getDatabase(String path){
    UrlDatabase tracker = holder.get(path);
    if(tracker != null && !tracker.isClose()) return tracker;
    try {
      File folder = new File(path);
      tracker = new UrlDatabase(folder, 1*1024*1024l);
//    } catch (RecoveryException e) {
//      LogService.getInstance().setThrowable(path, e.getCause());
//      LogService.getInstance().setThrowable(path, e);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(path, e);
    }
    if(tracker != null) holder.put(path, tracker);
    return tracker;
  }

 /* public void closeExpire()  {
    Iterator<String> iterator = holder.keySet().iterator();
    Queue<String> queue = new Queue<String>();

    while(iterator.hasNext()) {
      String key = iterator.next();
      UrlDatabase tracker = holder.get(key);
      if(System.currentTimeMillis() - tracker.lastAccess() >= TIMEOUT) {
        queue.push(key);
      } else if(tracker.isClose()) {
        queue.push(key);
      } 
    }

    while(queue.hasNext()) {
      String key = queue.pop();
      UrlDatabase tracker = holder.get(key);
      holder.remove(key);
      if(tracker == null) continue;
      tracker.close();
    }
  }

  void closes() {
    Iterator<String> iterator = holder.keySet().iterator();

    while(iterator.hasNext()) {
      String key = iterator.next();
      UrlDatabase tracker = holder.get(key);
      tracker.close();
    }
  }*/
  
  public void importStream(InputStream stream, String folder) throws Exception {
    BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "utf-8"));
    UrlDatabases.getInstance().setSleep(800);
    while(true) {
      String line  = reader.readLine();
      if(line == null) break;
      
      UrlData urlData = new UrlData(line, folder);
      UrlDatabases.getInstance().save(urlData);
    }
    UrlDatabases.getInstance().setSleep(15*1000l);
  }
  
  public void setSleep(long sleep) { this.sleep = sleep; }
}
