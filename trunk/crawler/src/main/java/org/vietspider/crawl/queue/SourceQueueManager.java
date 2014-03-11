/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.queue;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.UtilFile;
import org.vietspider.crawl.CrawlingPool;
import org.vietspider.crawl.CrawlService;
import org.vietspider.db.CrawlerConfig;
import org.vietspider.db.SystemProperties;
import org.vietspider.pool.SourceQueue;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 5, 2009  
 */
class SourceQueueManager extends Thread {

  private volatile static SourceQueueManager instance = null;

  synchronized final static SourceQueueManager getInstance() {
    if(instance == null) instance = new SourceQueueManager();
    return instance;
  }

  private SourceQueue rapid;
  private SourceQueue normal;

  private boolean sleep = false;

  private List<Integer> crawlHours = new ArrayList<Integer>();

  public SourceQueueManager() {
    long max = 100*1024l;
    if(CrawlerConfig.EXCUTOR_SIZE < 10) {
      max = 150*1024l;
    } else if(CrawlerConfig.EXCUTOR_SIZE >= 10 && CrawlerConfig.EXCUTOR_SIZE < 20) {
      max = 512*1024l;
    } else if( CrawlerConfig.EXCUTOR_SIZE >= 20){
      max = 1024*1024l;
    }
    File file = UtilFile.getFile("system", "load");
//    System.out.println("thay co "+ max + " / "+ file.length());
    if(file.length() >= max) {
      normal = new SourceQueueFileImpl();
    } else {
      normal = new SourceQueueMemoryImpl();
    }
    
    if(CrawlerConfig.TOTAL_EXECUTOR >= 5) {
      rapid = new SourceQueueRapidImpl() ;
    }
    //    System.out.println(" thay co "+ pointer+ " : "+ file.length());

    SystemProperties system = SystemProperties.getInstance();
    String value = system.getValue("crawler.crawl.hours");
    if(value != null) {
      String [] elements = value.split(",");
      for(int i = 0; i < elements.length; i++) {
        if((elements[i] = elements[i].trim()).isEmpty()) continue;
        try {
          crawlHours.add(Integer.parseInt(elements[i]));
        } catch (Exception e) {
        }
      }
    }

    SourceQueueValidator.createInstance();
    this.start();
  }


  public void run() {
    while(true) {
//      System.out.println(" chay "+ crawlHours);
      if(crawlHours.size() > 0) {
        Calendar calendar = Calendar.getInstance(); 
        CrawlingPool pool = CrawlService.getInstance().getThreadPool();
        if(pool != null && pool.getExecutors() != null) {
          int hour = calendar.get(Calendar.HOUR_OF_DAY);
//          System.out.println(" ===  >"+ hour);
          if(crawlHours.contains(hour)) {
            if(Application.containsError(SourceQueueManager.this)) {
              LogService.getInstance().setMessage(null, "Source queue continue by crawl time schedule");
              Application.removeError(SourceQueueManager.this);
            }
          } else {
            if(!Application.containsError(SourceQueueManager.this)) {
              LogService.getInstance().setMessage(null, "Source queue pause by crawl time schedule");
              Application.addError(SourceQueueManager.this);
            }
          }
        }
      }

      if(rapid != null) rapid.next();
      if(normal.next()) {
        SystemProperties system = SystemProperties.getInstance();
        try {
          Thread.sleep(Long.parseLong(system.getValue(Application.QUEUE_SLEEP)));
        } catch (Exception e) {
        }
      } else {
        try {
          Thread.sleep(10*1000);
        } catch (Exception e) {
          LogService.getInstance().setThrowable(e);
        }
      }
    }
  }

  boolean isSleep() { return sleep ; }

  void wake() { this.wake(); }

  SourceQueue getNormalQueue() { return normal;  }

}
