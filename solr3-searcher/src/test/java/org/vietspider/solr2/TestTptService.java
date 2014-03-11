/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.solr2;

import java.io.File;

import org.vietspider.common.Application;
import org.vietspider.tpt.TptService;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 28, 2009  
 */
public class TestTptService {
  public static void main(String[] args) {
    try {
      File file = new File(SolrIndexStorage.class.getResource("").toURI());
      String path = file.getAbsolutePath()+File.separator+".."+File.separator+"..";
      path += File.separator+".."+File.separator+".."+File.separator+".."+File.separator+"..";
      path += File.separator + "startup"+File.separator+"src"+File.separator+"test"+File.separator+"data2";
      file  = new File(path);

      //    UtilFile.FOLDER_DATA = file.getCanonicalPath();
      System.setProperty("vietspider.data.path", file.getCanonicalPath());
      System.setProperty("vietspider.test", "true");
      
      TptService.getInstance();
      
      
      Application.PRINT = false;
      
//      testSearch();
//      testTpt("0918456074");

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      System.exit(1);
    }
  }
}
