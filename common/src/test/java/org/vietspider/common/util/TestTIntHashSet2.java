/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.common.util;

import gnu.trove.TIntHashSet;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 6, 2009  
 */
public class TestTIntHashSet2 {
  
  static  int size  = 1000000;
  static int [] values = new int [size];
  
  static TIntHashSet tintSet= new TIntHashSet(1000);
  
  private static void testTIntset() {
    long start2  = System.currentTimeMillis();
    for(int i = 0; i < size; i++) {
      tintSet.add(values[i]);
    }
    long end2= System.currentTimeMillis();
    System.out.println(tintSet.getClass().getName() + " add 3 "+ (end2 - start2));
    
    start2  = System.currentTimeMillis();
    for(int i = 0; i < size; i++) {
      tintSet.contains(values[i]);
    }
    end2= System.currentTimeMillis();
    System.out.println(tintSet.getClass().getName() + " contains "+ (end2 - start2));
    
    System.out.println(tintSet.getClass().getName() + " " + tintSet.size());
  }
  
  public static void main(String[] args) {
    for(int j = 0; j < 100; j++) {
      for(int i = 0; i < size; i++) {
        if(i < size/2) {
          values[i] = (int)(Math.random()*size);
        } else {
          values[i] = -(int)(Math.random()*size);
        }
      }
      tintSet.clear();
      testTIntset();

      for(int i = 0; i < values.length; i++) {
        if(!tintSet.contains(values[i])) {
          new Exception("hdsfdsfds").printStackTrace();
        }
      }
    }
    
  }
  
}
