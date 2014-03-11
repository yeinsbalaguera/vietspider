/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.io.downloaded;

import java.io.File;

import org.vietspider.common.io.LogService;
import org.vietspider.crawl.CrawlService;
import org.vietspider.io.LogDataImpl;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 8, 2008  
 */
public class TestDownloadService {

  public static void main(String[] args) {
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
      
//      System.out.println(DownloadedTracker.search("ARTICLE", 2107839477, true));
      
      System.exit(0);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
