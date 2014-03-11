/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.data.jdbc;

import java.util.Calendar;
import java.util.Comparator;

import org.vietspider.locale.ParseDateUtils;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Feb 8, 2007
 */
public class DateSort implements Comparator<String> {
  
  private ParseDateUtils parser= new ParseDateUtils();
  
  public int compare(String date1 , String date2){
    Calendar calendar1 = parser.parse(date1);
    Calendar calendar2 = parser.parse(date2);
    
    int number1 = calendar1.get(Calendar.YEAR);
    int number2 = calendar2.get(Calendar.YEAR);
    int value = number2 - number1;
    if(value != 0) return value;
    
    number1 = calendar1.get(Calendar.MONTH);
    number2 = calendar2.get(Calendar.MONTH);
    value = number2 - number1;
    if(value != 0) return value;
    
    number1 = calendar1.get(Calendar.DATE);
    number2 = calendar2.get(Calendar.DATE);
    value = number2 - number1;
    if(value != 0) return value;
    
    return 0;
  }
  
}
