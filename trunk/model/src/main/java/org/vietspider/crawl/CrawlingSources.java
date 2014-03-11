/***************************************************************************
 * Copyright 2001-2012 ArcSight, Inc. All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.vietspider.model.Source;
import org.vietspider.model.SourceIO;

/** 
 * Author : Nhu Dinh Thuan
 *          thuannd2@fsoft.com.vn
 * Mar 1, 2012  
 */
public class CrawlingSources  extends Thread {
  
  private final static CrawlingSources mananger = new CrawlingSources();

  public final static CrawlingSources getInstance() { return mananger; }
  
  private volatile Map<String, Data> stores;
  
  private CrawlingSources() {
    stores  = new ConcurrentHashMap<String, Data>();
    start();
  }
  
  public Source getSource(String fullName) {
    //    System.out.println("tong so "+ stores.size());
    Data data = stores.get(fullName);
    if(data == null) {
      Source source = SourceIO.getInstance().loadSource(fullName);
      if(source == null) return null;
      data = new Data(source);
      stores.put(fullName, data);
    }
    
    data.access();
    //    if(store != null) System.out.println(" thay co da duoc "+ store.hashCode()+ " : ");
    return data.source;
  }
  
  public void run() {
    while(true) {
//      System.out.println(" ==== ===========  >"+ stores.size());
      Iterator<Map.Entry<String, Data>> iterator = stores.entrySet().iterator();
      while(iterator.hasNext()) {
        Map.Entry<String, Data> entry = iterator.next();
        Data data = entry.getValue();
//        System.out.println(" store "+ store.source.getFullName() + "  : "+ store.isTimeout());
        if(data.isTimeout() || data.source == null) {
          iterator.remove();
        } else if(data.isReload()) {
          data.source = SourceIO.getInstance().reload(data.source);
        }
      }

      try {        
        Thread.sleep(60*1000l);
      } catch (Exception e) {
      }
    }
  }
  
  private class Data {
    
    private Source source;
    private long lastAccess;
    
    private Data(Source source) {
      this.source = source;
    }
    
    private void access() {
      lastAccess = System.currentTimeMillis();
    }
    
    private boolean isTimeout() {
      return System.currentTimeMillis() - lastAccess >= 5*60*1000l;
    }
    
    private boolean isReload() {
      return System.currentTimeMillis() - lastAccess >= 3*60*1000l;
    }
  }

}
