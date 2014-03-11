/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.common.util;

import java.util.concurrent.ConcurrentSkipListSet;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 8, 2009  
 */
public class TestIntBinaryTree {
  
  private static void  compare(IntBinaryTree hashSet, Object [] values) {
    java.util.Arrays.sort(values);
    int [] array = hashSet.array();
    if(array.length != values.length) throw new Error("incorrect length");

    for(int i = 0; i < values.length; i++) {
      int v = ((Integer)values[i]).intValue();
      if(array[i] != v) {
        print(values, i);
        System.out.println("\n\n");
        print(array, i);
//        System.out.println(array[i]+ " : "+ v);
//        throw new Error("invalid value");
      }
    }
  }
  
  private static  void print(Object [] values, int j)  {
    for(int i = 0; i < values.length; i++) {
      if(i == j ) {
        System.err.print(values[i]+",");  
      } else {
        System.out.print(values[i]+",");
      }
      
    }
  }
  
  private static  void print(int [] array, int j)  {
    for(int i = 0; i < array.length; i++) {
      if(i == j ) {
        System.err.print(array[i]+",");  
      } else {
        System.out.print(array[i]+",");
      }
    }
  }
  
  public static void main(String[] args) {
    int time = 1;
    for(int i = 0; i < time; i++) {
      int size = (int)(Math.random()*100000);
      ConcurrentSkipListSet concurrentSkipListSet = new ConcurrentSkipListSet();
      IntBinaryTree hashSet = new IntBinaryTree();
      for(int j = 0; j < size; j++) {
        int v = (int)(Math.random()*size);
        concurrentSkipListSet.add(v);
        hashSet.insert(v);
      }
      Object [] values =  concurrentSkipListSet.toArray(new Integer[concurrentSkipListSet.size()]);
//      System.out.println(size+ "  : " +hashSet.size());
      compare(hashSet, values);
    }
   
//    System.out.println(hashSet.size());
//    System.out.println(hashSet.contains(89));
//    System.out.println(hashSet.contains(-34));
    
//    System.out.println("==============================");
    
     
  }
}

