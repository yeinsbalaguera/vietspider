/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.common.util;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 4, 2008  
 */
public class TestForEach {
  
  public static void main(String[] args) {
    int size = 2000000;
    
//    List<Integer> list = new ArrayList<Integer>();
//    for(int i = 0 ; i < size; i++) {
//      list.add((int)(Math.random()*1000));
//    }
    
    Integer [] values = new Integer[size];//list.toArray(new Integer[list.size()]);
    for(int i = 0 ; i < size; i++) {
      values[i] = (int)(Math.random()*1000);
    }
//    list.add((int)(Math.random()*1000));
//  }
    
    long start2  = System.currentTimeMillis();
    for(int i = 0 ; i < values.length; i++) {
//      Integer ele = list.get(i);
      Integer ele = values[i];
//      System.out.println("==== > "+ list.get(i));
    }
    long end2 = System.currentTimeMillis();
    
    long start1  = System.currentTimeMillis();
    for(Integer ele : values) {
//      System.out.println("==== > "+ ele);
    }
    long end1 = System.currentTimeMillis();
    
    
    System.out.println(" ==== > for each "+ (end1 - start1));
    System.out.println(" ==== > for index "+ (end2 - start2));
  }
}
