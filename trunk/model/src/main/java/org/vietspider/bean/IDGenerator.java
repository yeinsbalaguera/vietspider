/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.bean;

import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;

import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 5, 2007  
 */
public class IDGenerator {
  
  public final static int IMPORT = 101; 
  
  private static IDGenerator GENERATOR;
  
  public static void createIDGenerator(/*int max_*/) {
//    System.out.println(" tao voi kich thuoc "+ max_);
    GENERATOR = new IDGenerator();
  }
  
  public static IDGenerator currentInstance() { return GENERATOR; }
  
//  private volatile int max = 10;
  
//  private ConcurrentLinkedQueue<Short> set = new ConcurrentLinkedQueue<Short>();
  private AtomicInteger counter = new AtomicInteger(0);

  public IDGenerator(/*int max_*/) {
//    this.max  = max_;
//    if(max > 10000) max = 10000;
//    for(int i = max - 1000; i < max; i++) {
//      set.add(new Short((short)i));
//    }
//    if(Application.LICENSE == Install.SEARCH_SYSTEM) max = 100000; 
  }
  
  public String generateId(int index, Calendar calendar) {
    StringBuilder builder = new StringBuilder(20);
    
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
    
    if(index < 10) {
      builder.append('0').append('0').append(index);
    } else if(index >= 10 && index < 100) {
      builder.append('0').append(index);
    } else if(index == 100) {
      builder.append(index);
    } else {
      LogService.getInstance().setThrowable(new Exception("To many executors (>100)!"));
      Application.addError(this);
    }
    
//    long a = 200909052359591001l;
    
//    Short counter = //set.poll();
//    if(counter == null) return null;
    int countValue = counter.getAndIncrement();
    if(countValue >= 10) {
      counter = new AtomicInteger(0);
      countValue = 0;
    }
    
    builder.append(countValue);
//    set.add(counter);
    
    return builder.toString();
  }
  
}
