/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.browser;

import java.net.URLDecoder;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 23, 2008  
 */
public class TestURLDecoder {
  
  public static void main(String[] args) throws Exception {
    
    String value = "http://my.opera.com/chu%C3%B4nggi%C3%B3bu%E1%BB%93n/info/";
    value = URLDecoder.decode(value, "utf-8");
    System.out.println(value);
    value = URLDecoder.decode(value, "utf-8");
    System.out.println(value);
    
  }

}
