/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.common.util;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 6, 2009  
 */
public class TestTreeSet {
  
  public static void main(String[] args) {
    java.util.TreeSet<Integer> hashSet = new java.util.TreeSet<Integer>();
    
    hashSet.add(100);
    hashSet.add(100000000);
    
    hashSet.add(-333000008);
    hashSet.add(-34);
    hashSet.add(13);
    hashSet.add(-2);
    hashSet.add(89);
    hashSet.add(13);
    
//    Integer [] values = hashSet.toArray(new Integer[hashSet.size()]);
//    Arrays.sort(values);
//    
    
    for(Integer code : hashSet) {
      System.out.println(code);
    }
    
  }
  
}
