/***************************************************************************
 * Copyright 2003-2007 by VietSpider - All rights reserved.                *    
 **************************************************************************/
package org.vietspider.common.text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Sep 6, 2007
 */
public class CalendarUtils {
  
  public static synchronized SimpleDateFormat getDateTimeFormat(){
    return new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
//    return getFormat("dd/MM/yyyy HH:mm:ss", KEY_FULL_FORMAT);   
  }

  public static synchronized SimpleDateFormat getDateFormat(){
    return new SimpleDateFormat("dd/MM/yyyy");
//    return getFormat("dd/MM/yyyy", KEY_DATE_FORMAT);
  }

  public static synchronized SimpleDateFormat getFolderFormat(){
    return new SimpleDateFormat("yyyy_MM_dd");
//    return getFormat("yyyy_MM_dd", KEY_FOLDER_FORMAT);
  }
  
  public static synchronized SimpleDateFormat getParamFormat(){
    return new SimpleDateFormat("dd.MM.yyyy");
//    return getFormat("dd.MM.yyy", KEY_PARAM_FORMAT);
  }
  
  public static boolean isCurrent(Date date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    return isCurrent(calendar);
  }
  
  public static boolean isCurrent(Calendar calendar) {
    Calendar current = Calendar.getInstance();
    if(current.get(Calendar.DATE) != calendar.get(Calendar.DATE)) return false;
    if(current.get(Calendar.MONTH) != calendar.get(Calendar.MONTH)) return false;
    if(current.get(Calendar.YEAR) != calendar.get(Calendar.YEAR)) return false;
    return true;
  }
  
//  private static synchronized SimpleDateFormat getFormat(String strFormat, int style){
//    SimpleDateFormat format;
//    SoftReference<SimpleDateFormat> data = cachedDateFormat.get(style);
//    if (data == null || (format = data.get()) == null) {
//      format = new SimpleDateFormat(strFormat);
//      data = new SoftReference<SimpleDateFormat>(format);
//      cachedDateFormat.put(style, new SoftReference<SimpleDateFormat>(format));
//    } 
//    return format;
//  }

}
