/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.io;

import java.net.URL;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 4, 2008  
 */
public class TestURL {
  public static void main(String[] args) {
    try {
      new URL(".%E1%83%A6Tr%C3%B9mL%C6%B0%E1%BB%9Di%E2%84%A2%E1%83%A6.:.%C2%AB");
    }catch (Exception e) {
      e.printStackTrace();
    } 
  }
}
