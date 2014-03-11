/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.creator;

import java.net.InetAddress;
import java.net.URL;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 12, 2007  
 */
public class TestURL {

  public static void main(String[] args) {
    URL url = null;
    System.out.println("timvitdet 9kute com".endsWith("9kute com"));
    try{
      url = new URL("http://www.mattran.org.vn/TinMoi/2.htm#7");
      System.out.println(url.getRef());
      
      System.out.println(InetAddress.getLocalHost().getHostName());
      
    }catch (Exception e) {
    }
    
    

  }

}
