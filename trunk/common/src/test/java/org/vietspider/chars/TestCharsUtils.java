/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.chars;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 6, 2011  
 */
public class TestCharsUtils {
  public static void main(String[] args) {
    String text = "helll o 0";
    System.out.println("|" + text + "|");
    char [] chars = text.toCharArray();
    System.out.println("code 1 " + chars.hashCode());
    chars = CharsUtil.cutAndTrim(chars, 0, chars.length);
    System.out.println("code 2 " + chars.hashCode());
    System.out.println("|" + new String(chars) + "|");
  }
}
