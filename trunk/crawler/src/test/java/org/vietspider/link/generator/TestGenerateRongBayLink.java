/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.link.generator;

import java.io.File;

import org.vietspider.common.io.LogService;
import org.vietspider.crawl.CrawlService;
import org.vietspider.io.LogDataImpl;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 18, 2008  
 */
public class TestGenerateRongBayLink {

  public static void main(String[] args) throws Exception {
//    char [] chars = "0123456789QWERTYUIOPSDFGHJKLZXCVBNMĐÊƠÔƯÂ".toCharArray();
    int [] elements  =  {
      98, 276, 278,15,280,2,272,279,19,264,100,20,69,277,1,266,83,275
    };
    
    File file = new File(CrawlService.class.getResource("").toURI());
    String path = file.getAbsolutePath()+File.separator+".."+File.separator+"..";
    path += File.separator+".."+File.separator+".."+File.separator+".."+File.separator+"..";
    path += File.separator + "startup"+File.separator+"src"+File.separator+"test"+File.separator+"data";
    file  = new File(path);
    
//    UtilFile.FOLDER_DATA = file.getCanonicalPath();
    LogService.createInstance(LogDataImpl.class);
    System.setProperty("vietspider.data.path", file.getCanonicalPath());
    System.setProperty("vietspider.test", "true");
    
//    VietnameseSingleWords singleWords = new VietnameseSingleWords(null);
//    String[] chars = singleWords.getDict();
    
    String[] chars = {"người đăng"};
    
    for(int i = 0; i < elements.length; i++) {
      for(int j = 0; j < chars.length; j++) {
        for(int k = 1; k <= 50; k++) {
          String value  = "http://rongbay.com/ajax_request.html?search_type=sphinx&request_type=request_search&searchword=" 
            + chars[j] + "&catid_search=" + String.valueOf(elements[i])
            +"&nc=0&tt=0&total=2524&page=" + String.valueOf(k);
          System.out.println(value);
        }
      }
    }
    
//    for(int i = 0; i < elements.length; i++) {
//      for(int j = 1; j < chars.length; j++) {
//        for(int k = 1; k <= 30; k++) {
//          String value  = "http://rongbay.com/ajax_request.html?search_type=sphinx&request_type=request_search&searchword=" 
//            + chars[j] + "&catid_search=" + String.valueOf(elements[i])
//            +"&nc=0&tt=0&total=2524&page=" + String.valueOf(k);
//          System.out.println(value);
//        }
//      }
//    }
  }

}
