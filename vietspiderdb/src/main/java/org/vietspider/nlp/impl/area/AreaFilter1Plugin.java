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
public class AreaFilter1Plugin extends  AreaFilterPlugin {
  
  Point filter(TextElement element, short type) {
    String text = element.getLower();

    int start = 0;
    Point point = null;
    while(start < text.length()) {
      start = text.indexOf("diện tích", start);
      if(start < 0) break;
//                System.out.println(text.substring(start));
      int index = start;
      int priceIndex  = text.indexOf("giá", start);
      int areaIndex = text.indexOf("diện tích", start+1);
      int max = -1;
      if(priceIndex < 0 && areaIndex > 0) {
        max = areaIndex;
      } else if(priceIndex > 0 && areaIndex < 0) {
        max = priceIndex;
      } else if(priceIndex > 0 && areaIndex > 0) {
        max = Math.min(priceIndex, areaIndex);
      } else {
        max = text.length();
      }
//      System.out.println(text.substring(index, max));
      while(index < max) {
        char c = text.charAt(index);
        if(!Character.isDigit(c)) {
          index++;
          continue;
        }
        

        if(index - start > 100) break;
        int end = searchEnd(text, index);
//        System.out.println(text.substring(index, max) + " : ");
//        System.out.println(end);
        
        //16x17m2
        if(end  > 0 && text.charAt(end - 1) == '2') {
//          System.out.println(text.substring(index, end));
          int idx = text.indexOf('x', index);
          if(idx < 0) idx = text.indexOf('*', index);
          if(idx < end) end--;
//          System.out.println("hehe "+ idx+  " : "+ end);
        }
        
        
//        System.out.println("=====  > " + text.substring(start, index)+ " : "+ end);
        if(end  < 0) {
          index++;
          continue;
        }
        
        String label  = null;
        int idx = start - 1;
        while(idx > 0) {
          char c1  = text.charAt(idx);
          if(c1 == ',' 
            /*|| c1 == '.' 
              || c1 == ';'
                || c1 == ':'
                  || c1 == '!'
                    || c1 == '?'*/) {
            label  = text.substring(idx, index);
            break;
          }
          if(!Character.isLetterOrDigit(c1) 
              && !Character.isSpaceChar(c1)
              && !Character.isWhitespace(c1)) break;
          idx--;
        }
        if(label == null) label  = text.substring(start, index);
        
//        System.out.println(label);
        int score = score(label);
        //        if(score < 0) break;
        if(AreaFilter.TEST) {
          System.out.println(" filter 1: " + label + " = " + text.substring(index, end) + " : "+ score);
        }
        Point p = new Point(score, index, end);
        if(point == null) {
          point = p;
          element.putPoint(type, point);
        } else {
          point.setNext(p);
          point = p;
        }
        start = end;
        index = end+1;
      }
      
      start = max;
    }
    return point;
//    
  }
  
  @Override
  int searchEnd(String text, int index) {
    String nearWord = extractPreviousWord(text, index-1);
//        System.out.println("======  >"+ nearWord);
    if("từ".equals(nearWord)) {
//            int start = index;
      int end = -1;
      
      while(index < text.length()) {
//        char c = text.charAt(index) ;
//        if(!Character.isDigit(c)) {
        if(!isAreaValue(text, index)) {
//        if(!isDigitValue(c) && c != 'm') {
//                    System.out.println("ket qua "+ text.substring(start, index));
          end = index;
          break;
        }
        index++;
      }
      
      if(index >= text.length()) return index;
      
      return end;
    }

    return super.searchEnd(text, index);
  }
  
}
