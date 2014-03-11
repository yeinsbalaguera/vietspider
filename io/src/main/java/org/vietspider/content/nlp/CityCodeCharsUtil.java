/***************************************************************************
 * Copyright 2001-2006 VietSpider         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.vietspider.content.nlp;

import java.util.List;

/**
 * Created by VietSpider
 * Author : Nhu Dinh Thuan
 *          thuan.nhu@exoplatform.com
 * Sep 13, 2006  
 */
public final class CityCodeCharsUtil {
  
 /* final static char [] [] keys = {
    "địa chỉ".toCharArray(),
    "nơi rao".toCharArray(),
    "liên hệ".toCharArray(), 
    "địa điểm".toCharArray(),
    "liên lạc".toCharArray(),
    "tin rao vặt tại".toCharArray(), 
    "nơi đăng tin".toCharArray(), 
    "nơi đăng".toCharArray(),
    "region".toCharArray(),
    "phạm vi".toCharArray(),
    "address".toCharArray(),
    "khu vực".toCharArray(),
    "add".toCharArray(),
    "người đăng".toCharArray()
  };*/
  
  public static int indexOfIgnoreCase(char[] value, char [] c, int start){
    return indexOfIgnoreCase(value, c, start, value.length);
  }
  
  public static int indexOfIgnoreCase(char[] value, char [] c, int start, int max){
    boolean is = false;
    for(int i = start; i < max; i++){
      is = true;
      for(int j = 0; j< c.length; j++){        
        if(i+j < value.length 
           &&  Character.toLowerCase(c[j]) == Character.toLowerCase(value[i+j])) continue;
        is = false;
        break;
      }      
      if(is) return i;
    }
    return -1;
  }
  
  public static void split2List(char [][] keys, char[] chars, List<char[]> list1, List<char[]> list2) {
    int start  = 0;
    int index  = 0;
    while(index < chars.length) {
      if(chars[index] == '\n') {
        char [] sub = new char[index - start];
        System.arraycopy(chars, start, sub, 0, sub.length);
        if(isKeyTrust(keys, sub)) list1.add(sub); else  list2.add(sub);
        start = index+1;
      }
      index++;
    }
    
    if(start >= chars.length) return;
    char [] sub = new char[chars.length - start];
    System.arraycopy(chars, start, sub, 0, sub.length);
    if(isKeyTrust(keys, sub)) list1.add(sub); else  list2.add(sub);
  }
  
  private static boolean isKeyTrust(char [][] keys, char [] chars) {
    for(int i = 0; i < keys.length; i++) {
      int index = indexOfIgnoreCase(chars, keys[i], 0);
      if(index > -1) {
        if(index > 0 && Character.isLetterOrDigit(chars[index-1])) continue;
        index += keys[i].length;
        if(index < chars.length && Character.isLetterOrDigit(chars[index])) continue;
        return true;
      }
    }
    return false;
  }
  
 
}
