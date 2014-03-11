/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.util2;

import java.net.URL;
import java.net.URLEncoder;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 14, 2008  
 */
public class TestURI {
  
  public static void main(String[] args) throws Exception {
    URL url  = new URL("http://conghung.com/index.php?conghung=mod:news|act:detail|newsid:3862");
    System.out.println(URLEncoder.encode("^"));
  }

}
