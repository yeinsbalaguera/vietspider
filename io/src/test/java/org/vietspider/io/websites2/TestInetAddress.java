/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.io.websites2;

import java.net.InetAddress;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 1, 2009  
 */
public class TestInetAddress {
  public static void main(String[] args) throws Exception {
    
    InetAddress  inetAddress = InetAddress.getLocalHost();
//    for(int i = 0; i < inetAddress.length; i++) {
      System.out.println(inetAddress.getHostAddress());
//    }
    
  }
}
