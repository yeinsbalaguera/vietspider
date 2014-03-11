/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.net.client;

import java.net.URL;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 1, 2008  
 */
public class TestHostPort {
  public static void main(String[] args) throws Exception {
    URL url = new URL("http://vietnamnet.vn");
    System.out.println(url.getDefaultPort());
  }
}
