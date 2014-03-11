/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.common.io;

import java.text.DateFormat;
import java.util.Calendar;

import org.vietspider.common.text.CalendarUtils;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 8, 2008  
 */
public class DataOfDay /*implements Runnable */{
  /*
  private volatile int date = -1;
  
  private volatile String dateFoder;
  private volatile String dateValue;
  
  private static final DataOfDay INSTANCE = new DataOfDay();

  public static final DataOfDay getInstance() { return INSTANCE; }*/
  
  public DataOfDay() {
   /* Calendar calendar = Calendar.getInstance();
    date = calendar.get(Calendar.DATE);
    DateFormat folderFormat = CalendarUtils.getFolderFormat();
    dateFoder = folderFormat.format(calendar.getTime());
    DateFormat dateFormat = CalendarUtils.getDateFormat();
    dateValue = dateFormat.format(calendar.getTime());
    
    new Thread(this).start();*/
  }
  
  /*public void run() {
    while(true) {
      Calendar calendar = Calendar.getInstance();
      int value = calendar.get(Calendar.DATE);
      if(date != value) {
        date = value;
        
        DateFormat folderFormat = CalendarUtils.getFolderFormat();
        dateFoder = folderFormat.format(calendar.getTime());
        
        DateFormat dateFormat = CalendarUtils.getDateFormat();
        dateValue = dateFormat.format(calendar.getTime());
      }

      int hour = calendar.get(Calendar.HOUR_OF_DAY);
      try {
        if(hour > 1 && hour < 23) {
          Thread.sleep(30*60*1000);
        } else {
          Thread.sleep(10*1000);
        }
      } catch (Exception e) {
      }
    }
  }*/

  public final static String getDateFoder() {
    Calendar calendar = Calendar.getInstance();
    DateFormat folderFormat = CalendarUtils.getFolderFormat();
    return folderFormat.format(calendar.getTime());
  }
  
  public final static String getDateValue() { 
    Calendar calendar = Calendar.getInstance();
    DateFormat dateFormat = CalendarUtils.getDateFormat();
    return dateFormat.format(calendar.getTime());
  }

}
