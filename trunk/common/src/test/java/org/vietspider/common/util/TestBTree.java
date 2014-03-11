/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.common.util;

import java.util.Comparator;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 7, 2007  
 */
public class TestBTree {
  
  public static void main(String[] args) {
    Comparator<Integer> comparator = new Comparator<Integer>() {
      public int compare(Integer value1, Integer value2) {
        return value1 - value2;
      }
    };
    
    BinaryTree<Integer> tree = new BinaryTree<Integer>(comparator);
    tree.insert(2);
    tree.insert(4);
    tree.insert(1);
    tree.insert(-5);
    tree.insert(3);
    tree.insert(7);
    tree.insert(10);
    tree.insert(6);
    tree.insert(-2);
    tree.insert(8);
    
    System.out.println("contains 8: "+tree.contains(8));
    System.out.println("contains 2: "+tree.contains(2));
    
    System.out.println( "contains 3: "+tree.contains(3));
    tree.delete(3);
    System.out.println( "contains 3:" +tree.contains(3));
    
//    System.out.println("=====================> " + space);
    
//    for(int i = 0; i < list.size(); i++) {
//      List<Integer> subList = list.get(i);
//      if(subList.size() < 1) continue;
//      for(int k = 0; k < space; k++)  System.out.print("*");
//      for(int j = 0; j < subList.size(); j++) {
//        if(subList.get(j) == null) {
//          System.out.print("a");
//        } else {
//          System.out.print(subList.get(j));
//        }
//        if(j < subList.size()-1) System.out.print("|");
//      }
//      for(int k = 0; k < space; k++)  System.out.print("*");
//      space -= 2;
//      System.out.println();
//    }
  }
  
}
