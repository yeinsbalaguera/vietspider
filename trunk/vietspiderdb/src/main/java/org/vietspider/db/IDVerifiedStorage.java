/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db;

import java.io.File;
import java.util.Calendar;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.common.text.CalendarUtils;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 23, 2011  
 */
public class IDVerifiedStorage extends Thread {

  protected volatile java.util.Queue<String> temp = new ConcurrentLinkedQueue<String>();
  private String name;

  public IDVerifiedStorage(String name) {
    this.name = name;
    this.start();
  }

  public void run() {
    while(true) {
      save();
      try {
        Thread.sleep(60*1000l);
      } catch (Exception e) {
      }
    }
  }
  
  public void add(String id) { temp.add(id); }
  
//  public String getStorageName() { return name; }

  void save() {
    Calendar calendar = Calendar.getInstance();
    String time = CalendarUtils.getFolderFormat().format(calendar.getTime());
    String fileName = name + "." + time + ".txt";
    File file = UtilFile.getFile("track/temp/", fileName);
    StringBuilder builder = new StringBuilder();
    while(!temp.isEmpty()) {
//      if(builder.length() > 0) builder.append("\n");
      builder.append(temp.poll());
      builder.append("\n");
    }
    String text = builder.toString();
    try {
      if(file.exists()) {
//        builder.insert(0, '\n');
        RWData.getInstance().append(file, text.getBytes());
      } else {
        RWData.getInstance().save(file, text.getBytes());
      }
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }

}
