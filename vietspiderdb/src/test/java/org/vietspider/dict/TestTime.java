/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.dict;

import java.util.Calendar;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 15, 2010  
 */
public class TestTime {
  
  public static void main(String[] args) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(1112804033822l);
    System.out.println(calendar.getTime());
    
    calendar.setTimeInMillis(1270570433822l);
    System.out.println(calendar.getTime());
    
    calendar.setTimeInMillis(1117556033838l);
    System.out.println(calendar.getTime());
    
    calendar.setTimeInMillis(1275322433838l);
    System.out.println(calendar.getTime());
    
  }
}
