/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.link.pattern;

import java.net.URLDecoder;
import java.net.URLEncoder;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 14, 2009  
 */
public class TestReplace {
  public static void main(String[] args) throws Exception {
    String value =  "__EVENTTARGET={1}";
    String data = "DataGrid1$ctl24$ctl10";
    data = URLEncoder.encode(data, "utf-8");
    System.out.println(data);
    value = value.replaceAll("\\{1\\}", data);
    System.out.println(URLDecoder.decode(value, "utf-8"));
  }
}
