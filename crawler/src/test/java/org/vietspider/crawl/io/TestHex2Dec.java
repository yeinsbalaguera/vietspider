/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.io;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jun 25, 2008  
 */
public class TestHex2Dec {
  
  public static void main(String[] args) {
    int [] values = {
        7356,
        26860,
        14469,
        8677,
        33580,
        14511,
        2081,
    };
    int total = 0;
    for(int value : values) {
      total += value;
    }
    System.out.println(total);
    
    
    String hex = "3B9C567C";
    long dec = Long.parseLong(hex, 16);

    System.out.println(dec);
//    int i = 0;
//    String value = String.valueOf(dec);
//    int total = 0;
//    while(i < value.length()) {
//      total += Integer.parseInt(String.valueOf(value.charAt(i)));
//      i++;
//    }
//    System.out.println(total);
//    
//    //523106
//    
    System.out.println(Long.toHexString(1000101550));
    //7552206458328332970
  
  }
  
}
