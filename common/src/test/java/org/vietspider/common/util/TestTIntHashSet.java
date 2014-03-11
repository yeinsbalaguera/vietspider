/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.common.util;

import gnu.trove.TIntHashSet;
import gnu.trove.TIntIterator;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 6, 2009  
 */
public class TestTIntHashSet {
  
  public static void main(String[] args) {
    TIntHashSet hashSet = new TIntHashSet(1000);
    hashSet.add(100);
    hashSet.add(10000);
    hashSet.add(34);
    hashSet.add(13);
    
    TIntIterator iterator = hashSet.iterator();
    while(iterator.hasNext()){
      System.out.println(iterator.next());
    }
    
    System.out.println(hashSet.add(34));
    System.out.println(hashSet.add(67));
   
    iterator = hashSet.iterator();
    while(iterator.hasNext()){
      System.out.println(iterator.next());
    }
  }
  
}
