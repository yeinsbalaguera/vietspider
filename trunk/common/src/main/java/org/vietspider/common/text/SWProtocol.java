/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.common.text;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 17, 2008  
 */
public class SWProtocol {
  
  public final static String HTTP ="http://";
  
  public final static int LENGTH = HTTP.length();
  
  public final  static boolean isHttp(String value) {
    int index = 0;
    int counter = 0;
    while(index < value.length()) {
      char c = value.charAt(index);
      if(Character.isWhitespace(c) || Character.isWhitespace(c)) {
        if(counter > 0) return false;
        index++;
        continue;
      } 
      switch (counter) {
      case 0:
        if(c != 'h' && c != 'H') return false;
        break;
      case 1:
      case 2:
        if(c != 't' && c != 'T') return false;
        break;
      case 3:
        if(c != 'p' && c != 'P') return false;
        break;
      case 4:
        if(c == 's' || c == 'S') {
          counter--;
        } else if(c != ':') {
          return false;
        }
        break;
      case 5:
      case 6:
        if(c != '/') return false;
        break;
      default:
        break;
      }
      counter++;
      index++;
      if(counter > 6) return true;
    }
    return false;
  }
  
  public final static int lastIndexOf(String value) {
    int index = 0;
    int counter = 0;
    while(index < value.length()) {
      char c = value.charAt(index);
      if(Character.isWhitespace(c) || Character.isWhitespace(c)) {
        if(counter > 0) return -1;
        index++;
        continue;
      } 
      switch (counter) {
      case 0:
        if(c != 'h' && c != 'H') return -1;
        break;
      case 1:
      case 2:
        if(c != 't' && c != 'T') return -1;
        break;
      case 3:
        if(c != 'p' && c != 'P') return -1;
        break;
      case 4:
        if(c == 's' || c == 'S') {
          counter--;
        } else if(c != ':') {
          return -1;
        }
        break;
      case 5:
      case 6:
        if(c != '/') return -1;
        break;
      default:
        break;
      }
      counter++;
      index++;
      if(counter > 6) return index;
    }
    return -1;
  }
  
  public final static int lastIndexOfWWW(String value, int start) {
    if(value.length() < start+4) return -1;
    char c = value.charAt(start);
    if(c != 'w' && c != 'W') return -1;
    c = value.charAt(start+1);
    if(c != 'w' && c != 'W') return -1;
    c = value.charAt(start+2);
    if(c != 'w' && c != 'W') return -1;
    return start+3;
  }
  
  public final static int lastRealValueURL(String value, boolean ignoreWWW) {
    int start = lastIndexOf(value);
    if(start < 0) start = 0;
    if(ignoreWWW) return start;
    int idx = lastIndexOfWWW(value, start);
    if(idx < start) return start;
    idx = value.indexOf('.', idx);
    return (idx < 0) ? start : idx + 1;
  }
  
  public static void main(String[] args) {
    System.out.println(isHttp("  HTTPs://asfasdasd.com"));
    String value = "  HTTp://wwW.asfasdasd.com/dd";
    int index = lastIndexOf(value);
    System.out.println(value.substring(index));  
    
    index = lastIndexOfWWW(value, index);
    System.out.println(index);
    
    System.out.println(" == > "+ lastIndexOfWWW("www.vietnamnet", 0));
  }
  
}
