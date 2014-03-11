/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.io.tracker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.text.NameConverter;
import org.vietspider.crawl.CrawlingSources;
import org.vietspider.crawl.link.Link;
import org.vietspider.je.page.PageSizeDatabases;
import org.vietspider.model.Source;
import org.vietspider.model.SourceUtils;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 12, 2008  
 */
public class PageSizeTracker extends Thread {
  
  private volatile static PageSizeTracker dtracker;
  
  public synchronized static PageSizeTracker getInstance() {
    if(dtracker == null) {
      dtracker = new PageSizeTracker();
      dtracker.start();
    }
    return dtracker;
  }
  
  private final static long TIMEOUT = 5*60*1000L;
  
  private PageSizeTracker() {
    Application.addShutdown(new Application.IShutdown() {

      public String getMessage() { return "Close Page Tracker Database"; }

      public int getPriority() { return 2; }

      public void execute() {
        excute = false;
        Iterator<Map.Entry<String, PageSizeDatabases>> iterator = holder.entrySet().iterator();
        while(iterator.hasNext()) {
          Map.Entry<String, PageSizeDatabases> entry = iterator.next();
          PageSizeDatabases tracker = entry.getValue();
          tracker.writeTemp();
          try {
            tracker.commit(false);
          } catch (Throwable e) {
            LogService.getInstance().setThrowable(e);
          }
          tracker.close();
        }
      }
    });
  }
  
  private ConcurrentHashMap<String, PageSizeDatabases> holder = new ConcurrentHashMap<String, PageSizeDatabases>();
  
  private ConcurrentLinkedQueue<String> closeTrackers =  new ConcurrentLinkedQueue<String>();
  
  private volatile boolean excute = true;
  
  public void run() {
    while(excute) {
      try {
        process();
      } catch (Throwable e) {
        LogService.getInstance().setThrowable(e);
      }
      
      try {
        Thread.sleep(30*1000) ;
      } catch(Exception ex) {
      }
    }
  }
  
  private void process() throws Throwable {
    List<String> removes = new ArrayList<String>();
    
    Iterator<Map.Entry<String, PageSizeDatabases>> iterator = holder.entrySet().iterator();
    while(iterator.hasNext()) {
      Map.Entry<String, PageSizeDatabases> entry = iterator.next();
      String key = entry.getKey();
      PageSizeDatabases tracker = entry.getValue();
      
      if(System.currentTimeMillis() - tracker.getLastAccess() >= TIMEOUT) {
        removes.add(key);
        closeTrackers.remove(key);
      } else if(tracker.isClose()) {
        iterator.remove();
        closeTrackers.remove(key);
      } else if(closeTrackers.contains(key)) {
        removes.add(key);
        closeTrackers.remove(key);
      } else {
        tracker.writeTemp();
        try {
          tracker.commit(true);
        } catch (Throwable e) {
          LogService.getInstance().setThrowable(e);
        }
      }
    }
    
    for(int i = 0; i < removes.size(); i++) {
      String key = removes.get(i);
      PageSizeDatabases tracker = holder.get(key);
      holder.remove(key);
      if(tracker == null) continue;
      tracker.writeTemp();
      try {
        tracker.commit(false);
      } catch (Throwable e) {
        LogService.getInstance().setThrowable(e);
      }
      tracker.close();
    }
    
    if(removes.size() > 0) Runtime.getRuntime().gc();
  }
  
  public void registry(final Source source) {
    new Thread() {
      public void run() {
        createTracker(source.getGroup(), SourceUtils.getCodeName(source));
      } 
    }.start() ; 
  }
  
  private synchronized void createTracker(String group, String codeName) {
    PageSizeDatabases tracker = holder.get(codeName);
    if(tracker != null && !tracker.isClose()) return;
    try {
      tracker = new PageSizeDatabases(
          "track/page/" + group + "/" + NameConverter.encode(codeName)
          , "size", codeName, 3*24*60*60*1000l, 3, 5*1024*1024l);
      holder.put(codeName, tracker);
//    } catch (RecoveryException e) {
//      LogService.getInstance().setThrowable(codeName, e.getCause());
//      LogService.getInstance().setThrowable(codeName, e);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(codeName, e);
    }
  }
  
  private synchronized PageSizeDatabases getTracker(Link link) {
    Source source = CrawlingSources.getInstance().getSource(link.getSourceFullName());
    String codeName = SourceUtils.getCodeName(source);
    PageSizeDatabases tracker = holder.get(codeName);
    if(tracker == null || tracker.isClose()) registry(source);
    return tracker; 
  }
  
  public static boolean isValidSize(Link link, HttpResponse response)  {
    Header header = response.getFirstHeader("Content-Length");
    if(header == null || header.getValue() == null) return true;
    long size = -1;
    try {
      size = Long.parseLong(header.getValue().trim());
    } catch (Exception e) {
    } 
    if(size < 1) return true;
    
    link.setSize(size);
    
    PageSizeDatabases tracker = getInstance().getTracker(link);
    if(tracker == null) return true;
    long saveSize =  -1;
    try {
      saveSize = tracker.read(link.getUrlId());
    } catch (Throwable e) {
    }
    if(saveSize < 1) return true;
    
//    System.out.println(" \nresponse " + saveSize + "/"+ size + "/ " + Math.abs(saveSize - size));
    if(Math.abs(saveSize - size) < 50) {
//      System.out.println(link.getAddress());
      return false;
    }
    return true;
  }
  
  public static boolean isValidSize(Link link, byte [] data) {
    if(link.getSize() > 1) return true;
    
    link.setSize(data.length);
    
    PageSizeDatabases tracker = getInstance().getTracker(link);
    if(tracker == null) return true;
    long saveSize = -1;
    try {
      saveSize = tracker.read(link.getUrlId());
    } catch (Throwable e) {
    }
    if(saveSize < 1) return true;
//    System.out.println(" \ndata " + saveSize + "/"+ data.length + "/ " + Math.abs(saveSize - data.length));
    if(Math.abs(saveSize - data.length) < 50) {
//      System.out.println(link.getAddress());
      return false;
    }
    return true;
  }
  
  public static void saveSize(Link link) {
    if(link.getSize() < 1) return;
    PageSizeDatabases tracker = getInstance().getTracker(link);
    if(tracker == null) return;
    try {
      tracker.write(link.getUrlId(), link.getSize());
    } catch (Throwable e) {
    }
  }
    
}
