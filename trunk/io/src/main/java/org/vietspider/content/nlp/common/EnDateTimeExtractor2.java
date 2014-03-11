/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.nlp.common;

import java.text.SimpleDateFormat;

import org.vietspider.html.util.NodeHandler;
import org.vietspider.locale.DetachDate;
/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 9, 2007  
 */
public final class EnDateTimeExtractor2 {
  
  private SimpleDateFormat [] formats = new SimpleDateFormat [] {
      new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
  };
  
  public EnDateTimeExtractor2(NodeHandler nodeHandler) { 
  
  }
  
  public DetachDate extractDateTime(String value) {
    DetachDate detachDate = null;
    value = cleanValue(value);
    return detachDate;
  }
  
  
  public String cleanValue(String value) {
    StringBuilder builder = new StringBuilder();
    int idx = 0;
    while(idx < value.length()) {
      char c = value.charAt(idx);
      if(Character.isLetterOrDigit(c) || c == ',' || c == ':') {
        builder.append(c);
      } else {
        if(builder.charAt(builder.length()-1) != ' ') builder.append(' '); 
      }
      idx++;
    }
    return builder.toString();
  }
  
 
  
}
