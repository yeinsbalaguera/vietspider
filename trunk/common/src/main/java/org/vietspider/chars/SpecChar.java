/*
 * Copyright 2004-2006 The VietSpider        All rights reserved.
 *
 * Created on January 24, 2006, 7:49 PM
 */

package org.vietspider.chars;

/**
 *
 * @author nhuthuan
 * Email: nhudinhthuan@yahoo.com
 */
public class SpecChar {
  
  public final static char s = ' ';
  public final static char t = '\t';
  public final static char n = '\n';
  public final static char b = '\b';
  public final static char f = '\f';
  public final static char r = '\r';
  
  public final static char END_TAG1 = '/', OPEN_TAG1 = '<', CLOSE_TAG1 = '>' ; 
  
  public final static char  HYPHEN1 = '-', QUESTION_MASK1 = '?',  PUNCTUATION_MASK1 ='!';
  public final static char  EQUALS1 = '=', SINGLE_QUOTATION_MASK1 = '\'',  DOUBLE_QUOTATION_MASK1='"';
  
  
  public final static char [] START_CDATA1 = "[CDATA[".toCharArray();
  public final static char [] END_CDATA1 = "]]>".toCharArray();
  
  public final static char [] START_COMMENT1 = "--".toCharArray();
  public final static char [] END_COMMENT1 = "-->".toCharArray();
  
  public final static char [] START_DOCTYPE1 = "DOCTYPE".toCharArray();
  public final static char [] END_DOCTYPE1 = ">".toCharArray();
    
  public static int findWhiteSpace(String value, int start){    
    for(int i=start; i<value.length(); i++){  
      if(Character.isWhitespace(value.charAt(i))) return i;      
    }       
    return value.length();
  }    
}
