/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db.idm2;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.vietspider.common.io.LogService;
import org.vietspider.common.text.NameConverter;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 8, 2009  
 */
public class MergeEntryDomains  extends Thread {
  
  private static volatile MergeEntryDomains INSTANCE;

  public static synchronized MergeEntryDomains getInstance() {
    if(INSTANCE == null) {
      INSTANCE  = new MergeEntryDomains();
    }
    return INSTANCE;    
  }
  
  private Map<String, MergeEntryDomain> holder;
  
  public MergeEntryDomains() {
    holder = new ConcurrentHashMap<String, MergeEntryDomain>();
    this.start();
  }
  
  public void run() {
    while(true) {
      
      Iterator<Map.Entry<String, MergeEntryDomain>> iterator = holder.entrySet().iterator();
      
      while(iterator.hasNext()) {
        Map.Entry<String, MergeEntryDomain> entry = iterator.next();
        
        if(entry.getValue().isTimeout()) {
          iterator.remove();
        } else {
          try {
            entry.getValue().scanFiles();
          } catch (java.lang.Exception e) {
            LogService.getInstance().setThrowable(e);
          }
        }
      }
      
      try {
        Thread.sleep(1*60*1000l);
      } catch (java.lang.Exception e) {
      }
    }
  }
  
  public MergeEntryDomain getIterator(String category, String source) throws Exception {
    StringBuilder builder = new StringBuilder();
    
    if(category != null) builder.append(category);
    if(source != null) builder.append('.').append(source);
    
    String name  = NameConverter.encode(builder.toString());
    MergeEntryDomain iterator = holder.get(name);
    if(iterator != null) {
      iterator.updateLastAccess();
      return iterator;
    }
    
    iterator = new MergeEntryDomain(name);
    
    try{ 
      iterator.scanFiles();
    } catch (java.lang.Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    holder.put(name, iterator);
    return iterator;
  }
}
