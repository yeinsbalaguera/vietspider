/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.index3;

import org.vietspider.chars.CharsUtil;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 26, 2009  
 */
//http://moom.vn:4529/site/cached/200909070618460097/?query=v%C3%A9+bay+r%E1%BA%BB
public class TextSearchUtils {
  
  public String normalize(String text) {
    int index = 0;
    StringBuilder builder = new StringBuilder();
    int length = text.length();
    while(index < length) {
      char  c = text.charAt(index);
      if(Character.isLetterOrDigit(c)) {
        builder.append(c);
      } else {
        builder.append(' ');
        while(index < length - 1) {
          c = text.charAt(index + 1);
          if(Character.isLetterOrDigit(c)) break;
          index++;
        }
      }
      index++;
    }
    return builder.toString().trim();
  }
  
  public int[] searchSequence(String text, String _pattern, int from) {
    String value = text.toLowerCase();
    String pattern = _pattern.toLowerCase();
    int index = pattern.indexOf(' ');
    if(index < 0) return null;
//      index = value.indexOf(pattern, from);
////      System.out.println("thay "+ pattern + "  / " + index);
//      if(index < 0) return null;
//      return new int[]{index, index + pattern.length()};
//    }
    
    String startWord = pattern.substring(0, index);
    int end = pattern.lastIndexOf(' ');
    String endWord = null; 
    if(end < 0 || end == index)  {
//      System.out.println(end + "  / "+ index);
      endWord = pattern.substring(index+1, pattern.length());
    } else {
      endWord = pattern.substring(end+1, pattern.length());
    }
    
//    System.out.println(startWord + " : "+ endWord);
    
    index = from;
    int length = value.length();
    while(index < length) {
      int start = value.indexOf(startWord, index);
//      System.out.println("start "+ startWord +  " / "+ start + " : "+ index);
      if(start < 0) break;
//      System.out.println("start "+ startWord +  " / "+ start + " : "+ index);
      end  = start+ startWord.length();
      while(true) {
        end = value.indexOf(endWord, end);
//        System.out.println(end + " / "+ start + " == > end "+ endWord);
        if(end < 0) break;
//        System.out.println(end + " / "+ start);
        String data = value.substring(start, end + endWord.length());
//        System.out.println(data);
//        data = normalize(data);
        if(CharsUtil.equals(pattern, data)) {
          return new int []{start, end + endWord.length()};
        }
        end = end + endWord.length();
      }
     
      index = start + startWord.length();
//      System.out.println("=== > "+ index + " / "+startWord);
    }
    return null;
  }
  
//  ,hiệu Hitachi hàng mới 
  
  public int back(String text, int start, int size) {
    start -= size;
    if(start < 0) return 0;
    
    while(start > 0) {
      if(!Character.isLetterOrDigit(text.charAt(start))) {
        if(start < text.length() - 1  
            && !Character.isLetterOrDigit(text.charAt(start+1))) {
          start++;
          break;
        }
      }
      start--;
    }
    return start;
  }
  
  public int next(String text, int start, int size) {
    start += size;
    if(start >= text.length()) return text.length();
    
    while(start < text.length()) {
      if(!Character.isLetterOrDigit(text.charAt(start))) {
        if(start < text.length() - 1  
            && !Character.isLetterOrDigit(text.charAt(start+1))) break;
      }
      start++;
    }
    return start;
  }
  
  
  /*public static void main(String[] args) {
    String pattern1 = ".CHỦ NHẬT hàng tuần. vé máy bay 1, VÉ MÁY BAY SIÊU RẺ... VÉ MÁY BAY SIÊU R1Ẻ,";
    String pattern2 = " chủ ..// // /nhật .. hàng tuần.   vé máy bay 1 vé máy bay siêu rẻ vé máy bay siêu r1ẻ,   ";
    System.out.println(CharsUtil.equals(pattern1, pattern2));
  }*/
}
