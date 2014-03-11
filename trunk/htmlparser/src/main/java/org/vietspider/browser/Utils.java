/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.browser;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 23, 2008  
 */
public class Utils {
  
  static public String toFileName(String address) {
    StringBuilder builder = new StringBuilder();
    int i = 0;
    while(i < address.length()) {
      char c = address.charAt(i);
      if(Character.isLetterOrDigit(c)) {
        builder.append(c);
      } else {
        builder.append('_');
      }
      i++;
    }
    builder.append(".html");
    return builder.toString();
  }
}
