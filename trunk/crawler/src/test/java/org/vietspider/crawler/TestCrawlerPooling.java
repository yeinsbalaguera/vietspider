/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.                *
 **************************************************************************/
package org.vietspider.crawler;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.Test;
import org.vietspider.common.io.DataWriter;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.UtilFile;
import org.vietspider.common.text.NameConverter;
import org.vietspider.crawl.CrawlService;
import org.vietspider.crawl.link.Link;
import org.vietspider.db.database.DBScripts;
import org.vietspider.io.LogDataImpl;
import org.vietspider.io.loader.LoaderListHandler;
import org.vietspider.model.Source;
import org.vietspider.pool.Session;
import org.vietspider.pool.ThreadPool;
/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Oct 9, 2006
 */
public class TestCrawlerPooling {

  @BeforeClass 
  static public void before() throws Exception { 
    try {
      File file = new File(CrawlService.class.getResource("").toURI());
      String path = file.getAbsolutePath()+File.separator+".."+File.separator+"..";
      path += File.separator+".."+File.separator+".."+File.separator+".."+File.separator+"..";
      path += File.separator + "startup"+File.separator+"src"+File.separator+"test"+File.separator+"data";
      file  = new File(path);
      
//      UtilFile.FOLDER_DATA = file.getCanonicalPath();
      LogService.createInstance(LogDataImpl.class);
      System.setProperty("vietspider.data.path", file.getCanonicalPath());
      System.setProperty("vietspider.test", "true");
      
      clean(); //clean all downloaded data

//      addSource("Tổng hợp", "Vnexpress");
//      addSource("Văn hóa", "Vietnamnet");
//      addSource("Văn hóa", "Tuổi trẻ");
    }catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testCrawlerService() throws Exception {
    System.out.println("output data "+UtilFile.FOLDER_DATA);
    
    CrawlService crawlService = new CrawlService();
    crawlService.initServices();
    ThreadPool<String, Link> threadPool = crawlService.getThreadPool();
    System.out.println("started crawler service ");

    while(true) {
//      if(threadPool.isEndSession()) {
//        System.out.println("completed crawler service");
//        break;
//      }
      
      for(Session<String, Link> executor : threadPool.getExecutors()) {
//        System.out.println(" ====== > executor "+executor.getId());
        for(int i = 0; i < executor.size(); i++) {
          String status = executor.getStatus(i);
          if(status != null && !status.isEmpty()) {
            System.out.println(" Thread " + i + ": " + status);
          }
        }
      }

      Thread.sleep(5000);
    }
  }

  static void addSource(String category, String name) throws Exception {
    LoaderListHandler loaderListHandler = LoaderListHandler.getInstance();
    NameConverter converter = new NameConverter();
    category = NameConverter.encode(category);
    name = NameConverter.encode(name);
    String value = name + ".article."+category+".1"; 
    loaderListHandler.add(value);
  }

  static private void clean() {
    File file = UtilFile.getFolder("content");
    UtilFile.deleteFolder(file, false);

    file = UtilFile.getFolder("track");
    UtilFile.deleteFolder(file, false);
    
//    file = UtilFile.getFile("system", "load");
//    file.delete();

    file = UtilFile.getFile("system", "database.xml");
    try {
      DBScripts initScripts = new DBScripts(file);   
      initScripts.setInited(false);
     org.vietspider.common.io.RWData writer = org.vietspider.common.io.RWData.getInstance();
      writer.save(file, initScripts.getBytes());
    }catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }
}