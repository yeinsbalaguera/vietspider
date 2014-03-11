/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp.impl.area;

import org.vietspider.nlp.text.TextElement;
import org.vietspider.nlp.text.TextElement.Point;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 12, 2011  
 */
public class AreaFilter3Plugin extends  AreaFilterPlugin  {
  
  Point filter(TextElement element, short type) {
    String text = element.getLower();

    Point point = null;
    int index = 0;
//    int max  = text.indexOf("giá");
//    if(max < 0) max = text.length();
    int max = text.length();
    
//    System.out.println("|" + text+ "|");

    int start = 0;
    while(index < max) {
      char c = text.charAt(index);
      if(c == '.' 
        && index < text.length() - 1) {
        char c1 = text.charAt(index+1);
        if(Character.isWhitespace(c1) 
            || Character.isSpaceChar(c1)) start = index+2;
      }
      
      if(!Character.isDigit(c)) {
        index++;
        continue;
      }
      
//      System.out.println(text.substring(start, index));
      
      char pc = previousChar(text, index);
      if(pc == 'x' || pc == '*') {
        index++;
        continue;
      }
      
      int end = searchEnd(text, index);
      if(end  < 0) {
        index++;
        continue;
      }
      
      String label = text.substring(start, index);
      if(label.trim().isEmpty()) {
        TextElement previous = previous(element);
        if(previous != null) label = previous.getLower();
      }
      
//      System.out.println(label);
      int score = score(label, text.substring(index, end));

      Point p = new Point(score, index, end);
      if(AreaFilter.TEST) {
        System.out.println(" filter 3: "+ label + " = " + text.substring(index, end) + "  : " + score);
      }
      if(point == null) {
        point = p;
        element.putPoint(type, point);
      } else {
        point.setNext(p);
        point = p;
      }
      
      index = end+1;
      start = index;
    }
    return point;
  }
  
  private char previousChar(String text, int index) {
    index--;
    while(index >= 0) {
      char c = text.charAt(index);
      if(isDigitValue(c) ) {
        index--;
        continue;
      }
      return c;
    }
    return '1';
  }
  
  int score(String key, String text) {
//    System.out.println(key + " : "+ key.length());
    if(key.length() > 100) return -1;
    
    if(key.indexOf("dự án") > -1) {
      if(text.endsWith("m2")) {
        text = text.substring(0, text.length()-2);
        try {
          if(Integer.parseInt(text.trim()) < 200) return 4;
        } catch (Exception e) {
        }
      }
    }
    
    if(hasIgnoreTag(key)) return -1;

//    if(key.indexOf("dự án") > -1
//        || key.indexOf("sàn") > -1
//        || key.indexOf("xây dựng") > -1
//        || key.indexOf("tòa nhà") > -1) return 1;

    return 4;
  }
  
}
