/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp.impl;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 19, 2011  
 */
public class TextProcessor {
  public static String nextWord(String lower, int index) {
    while(index < lower.length()) {
      char c = lower.charAt(index);
      if(Character.isWhitespace(c) 
          || Character.isSpaceChar(c)) {
        index++;
        continue;
      }
      if(Character.isLetterOrDigit(c)) break;
      return null;
    }
    
    int start = index;
    if(start >= lower.length()) return null;
    
    while(index < lower.length()) {
      char c = lower.charAt(index);
      if(Character.isLetterOrDigit(c)) {
        index++;
        continue;
      }
      break;
    }
    return lower.substring(start, index);
  }
  
  public static String previousWord(String lower, int index) {
    while(index > -1) {
      char c = lower.charAt(index);
      if(Character.isWhitespace(c) 
          || Character.isSpaceChar(c)) {
        index--;
        continue;
      }
      if(Character.isLetterOrDigit(c)) break;
      return null;
    }
    
    int end = index + 1;
    if(end <= 0) return null;
    
    while(index > 0) {
      char c = lower.charAt(index);
      if(Character.isLetterOrDigit(c)) {
        index--;
        continue;
      }
      index++;
      break;
    }
//    System.out.println(lower.substring(0, index));
    return lower.substring(index, end);
  }
}
