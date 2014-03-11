/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.io;

import java.net.InetAddress;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 10, 2008  
 */
public class TestInetAddress {
  
  public static void main(String[] args) throws Exception {
    InetAddress inetAddress = InetAddress.getByName("www.vietnamnet.vn");
    System.out.println(inetAddress.getHostAddress());
  }
  
}
