/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawler;

import java.net.InetAddress;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 27, 2008  
 */
public class TestInetAddress {
  
  public static void main(String[] args) throws Exception {
    String hostName = InetAddress.getByName("localhost").getCanonicalHostName();
    System.out.println(hostName);
    InetAddress[] addresses = InetAddress.getAllByName(hostName);
    for(int i = 0; i < addresses.length; i++) {
      System.out.println(addresses[i].getHostAddress());
    }
//    System.out.println(InetAddress.getByName("headvances.homeip.net"));
  }
  
}
