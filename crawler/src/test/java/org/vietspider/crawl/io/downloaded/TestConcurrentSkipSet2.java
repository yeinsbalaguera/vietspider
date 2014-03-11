/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.io.downloaded;

import java.util.concurrent.ConcurrentSkipListSet;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 13, 2008  
 */
public class TestConcurrentSkipSet2 {
  

  public static void main(String[] args) {
    ConcurrentSkipListSet<Integer>  codes = new ConcurrentSkipListSet<Integer>();
//    List<Integer> codes = new ArrayList<Integer>();
    
    
    codes.add(1);
    codes.add(-1);
    codes.add(10);
    codes.add(34);
    codes.add(2);
    codes.add(6);
    codes.add(34);
    codes.add(27);
    codes.add(56);
    codes.add(-1);
    codes.add(-4);
    
//    int max_for = 10000000;
//    long start = System.currentTimeMillis();
//    for(int i = 0; i < max_for; i++) {
      for(Integer code : codes) {
        int v = code;
        if(v == 10)  codes.add(22);
        System.out.print(v+ " , ");
      }
//    }
//    long end = System.currentTimeMillis();
//    System.out.println(" thuc hien het "+ (end - start)+ " voi list");
    
    // next for
    
    
//    Integer [] array  = new Integer[codes.size()];
//    codes.remove(34);
//    array = codes.toArray(array);
    
    
    
//    Object [] array = codes.toArray();
    
    
//    start = System.currentTimeMillis();
//    for(int i = 0; i < max_for; i++) {
//      for(Integer code : array) {
//        int v = (Integer)code;
//      }
//    }
//    end = System.currentTimeMillis();
//    System.out.println(" thuc hien het "+ (end - start) + " voi array");
    
//    for(Integer code : array) {
//      System.out.print(code + " ,");
//    }
    
  }
}
