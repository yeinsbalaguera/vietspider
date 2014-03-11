/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.io.access;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import org.vietspider.common.io.LogService;
import org.vietspider.common.text.NameConverter;
import org.vietspider.model.Source;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 22, 2008  
 */
class URLTrackerServicesBak extends Thread {

  private volatile static URLTrackerServicesBak  instance;

  static synchronized  URLTrackerServicesBak  getInstance() {
    if(instance == null) instance = new URLTrackerServicesBak ();
    return instance;
  }

  static URLTrackerBak getURLTracker(Source source) {
    StringBuilder builder = new StringBuilder();
    NameConverter converter = new NameConverter();
    builder.append(NameConverter.encode(source.getGroup()));
    builder.append('.').append(NameConverter.encode(source.getCategory()));
    builder.append('.').append(NameConverter.encode(source.getName()));

    URLTrackerServicesBak service = getInstance();
    return service.getInternalHomepages(builder.toString());
  }

  private ConcurrentHashMap<URLTrackerBak, Long> hashtable;
  private long timeout = 10*60*1000;

  private URLTrackerServicesBak () {
    hashtable = new ConcurrentHashMap<URLTrackerBak, Long>();
    start();
  }

  public void run() {
    while(true) {
      Iterator<URLTrackerBak> iterator = hashtable.keySet().iterator();
      long current  = System.currentTimeMillis();
      while(iterator.hasNext()) {
        URLTrackerBak cacher = iterator.next();
        Long time = hashtable.get(cacher);
        if(time - current < timeout) continue;
        cacher.endSession();
        hashtable.remove(cacher);
      }

      try {
        Thread.sleep(60*1000);
      } catch (Exception e) {
      }
    }
  }

  private synchronized URLTrackerBak getInternalHomepages(String name) {
    Iterator<URLTrackerBak> iterator = hashtable.keySet().iterator();
    while(iterator.hasNext()) {
      URLTrackerBak service = iterator.next();
      String sourceName = service.getName();
      if(sourceName.equals(name)) {
        hashtable.put(service, System.currentTimeMillis());
        return service;
      }
    }

    URLTrackerBak service = new URLTrackerBak(name);
    try {
      hashtable.put(service, System.currentTimeMillis());
      return service;
    } catch (Exception e) {
      LogService.getInstance().setThrowable(name, e);
    }
    return null;
  }

}
