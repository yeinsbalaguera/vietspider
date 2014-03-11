/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawler.util;

import java.net.URL;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jun 6, 2008  
 */
public class TestURL {
  public static void main(String[] args) throws Exception {
    String address = "http://giadinh.net.vn/html/site/../../?direct=455c6d31e7e5e49f8dea243641ca29f2&column=93&nID=24387&lang=Vn";
    URL url = new URL(address);
    System.out.println(url.toURI().normalize().toString());
    
    
  }
}
