/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.locale;

import java.util.Calendar;

import org.vietspider.common.text.CalendarUtils;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 2, 2009  
 */
public class ParseDateUtils {
  
  public Calendar parse(String value) {
    DateTimeDetector detector = new DateTimeDetector();
    DetachDate detachDate = detector.detect(value);
    Calendar calendar = Calendar.getInstance();
    if(detachDate == null) {
      try {
        calendar.setTime(CalendarUtils.getDateFormat().parse(value));
        return calendar;
      } catch (Exception e) {
      }
      
      try {
        calendar.setTime(CalendarUtils.getFolderFormat().parse(value));
        return calendar;
      } catch (Exception e) {
      }
      try {
        calendar.setTime(CalendarUtils.getParamFormat().parse(value));
        return calendar;
      } catch (Exception e) {
      }
      return calendar;
    }
    
    calendar.set(Calendar.YEAR, detachDate.getYear());
    calendar.set(Calendar.MONTH, detachDate.getMonth()-1);
    calendar.set(Calendar.DATE, detachDate.getDate());
    calendar.set(Calendar.HOUR_OF_DAY, detachDate.getHour());
    calendar.set(Calendar.MINUTE, detachDate.getMinute());
    return calendar;
  }
  
}
