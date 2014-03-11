/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.path2;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 11, 2007  
 */
public class TestExpComputor {
  
  public static void main(String[] args) {
    ExpComputor computor = new ExpComputor();
    
//    System.out.println(computor.multiply(new StringBuilder("3+7x3+6+6:(4:3+1+2x(6-(900:(25x4)):3))+8%3")));
//    System.out.println(computor.multiply(new StringBuilder("3+7x-3+6+6:2+5+8%-3")));
//    System.out.println(computor.multiply(new StringBuilder("3+-7x3+6+6:2+5+8%3")));
//    System.out.println(computor.multiply(new StringBuilder("20:5x2")));
//    System.out.println(computor.multiply(new StringBuilder("20:5x2+7")));
//    System.out.println(computor.multiply(new StringBuilder("3+20:5x2+7")));
//    System.out.println(computor.multiply(new StringBuilder("3+20:5")));
    
//    System.out.println("==============================================");
    
//    System.out.println(computor.compute("3-20"));
//    System.out.println(computor.compute("-20+3"));
//    System.out.println(computor.compute("3+-20"));
//    System.out.println(computor.compute("3-20+7-8"));
    
    String [] expressions = new String [] {
        "1+1",
        "1+3+1+-3",
        "4x5-2",
        "4-5%2",
        "20:5x2",
        "3+-7x3+6+6:2+5+-8%3",
        "3+20:5x2+7",
        "3+20:5x2+7x",
        "3+7x3+6+6:(4:3+1+2x(6-(900:(25x4)):3))+8%3"
    };
    
    for(String expression : expressions) {
      System.out.println(expression + "="+computor.compute(new StringBuilder(expression)));
    }
  }
  
}
