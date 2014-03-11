/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.queue;

import java.io.File;

import org.vietspider.common.Application;
import org.vietspider.crawl.queue2.PriorityCrawlQueue;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 3, 2011  
 */
public class TestLoadQueue {
  public static void main(String[] args) {
    try {
      File file = new File(PriorityCrawlQueue.class.getResource("").toURI());
      String path = file.getAbsolutePath()+File.separator+".."+File.separator+".."
      + File.separator+"..";
      path += File.separator+".."+File.separator+".."+File.separator+".."+File.separator+"..";
      path += File.separator + "startup"+File.separator+"src"+File.separator+"test"+File.separator+"data";
      file  = new File(path);

      //    UtilFile.FOLDER_DATA = file.getCanonicalPath();
      System.setProperty("vietspider.data.path", file.getCanonicalPath());
      System.setProperty("vietspider.test", "true");
      
      Application.PRINT = false;
      
      new PriorityCrawlQueue().loadCrawlEntry();
      
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      System.exit(1);
    }
  }
}
