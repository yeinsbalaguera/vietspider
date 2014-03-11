/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.index.analytics;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 16, 2009  
 */
public class TextAnalyzer {
  
  public final String clean(String value) {
    StringBuilder builder = new StringBuilder(value) ;
    int index = 0;
    while(index < builder.length()) {
      char c = builder.charAt(index);
      if(Character.isWhitespace(c) 
          || Character.isSpaceChar(c)) {
        while(index < builder.length()-1) {
          c = builder.charAt(index+1);
          if(Character.isWhitespace(c) 
              || Character.isSpaceChar(c)) {
            builder.deleteCharAt(index+1);
            continue;
          }
          break;
        }
      } else if(Character.isUpperCase(c)) {
        builder.setCharAt(index, c);
      }
      index++;
    }
    while(builder.length() > 0) {
      char c = builder.charAt(0);
      if(Character.isWhitespace(c) 
          || Character.isSpaceChar(c)) {
        builder.deleteCharAt(0);
        continue;
      }
      break;
    }
    
    while(builder.length() > 0) {
      char c = builder.charAt(builder.length() - 1);
      if(Character.isWhitespace(c) 
          || Character.isSpaceChar(c)) {
        builder.deleteCharAt(builder.length() - 1);
        continue;
      }
      break;
    }
    return builder.toString();
  }
  
  static public class Word {
    
    private int end;
    private String value;
    private int length;
    
    public Word(String value, int end, int length) {
      this.value = value;
      this.end = end;
      this.length = length;
    }
    
    public int getEnd() { return end; }
    public void setEnd(int end) { this.end = end; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    public int getLength() { return length; }
    public void setLength(int length) { this.length = length; }
  }

}
