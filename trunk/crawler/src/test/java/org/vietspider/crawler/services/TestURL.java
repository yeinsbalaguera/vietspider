/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawler.services;

import java.net.URL;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 29, 2007  
 */
public class TestURL {
  
  public static void main(String[] args)  throws Exception {
    String address = "http://www.mattran.org.vn/tin%20chinh%20tri/ct-xh2.htm#20";
    URL url = new URL(address);
    System.out.println(url.getRef());
    System.out.println(address.substring(0, address.indexOf('#')));
  }
}
