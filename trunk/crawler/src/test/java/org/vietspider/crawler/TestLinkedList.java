/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawler;

import java.util.LinkedList;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 28, 2008  
 */
public class TestLinkedList {
  
  public static void main(String[] args) {
    LinkedList<Integer> values = new LinkedList<Integer>();
    for(int i = 0; i < 100000; i++) {
      values.add((int)(Math.random()*1000));
    }

    long start = System.currentTimeMillis();
    for(int value : values) {
      int value1 = value; 
    }
    long end = System.currentTimeMillis();
    
    System.out.println("mat == > " + (end - start));
    
    start = System.currentTimeMillis();
    for(int i = 0; i < values.size(); i++) {
      int value =  values.get(i);
    }
    end = System.currentTimeMillis();
    
    System.out.println("mat == > " + (end - start));
  }
  
  
}
