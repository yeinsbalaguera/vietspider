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
public class AreaFilter6Plugin extends  AreaFilterPlugin  {

  @Override
  Point filter(TextElement element, short type) {
    String text = element.getLower();
    
    int start = text.indexOf("diện tích");
//    System.out.println(text + " : "+ start);
    while(start > -1) {
      int index = start + 10;
      while(index < text.length()) {
        char c = text.charAt(index);
        if(Character.isDigit(c)) break;
        index++;
      }
//            System.out.println(text.substring(start, index));

      int end  = searchEnd(text, index, true);
//      System.out.println(end);
      if(end < 0) return null;
      int score = score(text.substring(start, index));
      
      if(score < 0) {
        start = text.indexOf("diện tích", index);
        continue;
      }
      //        if(score < 0) break;
      if(AreaFilter.TEST) {
        System.out.println(" filter 6: "  + text.substring(start, index) + " = " + text.substring(index, end));
      }
      Point point = new Point(score, index, end);
      element.putPoint(type, point);
      return point;
    }
    
    Point point = null;
    String [] keys = {"dt", "dt=", "s=", "s = "};
    for(int i = 0; i < keys.length; i++) {
      start = text.indexOf(keys[i]);
      //      if(start < 0) start = text.indexOf("s=");
      if(start < 0) continue;
      int index = start + keys[i].length();
      while(index < text.length()) {
        char c = text.charAt(index);
        if(Character.isWhitespace(c)
            || Character.isSpaceChar(c)) {
          index++;
          continue;
        }
        break;
      }
//      System.out.println(text.substring(start));

      while(index < text.length()) {
        char c = text.charAt(index);
        if(Character.isDigit(c)) break;
        index++;
      }

      int end  = searchEnd(text, index, keys[i].charAt(keys[i].length() - 1) == '=');
//      System.out.println(keys[i] + " : "+ end);
      if(end < 0) continue;
      if(AreaFilter.TEST) {
        System.out.println(" filter 6: "  + text.substring(start, index) + " = " + text.substring(index, end));
      }
      
      int score = score(text.substring(start, index));
      
      Point p = new Point(score, index, end);
      if(point == null) {
        point = p;
        element.putPoint(type, point);
      } else {
        point.setNext(p);
        point = p;
      }
    }

    return point;
  }

  int searchEnd(String text, int index, boolean trust) {
    String nearWord = extractPreviousWord(text, index-1);
    //    System.out.println("======  >"+ nearWord);
    if("từ".equals(nearWord)) {
      //      int start = index;
      while(index < text.length()) {
        char c = text.charAt(index) ;
        if(!Character.isDigit(c)) {
          //          System.out.println("ket qua "+ text.substring(start, index));
          return index;
        }
        index++;
      }
      return -1;
    }
    
    index = nextDigit(text, index);
    if(index < 0) return -1;
    
    if(index >= text.length()) return -1;

    char c = text.charAt(index) ;

    if(c == 'm' ) {
      if(index + 1 >= text.length()) return index + 1;
      if(!Character.isLetterOrDigit(text.charAt(index+1))) return index + 1;
      if(index < text.length() - 2
          && (text.charAt(index+1) == 'e' || text.charAt(index+1) == 'é')
          && text.charAt(index+2) == 't') return index + 3;
      return -1;
    } 

    if(c == 'h' && index < text.length() - 1
        && text.charAt(index+1) == 'a') return index+2;

    if(trust) return index;
    return -1;
  }

}
