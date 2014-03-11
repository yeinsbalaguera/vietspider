/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.test;

import java.util.Calendar;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 19, 2008  
 */
public class TestPermission {
  
  public static void main(String[] args) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(1246438141920l);
    System.out.println(calendar.get(Calendar.DATE));
    System.out.println(calendar.get(Calendar.HOUR_OF_DAY));
    System.out.println(calendar.get(Calendar.MINUTE));
  }
}
