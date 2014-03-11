/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp.impl.area;

import org.vietspider.nlp.impl.TextProcessor;
import org.vietspider.nlp.text.TextElement;
import org.vietspider.nlp.text.TextElement.Point;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 12, 2011  
 */
public class AreaFilter5Plugin extends  AreaFilterPlugin  {
  
  Point filter(TextElement element, short type) {
    String text = element.getLower();
    if(text.trim().isEmpty()) return null;
    
    int index = 0;
    Point point = null;
    
    while(index < text.length()) {
      char c = text.charAt(index);
      
      if(!Character.isDigit(c)) {
        index++;
        continue;
      }
      
      if(index > 2 
          && text.charAt(index-1) == '.' 
          && Character.isLetter(text.charAt(index-2)) ) {
        index++;
        continue;
      }

      int end = searchEnd(text, index);
      
      if(end  < 0) {
        index++;
        continue;
      }
      
      String label = extractLabel(text, index);
      if(label.trim().isEmpty()) {
        TextElement previous = previous(element);
        if(previous != null) label = previous.getLower();
      }
      
      if(end <= index) {
        index++;
        continue;
      }
      
//      System.out.println("=====  > " + label);
      
      int score = score(label, text.substring(index, end));
      Point p = new Point(score, index, end);
      if(AreaFilter.TEST) {
        System.out.println(" filter 5: " + label +  " : "+ text.substring(index, end) +  " : " + score);
      }
      if(point == null) {
//        if(score(label) < 0) 
        point = p;
        element.putPoint(type, point);
      } else {
        point.setNext(p);
        point = p;
      }
      index = end+1;
    }
    
    return point;
  }
  
  int score(String key, String text) {
//    int idx1 = key.lastIndexOf('.');
//    int idx2 = key.lastIndexOf(',');
//    if(idx1 > 0 && idx2 > 0) {
//      key = key.substring(Math.max(idx1, idx2) + 1);
//    } else if(idx1 > 0) {
//      key = key.substring(idx1 + 1);
//    } else if(idx2 > 0) {
//      key = key.substring(idx2 + 1);
//    }
    
    if(key.indexOf("khuôn viên") > -1
        || key.indexOf("quy hoạch") > -1
        || key.indexOf("tầng hầm") > -1
        || key.indexOf("khu ở") > -1
        || key.indexOf("thực hiện dự án") > -1
        || key.indexOf("diện tích xây dựng") > -1 
        || key.indexOf("sàn xây dựng") > -1) return -1;
    
    if(key.indexOf("diện tích") > -1
        || key.indexOf("dt") > -1
        || key.indexOf("dtích") > -1
        || key.indexOf("đất") > -1
        || key.indexOf("tầng") > -1
        || key.indexOf("mb") > -1
        || key.indexOf("nhà") > -1
        || key.indexOf("trệt") > -1
        || key.indexOf("lầu") > -1
        || key.indexOf("kích thước") > -1
        || key.indexOf("nha") > -1
        || key.indexOf("mặt bằng") > -1
//        || key.indexOf("đường") > -1
//        || key.indexOf("") > -1
        || key.indexOf("phòng") > -1) return 4;
    
    int idx = text.indexOf('x');
    if(idx > 0 && idx < text.length()-1) {
      int i = idx -1;
      char c1 = 'a';
      while(i > -1) {
        char c = text.charAt(i);
        if(Character.isSpaceChar(c) 
            || Character.isWhitespace(c) 
            || c == 'm') {
          i--;
          continue;
        }
        c1 = c;
        break;
      }
      
      char c2 = 'a';
      i = idx + 1;
      while(i < text.length()) {
        char c = text.charAt(i);
        if(Character.isSpaceChar(c) 
            || Character.isWhitespace(c)) {
          i++;
          continue;
        }
        c2 = c;
        break;
      }
      
      if(Character.isDigit(c1) && Character.isDigit(c2)
      /*&& text.charAt(text.length()-1) == 'm'*/) return 4;
      
    }
        

    return -1;
  }
  
  int searchEnd(String text, int index) {
    char temp = '0';
    while(index < text.length()) {
      char c = text.charAt(index) ;
      if(c == 'x' 
          || c == '*') {
        temp = c;
        index++;
        break;
      } else if(Character.isDigit(c) 
            || c == ',' || c == '.' || c == 'm'
            || Character.isSpaceChar(c) 
            || Character.isWhitespace(c) ) {
        index++;
        continue;
      }
      break;
    }

    if(temp == '0') return -1;
    
    while(index < text.length()) {
      char c = text.charAt(index) ;
      if(Character.isSpaceChar(c) 
            || Character.isWhitespace(c) ) {
        index++;
        continue;
      }
      break;
    }
    
    temp = 'a';
    
    while(index < text.length()) {
      char c = text.charAt(index) ;
      if(Character.isDigit(c)) {
        if(temp == 'a') temp = c;
        index++;
        continue;
      } else if(c == ',' || c == '.' || c == 'm') {
        index++;
        continue;
      }
      break;
    }
    
    if(!Character.isDigit(temp)) return -1;
    
    while(index > -1 && index < text.length()) {
      char c = text.charAt(index) ;
      if(!Character.isDigit(c) && c != 'm') {
        index--;
        continue;
      } 
      index++;
      break;
    }
    
//    System.out.println("bebe " + text.substring(index));
    
    String next = TextProcessor.nextWord(text, index);
    if("tầng".equals(next) 
        || "t".equals(next)) {
      while(index > -1) {
        char c = text.charAt(index) ;
        if(c == 'x') break;
        index--;
      }
    }
      
    
//    System.out.println("bebe " + text.substring(index));
    
    return index;
  }
  
  private String extractLabel(String text, int index) {
    int end = index;
    while(index > -1) {
      char c = text.charAt(index);
      if(c == '.' 
        && index < text.length() -1
        && Character.isWhitespace(text.charAt(index+1))) {
        return text.substring(index+1, end);
      }
      index--;
    }
    return text.substring(0, end);
  }
}
