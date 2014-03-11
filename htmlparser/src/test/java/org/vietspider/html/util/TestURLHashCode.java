/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.util;

import java.net.URL;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 14, 2008  
 */
public class TestURLHashCode {
  
  private void testHashcode()  {
    URLCodeGenerator util = new URLCodeGenerator();
//    String a1 = "http://www.thanhnien.com.vn/Nhadat/2007/11/17/216183.tno";
//    String a2 = "http://www2.thanhnien.com.vn/Nhadat/2007/11/17/216183.tno";
    
//    String a1 = "http://www.vietnamnet.vn/thegioi/2007/11/755374/";
//    String a2 = "http://Vietnamnet.vn/thegioi/2007/11/755374/";
    
//    String a1 = "http://www2.vietnamnet.vn/vanhoa/2007/11/756231/";
//    String a2 = "http://www.vietnamnet.vn/vanhoa/2007/11/756231/";
    
    String a1 = "http://www.openshare.com.vn/community/showthread.php?s=dbf2abcda88b21f0feec4e88d2e02e95&t=8134";
    String a2 = "http://www.openshare.com.vn/community/showthread.php?s=sdfdsfdsfdsfd5&t=8134";;
    try {
      System.out.println(util.hashCode(new URL(a1), "s="));
      System.out.println(util.hashCode(new URL(a2), "s="));
    } catch (Exception e) {
      e.printStackTrace();
    }
    
//    String a = "&#xD;&#xA;                     /Chitietsanpham/tabid/8598/ProductID/700/tid/8618/Default.aspx&#xD;&#xA;                    ";
//    System.out.println(util.replaceSpace(a));
    
  }
}
