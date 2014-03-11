/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.io.websites2.lang;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.vietspider.bean.website.Website;
import org.vietspider.bean.website.Websites;
import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.db.SystemProperties;
import org.vietspider.db.website.WebsiteDatabases;
import org.vietspider.locale.vn.VietnameseDataChecker;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 24, 2009  
 */
public class WebsiteDetectors extends Thread {

  private volatile boolean pause = false;
  private VietnameseDataChecker langChecker;
  private WebsiteDetector [] threads;
  private ConcurrentLinkedQueue<Website> queue;
  
  private WebsiteDatabases database;
  
//  private WebsiteReDetect redetect;

  public WebsiteDetectors(WebsiteDatabases database) {
    langChecker = new VietnameseDataChecker();
    threads = new WebsiteDetector[1];
    queue = new ConcurrentLinkedQueue<Website>();
    for(int i = 0; i < threads.length; i++) {
      threads[i] = new WebsiteDetector(database, langChecker);
    }
    this.database = database;
    this.start();
//    redetect = new WebsiteReDetect();
  }

  public void run() {
    SystemProperties system = SystemProperties.getInstance();
    String configValue = system.getValue(Application.DETECT_LANGUAGE);
    configValue = configValue != null ? configValue.trim() : "false";
    if("false".equals(configValue)) return;

    LogService.getInstance().setMessage("WEBSITE", null, "Website Detector start...");
    while(true) {
      if(pause) {
        try {
          Thread.sleep(5*1000);
        } catch (Exception e) {
        }    
      } else {
        try {
          Thread.sleep(detect());
        } catch (Exception e) {
          
        } 

        for(int i = 0; i < threads.length; i++) {
          if(!threads[i].isTimeout()) continue;
          threads[i].start(queue.poll());
        }
      }
    }
  }

  long detect() {
    if(Application.hasError()) return 15*1000;
    if(!queue.isEmpty()) return 1000;
    Websites websites = new Websites();
    database.loadNewWebsite(websites);
    List<Website> list = websites.getList();
    if(list.size() < 1) return 15*1000;
    queue.addAll(list);
    return 1000;
  }

//  public WebsiteReDetect getRedetect() { return redetect; }

}
