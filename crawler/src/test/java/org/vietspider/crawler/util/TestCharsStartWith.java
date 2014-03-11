/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawler.util;

import org.vietspider.chars.CharsUtil;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 9, 2007  
 */
public class TestCharsStartWith {
  public static void main(String[] args) {
    
    char [] chars = "(6/25/2007 6:44:29 PM)".toCharArray();

    int start = CharsUtil.startWith(chars, '(');
    int end = -1;
    if(start > -1) {
      System.out.println(new String(chars));
      end = CharsUtil.indexOf(chars, ')', start);
      if(end > -1) {
        char [] newChars = new char[end - start - 1];
        System.arraycopy(chars, start+1, newChars, 0, newChars.length);
        chars = newChars;
      }
    }
    System.out.println(new String(chars));
  }
}
