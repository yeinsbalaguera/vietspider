/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.webui.search;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 20, 2010  
 */
public class ClientSessionDetector extends Thread {
  
  private final static ClientSessionDetector instance = new ClientSessionDetector();
  
  public final static ClientSessionDetector getInstance() { return instance; } 
  
  private ConcurrentHashMap<Long, Long> set = new ConcurrentHashMap<Long, Long>();
  
  private ClientSessionDetector() {
    start();
  }
  
  public void run() {
    while(true) {
      Iterator<Map.Entry<Long,Long>> iterator = set.entrySet().iterator();
      while(iterator.hasNext()) {
        Map.Entry<Long,Long> entry = iterator.next();
        long lastAccess = entry.getValue();
        if(System.currentTimeMillis() - lastAccess > 15*60*1000l) iterator.remove();
      }
      
      try {
        Thread.sleep(15*60*1000l);
      } catch (Exception e) {
      }
    }
  }
  
  public boolean isVeryFast(Long id) {
    Long value  = set.get(id);
    if(value == null) return false;
//    System.out.println(" check vao day co " + id + " :  "
//         + (System.currentTimeMillis() - value.longValue() )
//         + " : " + (System.currentTimeMillis() - value.longValue() < 1000l));
    if(id == 1) {
      return System.currentTimeMillis() - value.longValue() < 10l;
    }
    return System.currentTimeMillis() - value.longValue() < 1000l;
  }
  
  public synchronized Long generateId() {
    return System.currentTimeMillis();
  }
  
  public void put(Long key) {
    set.put(key, System.currentTimeMillis());
  }
  
  
  
}
