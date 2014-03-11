/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.locale;

import java.util.Calendar;
import java.util.Date;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 7, 2008  
 */
public class DetachDate {
  
  private int hour = -1;
  private int minute = -1;
  private int date = -1;
  private int month = -1;
  private int year = -1;
  private int score = 0;
  
  public int getDate() { return date; }
  
  public void setDate(int date) { this.date = date; }
  
  public int getMonth() { return month; }
  
  public void setMonth(int month) { this.month = month; }
  
  public int getYear() { return year; }
  
  public void setYear(int year) { this.year = year; }
  
  public int getHour() { return hour; }
  
  public void setHour(int hour) { this.hour = hour; }
  
  public int getMinute() { return minute; }
  
  public void setMinute(int minute) { this.minute = minute; }
  
  public void increScrore(int value) { score += value  ; }
  
  public void decreScrore(int value) { score -= value  ; }
  
  public int getScrore() { return score; }
  
  public Date toDate() {
    Calendar calendar = Calendar.getInstance();
    if(date > -1) calendar.set(Calendar.DATE, date);
    if(month > -1) calendar.set(Calendar.MONTH, month-1);
    if(year > -1) calendar.set(Calendar.YEAR, year);
    if(hour > -1) calendar.set(Calendar.HOUR_OF_DAY, hour);
    if(minute > -1) calendar.set(Calendar.MINUTE, minute);

    return calendar.getTime();
  }
}
