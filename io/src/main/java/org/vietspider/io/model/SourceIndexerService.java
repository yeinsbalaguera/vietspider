/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.io.model;

import java.io.File;
import java.util.Hashtable;

import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.UtilFile;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 28, 2008  
 */
public class SourceIndexerService extends Thread {
  
  public final static String ID  = "id";
  public final static String GROUP  = "group";
  public final static String CATEGORY  = "category";
  public final static String NAME  = "name";
  public final static String URL  = "url";
  public final static String HOST  = "host";
  public final static String STATUS  = "status";
  public final static String DATE  = "date";
  
  public final static int SAVE  = 1;
  public final static int UNVAILABLE  = 0;
  public final static int DELETE  = -1;
  
  private static SourceIndexerService INSTANCE = null;

  public static synchronized SourceIndexerService getInstance() {
    if(INSTANCE == null) INSTANCE = new SourceIndexerService();
    return INSTANCE; 
  }
  
  private SourceIndexer2 indexer;
  private Hashtable<String, Integer> queue;

  SourceIndexerService() {
    queue = new Hashtable<String, Integer>();
    indexer = new SourceIndexer2();
    
    Application.addShutdown(new Application.IShutdown() {
      public void execute() {
        synchronizeIndexer();
        if(queue.size() > 0) {
          System.out.println("source indexer is writing...");
          indexer.setQueue(queue);
        }
        synchronizeIndexer();
      }
    });
    
    start();
  }
  
  public void put(String data, int status) {
    synchronizeIndexer();
    queue.put(data, status); 
  }
  
  private void synchronizeIndexer() {
    synchronized (indexer) {
      while(indexer.isWriting()) {
        try {
          indexer.wait(); 
        } catch (InterruptedException e) {}
      }
    }
  }

  public void run() {
    File file = UtilFile.getFolder("sources/index/");
    File [] files = file.listFiles();
    
    if(!file.exists() || files == null || files.length < 1) {
      SourceIndexerInit indexerInit = new SourceIndexerInit();
      synchronized (indexerInit) {
        while(indexerInit.isIndexing()) {
          try {
            indexerInit.wait(); 
          } catch (InterruptedException e) {}
        }
      }
    }
    
    while(true) {
      try {
        if(queue.size() > 0) {
          indexer.setQueue(queue);
        }
        Thread.sleep(5000);
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
  }
}
