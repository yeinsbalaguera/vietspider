/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.chars;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 11, 2008  
 */
public class TestCopyChar {
  
  private static char[] insert(char [] data, int start, char [] values) {
    char [] newData = new char[data.length + values.length-1];
    System.arraycopy(data, 0, newData, 0, start);
    System.arraycopy(values, 0, newData, start, values.length);
    System.arraycopy(data, start+1, newData, start+values.length, data.length-start-1);
    return newData;
  }
  
  public static void main(String[] args) {
    char [] chars = "thuan".toCharArray();
    char [] value  = "<>bbv".toCharArray();
    System.out.println(new String(insert(chars, 4, value)));
  }
}
