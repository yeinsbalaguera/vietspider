/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.webui.search.page;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 26, 2010  
 */
class Chunk {
  
  String encode(String pattern) {
    StringBuilder builder = new StringBuilder();
    for(int i = 0; i < pattern.length(); i++) {
      char c = pattern.charAt(i);
      if(c == '\"') {
        builder.append("&quot;");
      } else { 
        builder.append(c);
      }
    }
    return builder.toString();
  }
}
