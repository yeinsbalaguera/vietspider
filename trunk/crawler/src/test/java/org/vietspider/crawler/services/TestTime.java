package org.vietspider.crawler.services;
import java.util.Calendar;

/***************************************************************************
 * Copyright 2001-2006 VietSpider         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/

/**
 * Created by VietSpider
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Dec 7, 2006  
 */
public class TestTime {
  public static void main(String[] args) {
    double a = 4*60*60*1000;
    double current = System.currentTimeMillis();
    Calendar calendar = Calendar.getInstance();
    double value = current + a;
    calendar.setTimeInMillis((long)value);    
    System.out.println(calendar.get(Calendar.HOUR));
    calendar.set(Calendar.HOUR, 2);
    System.out.println(calendar.get(Calendar.MINUTE));
    
  }
}
