/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.       *
 **************************************************************************/
package org.vietspider.bean;

import java.util.Calendar;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 5, 2007  
 */
class IDGenerator2 {
  
  public final static int IMPORT = 11; 
  
  private static IDGenerator GENERATOR;
  
  static void createIDGenerator() {
    GENERATOR = new IDGenerator();
  }
  
  public static IDGenerator currentInstance() { return GENERATOR; }
  
  private volatile Calendar lastTime = null;
  
  private volatile int counter = 0;
  private volatile int max = 10;
//  private volatile DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
  
  public IDGenerator2(int max) {
    this.max  = max;
//    if(Application.LICENSE == Install.SEARCH_SYSTEM) max = 100000; 
  }
  
  public synchronized String generateId(Calendar calendar) {
    if(!equals(calendar)) {
      lastTime = calendar;
      counter = 0;
    }
    
    StringBuilder builder = new StringBuilder(100);
    
    builder.append(calendar.get(Calendar.YEAR));
    
    int value  = calendar.get(Calendar.MONTH) + 1;
    if(value < 10) builder.append('0');
    builder.append(value);
    
    value  = calendar.get(Calendar.DAY_OF_MONTH);
    if(value < 10) builder.append('0');
    builder.append(value);
    
    value  = calendar.get(Calendar.HOUR_OF_DAY);
    if(value < 10) builder.append('0');
    builder.append(value);
    
    value  = calendar.get(Calendar.MINUTE);
    if(value < 10) builder.append('0');
    builder.append(value);
    
    value  = calendar.get(Calendar.SECOND);
    if(value < 10) builder.append('0');
    builder.append(value);
    
    counter++;
    if(counter >= max) {
      return null;
    }
    
    builder.append(counter);
    
    return builder.toString();
  }
  
  private boolean equals(Calendar calendar) {
    if(lastTime == null) return false;
    if(lastTime.get(Calendar.SECOND) != calendar.get(Calendar.SECOND)) {
      lastTime = calendar;
      return false;
    } 
    
    if(lastTime.get(Calendar.MINUTE) != calendar.get(Calendar.MINUTE)) {
      lastTime = calendar;
      return false;
    }
    
    if(lastTime.get(Calendar.HOUR_OF_DAY) != calendar.get(Calendar.HOUR_OF_DAY)) {
      lastTime = calendar;
      return false;
    }
    
    if(lastTime.get(Calendar.DAY_OF_MONTH) != calendar.get(Calendar.DAY_OF_MONTH)) {
      lastTime = calendar;
      return false;
    }
    
    if(lastTime.get(Calendar.MONTH) != calendar.get(Calendar.MONTH)) {
      lastTime = calendar;
      return false;
    }
    
    if(lastTime.get(Calendar.YEAR) != calendar.get(Calendar.YEAR)) {
      lastTime = calendar;
      return false;
    }
    
    return true;
  }
  
  @SuppressWarnings("serial")
  public static class OutOfRangeIDException extends Exception {
    public String getMessage() {
      return "Out of range id exception";
    }
  }
  
  public static void main(String[] args) {
    IDGenerator iGenerator = new IDGenerator();
    IDGenerator2 iGenerator2 = new IDGenerator2(1000);
    
    long total1 = 0;
    long total2 = 0;
    int index = 0;
    while(index < 1000) {
      Calendar calendar = Calendar.getInstance();
      long start = System.nanoTime();
      iGenerator2.generateId(calendar);
      long end = System.nanoTime();
      total1 += (end - start);
      
      start = System.nanoTime();
      iGenerator.generateId(0, calendar);
      end = System.nanoTime();
      
      total2 += (end - start);
//      System.out.println(total1+ "  : "+ total2);
      try {
        Thread.sleep((long)(Math.random()*100));
      } catch (Exception e) {
      }
      index++;
    }
    
    System.out.println(total1+ "  : "+ total2);
  }
  
}
