/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.net.post;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.vietspider.chars.refs.RefsDecoder;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 23, 2009  
 */
public class TextReplace {
  private static String replace(String text, String pattern, String value) {
    int idx = text.indexOf(pattern);
    if(idx < 0) return text;
    text = text.substring(0, idx) + value + text.substring(idx+pattern.length());
    return replace(text, pattern, value);
  }
  
  public static void main(String[] args) {
    String text = "__EVENTTARGET=[page]";
    System.out.println(replace(text, "[page]", "ctl00$phMainBody$repPaginationTop$ctl01$lbPage"));
    
    SimpleDateFormat dateFormat = new SimpleDateFormat();
    
    System.out.println(dateFormat.format(Calendar.getInstance().getTime()));
    
    RefsDecoder decoder = new RefsDecoder();
    System.out.println(new String(decoder.decode("&#151;".toCharArray())));
  }
}
