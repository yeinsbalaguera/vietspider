/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.index3.monitor;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.db.SystemProperties;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 7, 2009  
 */
public class SearchMonitor extends Thread {

  private static volatile SearchMonitor instance;

  public final static synchronized SearchMonitor getInstance() {
    if(instance == null) instance = new SearchMonitor();
    return instance;
  }

  private TopQueries topQueries;

  private volatile boolean execute = true;

  private ConcurrentLinkedQueue<Query> tempQueries = new ConcurrentLinkedQueue<Query>();

  public SearchMonitor() {
    execute = "true".equalsIgnoreCase(SystemProperties.getInstance().getValue("search.system"));
    topQueries = new TopQueries();

    Application.addShutdown(new Application.IShutdown() {

      public String getMessage() { return "Close Query Database";}

      public void execute() {
        execute = false;
        try {
          topQueries.saveFile();
        } catch (Throwable e) {
          LogService.getInstance().setThrowable(e);
        }
      }
    });

    this.start();
  }

  public void savePattern(String pattern, int total) {
    if(!execute) return;
    Query query = new Query(pattern, total);
    if(tempQueries.size() < 1000) tempQueries.add(query);
    //    System.out.println(" chuan bi save roi "+ pattern + " : "+ total);
  }

//  public Set<Query> getTopQuery() {
//    try {
//      return  topQueries.getCollection();
//    } catch (Exception e) {
//      LogService.getInstance().setThrowable(e);
//      return new HashSet<Query>();
//    }
//  }

  public void run() {
    while(execute) {
      Iterator<Query> iterator = tempQueries.iterator();
      while(iterator.hasNext()) {
        Query query = iterator.next();
//        search(query);
        topQueries.add(query);
        iterator.remove();
      }

      try {
        Thread.sleep(1*60*1000);
      } catch (Exception e) {
      }
    }

  }

//  private void search(Query query) {
//    ClassifiedSearchQuery searchQuery = new ClassifiedSearchQuery(query.getPattern());
//    //    System.out.println(" chuan bi search "+ query.getPattern());
//    MonitorDataSearcher2 searcher = new MonitorDataSearcher2();
//    try {
//      searcher.search(searchQuery);
//    } catch (Throwable e) {
//      LogService.getInstance().setThrowable(e);
//    }
//  }

}
