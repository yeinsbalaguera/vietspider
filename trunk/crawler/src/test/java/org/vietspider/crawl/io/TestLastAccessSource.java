/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.io;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Calendar;

import org.vietspider.common.io.LogService;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 17, 2007  
 */
public class TestLastAccessSource {
  public static void main(String[] args) {
    
    File file = new File("F:\\Temp2\\last-update");
    RandomAccessFile random = null;
    try {
      random  = new RandomAccessFile(file, "rwd");
      random.seek(0);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
      return ;
    }
    if(random == null) return ;
    
    while(true) {
      try {
        int key = random.readInt();
        long start = random.getFilePointer();
        long time = random.readLong();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        System.out.println(key +":"+ calendar.get(Calendar.DATE));
      } catch (Exception e) {
        break;
      }
    }
  }
}
