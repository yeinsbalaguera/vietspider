/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.vietspider.bean.IDGenerator;
import org.vietspider.common.Application;
import org.vietspider.common.Install;
import org.vietspider.common.io.LogService;
import org.vietspider.crawl.link.Link;
import org.vietspider.crawl.site.WebsiteSaveSiteImpl;
import org.vietspider.db.CrawlerConfig;
import org.vietspider.db.SystemProperties;
import org.vietspider.db.database.WebsiteSaveSiteService;
import org.vietspider.io.websites2.WebsiteStorage;
import org.vietspider.net.proxy2.ProxiesLoader;
import org.vietspider.pool.Session;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 10, 2007  
 */
public final class CrawlService {
  
  private volatile static CrawlService INSTANCE = null;
  
  public synchronized  static CrawlService getInstance() {
    if(INSTANCE == null) INSTANCE = new CrawlService();
    return INSTANCE;
  }
  
  private volatile CrawlingPool threadPool = null;
  
  public CrawlService() {
    Application.addShutdown(new Application.IShutdown() {
      
      public int getPriority() { return 0; }
      
      public void execute() {
        if(threadPool == null) return;
        threadPool.destroy();
        threadPool = null;
      }
    });
  }
 
  public void initServices() throws Exception {
    SystemProperties properties = SystemProperties.getInstance();

    int totalExecutor = CrawlerConfig.TOTAL_EXECUTOR;
    int sizeOfExecutor = CrawlerConfig.TOTAL_WORKER_OF_EXECUTOR;
    long workerTimeout = 3*60*1000;
    
    try {
      workerTimeout = Long.parseLong(properties.getValue(Application.WORKER_TIMEOUT)+"000");
    } catch (Exception exp) {
      LogService.getInstance().setMessage(exp, null);
    }
    
    
    IDGenerator.createIDGenerator();
    
    try {
      if(Application.LICENSE == Install.SEARCH_SYSTEM) {
        WebsiteStorage.getInstance();
      }
     /* if(Application.LICENSE == Install.SEARCH_SYSTEM 
          && "true".equalsIgnoreCase(properties.getValue("website.master.store"))) {
        WebsiteStorage.getInstance();
      } else  if(Application.LICENSE == Install.SEARCH_SYSTEM && 
          "true".equals(properties.getValue(Application.DETECT_WEBSITE))) {
        WebsiteStorage.getInstance();
      } else {
        WebsiteStorage.getInstance().setExecute(false);
      }*/
    } catch (Exception exp) {
      LogService.getInstance().setMessage(exp, null);
    }
    
    //VSL
    if(Application.DEMO && Application.LICENSE != Install.SEARCH_SYSTEM){
      java.util.prefs.Preferences prefs = java.util.prefs.Preferences.userNodeForPackage(getClass());
      int value = 0;
      try {
        value = Integer.parseInt(prefs.get("demo", ""));
      } catch (Exception e) {
        value = 1;
      }
      if(value > 500) {
        LogService.getInstance().setMessage("ERROR", null, "Demo expired!");
//        detectWebsite = false;
        totalExecutor = 1;
        sizeOfExecutor = 1;
        return;
      }
      prefs.put("demo", String.valueOf(value+1));
    }
    //end VSL
    
    ProxiesLoader.init();
//    ProxyFinderService.startService();
    
    threadPool = new CrawlingPool();
//    new CrawlerPoolMonitor().start();
    
//    ResourcesLoader resourcesLoader = new ResourcesLoader();
//    ArticleCleaner articleCleaner = resourcesLoader.loadCleaner();
    List<Session<String, Link>> executors =  
       new CopyOnWriteArrayList<Session<String, Link>>() ;
    for(int i = 0; i < totalExecutor; i++) {
      try {
        CrawlingSession executor = new CrawlingSession(threadPool, i);
        
        executor.init(sizeOfExecutor, workerTimeout);
        executors.add(executor);
      } catch (Throwable e) {
        LogService.getInstance().setThrowable("CRAWLER", e);
      }
    }
    
    threadPool.setExecutors(executors);
    threadPool.start();
    
    if(Application.LICENSE == Install.SEARCH_SYSTEM) {
      WebsiteSaveSiteService.setInstance(new WebsiteSaveSiteImpl());
    }
    
  }
  
  /*public void resize(int total) throws Exception {
    if(threadPooling == null) return;
    Executor<String, Source, Link> [] executors = threadPooling.getExecutors();
    if(executors == null || executors.length == total) return;
    int totalExecutor = total;
    
    Executor<String, Source, Link> [] newExecutors = new CrawlExecutor[total];
    for(int i = 0; i < Math.min(total, executors.length); i++){
      newExecutors[i] = executors[i];
    }
    
    ArticleCleaner articleCleaner = new ResourcesLoader().loadCleaner();
    SystemProperties properties = SystemProperties.getInstance();
    
    int sizeOfExecutor = 1;
    long workerTimeout = 2*60*1000;
    
    try{
      sizeOfExecutor = Integer.parseInt(properties.getValue(Application.TOTAL_WORKER_OF_EXECUTOR));
//      poolTimer = Integer.parseInt(properties.getValue("poolTimer"));
      workerTimeout = Long.parseLong(properties.getValue(Application.WORKER_TIMEOUT)+"000");
    }catch (Exception exp) {
      LogService.getInstance().setMessage(exp.toString());
    }
    
    if(Application.LICENSE == Install.PERSONAL) { 
      if(totalExecutor > 3) totalExecutor = 3;
      if(sizeOfExecutor > 3) sizeOfExecutor = 1;
      detectWebsite = false;
    } if(Application.LICENSE == Install.ENTERPRISE) {
      if(totalExecutor > 5) totalExecutor = 5;
      if(sizeOfExecutor > 5) sizeOfExecutor = 1;
      detectWebsite = false;
    }
    
    if(executors.length < totalExecutor) {
      Class<?> [] classes = new Class[] {
          Step1TestSource.class, 
          Step2DectectWebsite.class,
          Step3VisitLink.class,
          Step4ProcessData.class
      };
      
      for(int i = executors.length; i < totalExecutor; i++){
        newExecutors[i] = new CrawlExecutor(i);
        newExecutors[i].putResource(articleCleaner);
        newExecutors[i].init(sizeOfExecutor, workerTimeout, classes);
        new Thread(newExecutors[i]).start();
      }
    } else {
      for(int i = totalExecutor; i < executors.length; i++) {
        executors[i].endSession();
      }
    }
    executors = newExecutors;
  }*/
  
  public CrawlingPool getThreadPool() { return threadPool; }

}
