/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawler.util;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 14, 2008  
 */
public class TestSplit {
  public static void main(String[] args) {
    String [] elements = "phóng viên+pmu 18".split("\\+");
    for(String ele : elements) {
      System.out.println("==== > "+ ele);
    }
    
    String address = "http://www.360themes.com/yahoothemes/showthread.php?t=6663&ymsgr:SendIM?";
    System.out.println(address.length());
  }
}
