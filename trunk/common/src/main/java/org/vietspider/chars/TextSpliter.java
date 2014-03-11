/***************************************************************************
 * Copyright 2003-2007 by VietSpider - All rights reserved.                *    
 **************************************************************************/
package org.vietspider.chars;

import java.util.ArrayList;
import java.util.List;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Dec 7, 2011
 */
public class TextSpliter {
  
  public String[] toArray(String value, char separator){  
    List<String> list = toList(value, separator);
    return list.toArray(new String[list.size()]);
  }

  public List<String> toList(String value, char separator) {
    List<String> temp = new ArrayList<String>((value.length()/2)+1);
    int start = 0;  
//    char [] chars = line.toCharArray();
//    int index = indexOf(chars, separator, start);
    int index = value.indexOf(separator);  // First substring
    while(index >= 0){  
      temp.add(value.substring(start, index));  
      start = index + 1;  
      index = value.indexOf(separator, start);   // Rest of substrings
//      if(start >= chars.length) break;
//      index = indexOf(chars, separator, start);
    } 
    temp.add(value.substring(start)); // Last substring  
    return temp;  
  }
  
 /* private int indexOf(char[] chars, char ch, int fromIndex) {
    int i = fromIndex;
    if (ch < Character.MIN_SUPPLEMENTARY_CODE_POINT) {
      for (; i < chars.length ; i++) {
        if (chars[i] == ch) return i;
      }
      return -1;
    }

    if (ch <= Character.MAX_CODE_POINT) {
      char[] surrogates = Character.toChars(ch);
      for (; i < chars.length; i++) {
        if (chars[i] != surrogates[0]) continue;
        if (i + 1 == chars.length) break;
        if (chars[i+1] == surrogates[1]) return i;
      }
    }
    return -1;
  }*/

}
