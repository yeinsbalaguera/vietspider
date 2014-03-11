/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.model.io;

import java.util.ArrayList;
import java.util.Collections;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Sep 17, 2009
 */
public class LinkedListSample {
  
  public static void main(String[] args) {
    ArrayList<String> sources = new ArrayList<String>();
    StringBuilder builder = 
      new StringBuilder("Ba cô đội gạp lên chùa")
      .append("Có cô cởi yếm bỏ bùa cho sư")
      .append("Sư nằm sư ốm tương tư")
      .append("Ốm lăn ốm lóc nên sư trọc đầu");
    
    Collections.addAll(sources, builder.toString().split(" "));
    
  
    long time  = 1000000;
    long start = System.currentTimeMillis();
    for(int i = 0; i < time; i++) {
      ArrayList<String>  dest = new ArrayList<String>();;
      int idx = 0;
      while(idx < sources.size()) {
        String temp = sources.get(idx);
        dest.add(idx, temp);
        idx++;
      }
    }
    long end = System.currentTimeMillis();
    System.out.println(" lan 1 "+  (end - start));
    
    start = System.currentTimeMillis();
    for(int i = 0; i < time; i++) {
      ArrayList<String> dest = new ArrayList<String>();
      int idx = 0;
      while(idx < sources.size()) {
        dest.add(idx, sources.get(idx));
        idx++;
      }
    }
    end = System.currentTimeMillis();
    System.out.println(" lan 2 "+  (end - start));
    
    start = System.currentTimeMillis();
    for(int i = 0; i < time; i++) {
      ArrayList<String>  dest = new ArrayList<String>();
      dest.addAll(sources);
    }
    end = System.currentTimeMillis();
    System.out.println(" lan 3 "+  (end - start));
    
    
   /* String value;
    for(int i = 0; i < list.size(); i++) {
      value = list.get(i);
      // code logic có sử dụng biến value
    }
    
    for(int i = 0; i < list.size(); i++) {
//      String  value = list.get(i);
      // code logic có sử dụng biến value
    }
    
    for(int item : list) {
      
    }*/
    
  }
}
