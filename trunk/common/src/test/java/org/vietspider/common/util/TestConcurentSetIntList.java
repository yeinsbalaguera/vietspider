/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.common.util;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.common.io.ConcurrentSetInt;
import org.vietspider.common.io.ConcurrentSetIntCacher;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 7, 2009  
 */
public class TestConcurentSetIntList {
  public static void main(String[] args) {
    ConcurrentSetInt  hashSet = new ConcurrentSetIntCacher();
    
    hashSet.add(100);
    hashSet.add(-10000);
    hashSet.add(-34);
    hashSet.add(13);
    hashSet.add(-2);
    hashSet.add(89);
    hashSet.add(13);
    hashSet.add(-4051);
    hashSet.add(-10000);
    hashSet.add(-20001);
    
    List<Integer> list = new ArrayList<Integer>();
    hashSet.addToList(list);
    
    System.out.println("kich thuoc " + list.size());
    for(int i = 0; i < list.size(); i++) {
      System.out.println(list.get(i));
    }
//    System.out.println(1000>>6);
//    System.out.println("hix "+(((long)(134217728+63))>>>6));
  }
}
