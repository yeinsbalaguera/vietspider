/***************************************************************************
 * Copyright 2003-2007 by VietSpider - All rights reserved.                *    
 **************************************************************************/
package org.vietspider.chars;

import java.util.ArrayList;
import java.util.List;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Dec 7, 2011
 */
public class TextSpliterBak {

  public String[] split2Array(String value, char separator) {
    List<String> list = split2List(value, separator);
    return list.toArray(new String[list.size()]);
  }

  public List<String> split2List(String value, char separator) {
    int start  = 0;
    int index  = 0;
    List<String> list = new ArrayList<String>();
    while(index < value.length()) {
      char c = value.charAt(index);
//      if(!Character.isLetterOrDigit(c) 
//          && c != ' ' && c != ',' && c != '-' && c != '/') {
//        System.out.println(" =====>" + (int)c + " : "+ (int)separator + " : " + (c ==  separator));
//        System.out.println(" =====>" + c + " : "+ separator + " : " + (c ==  separator));
//        System.out.println("=======>" + (c == '\r'));
//      }
      if(c == separator) {
        list.add(value.substring(start, index));
        start = index+1;
      }
      index++;
    }
    if(start < value.length()) {
      list.add(value.substring(start, value.length()));
    }
    return list;
  }
}
