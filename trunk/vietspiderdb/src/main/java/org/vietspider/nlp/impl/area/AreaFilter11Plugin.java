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
public class AreaFilter11Plugin extends AreaFilterPlugin  {

  private String [][] key_words =  {
      {"chiều rộng", "chiều dài"},
      {"rộng", "dài"}, {"ngang", "dài"},
      {"mt", "sâu"},/*, {"rộng", "thâm hậu"}*/
      {"mặt tiền","hậu"}, {"mặt đường","sâu"}, {"mđ","sâu"}
  };

  @Override
  Point filter(TextElement element, short type) {
    String text = element.getLower();

    Point point = null;
    for(int i = 0; i < key_words.length; i++) {
      Point p = search(text, i);
      if(p == null) continue;
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

  private Point search(String text, int i) {
    int start = text.indexOf(key_words[i][0]);
    int from = 0;
    while(start >= 0) {
//      System.out.println(text);
//      System.out.println(key_words[i][0] + " : "+ from);
      //    System.out.println(start);
      int from1 = start + key_words[i][0].length() + 1;
      start = back(text, start);
      int end = text.indexOf(key_words[i][1], from);
      
      if(end < 0) {
        from = from1;
//        System.out.println(" xay ra 1 "+ end);
        start = text.indexOf(key_words[i][0], from);
        continue;
      }
      
      String previous = text.substring(0, start).trim();
//      System.out.println(previous);
      if(previous.endsWith("gần đường")
          || previous.endsWith("đường trước nhà")) {
        from = from1;
//      System.out.println(" xay ra 1 "+ end);
        start = text.indexOf(key_words[i][0], from);
        continue;
      }

//      System.out.println(previous + " : "+ previous.endsWith("gần đường"));
//          System.out.println(text.substring(start, end));

      String endKey = key_words[i][1];
      if(start > end) {
        int temp  = start;
        start = end;
        end = temp;
        endKey = key_words[i][0];
      }

//      System.out.println(text.substring(start, end));

      int max = end + endKey.length();
      if(max  < text.length() && text.charAt(max) == ':') max++;
      //    System.out.println(text.charAt(max));
//                System.out.println(text.substring(start, max));
      end = searchEnd(text, max);
//                System.out.println(end);
      if(end < 0) {
//        System.out.println(" xay ra 2 "+ end);
//        System.out.println(start+ " : "+ text.substring(from));
        from = from1;
        start = text.indexOf(key_words[i][0], from);
//        System.out.println(start+ " : "+ text.substring(from));
        continue;
      }
      
      if(AreaFilter.TEST) {
        System.out.println(" filter 11: " + text);
        System.out.println(" ===  > "+ endKey);
//        System.out.println(" filter 1: "+ label + " = " + text.substring(index, end) + " : "+ score);
      }
      //      System.out.println(text.substring(start, end));

      return new Point(11, start, end);
    }
    return null;
  }

  private int back(String text, int start) {
    if(start > 0 && text.charAt(start-1) == '(') {
      start -= 2;
      while(start > 0) {
        char c = text.charAt(start);
        if(Character.isWhitespace(c) 
            || Character.isSpaceChar(c)) {
          start--;
          continue;
        }
        break;
      }

      while(start > 0) {
        char c = text.charAt(start);
        if(Character.isLetterOrDigit(c)) {
          start--;
          continue;
        }
        break;
      }
    }
    return start;
  }

  int searchEnd(String text, int index) {
    index++;
    int start = index;
    while(index < text.length()) {
      char c = text.charAt(index);
      if(Character.isDigit(c) 
          || Character.isWhitespace(c) 
          || Character.isSpaceChar(c)
          || c == 'm' || c == '²') {
        index++;
        continue;
      }

      //                  System.out.println("bebebe "+c);
      //            System.out.println(text.substring(index));
      if(c == '.' || c == ',') {
        if(index > 0 && index < text.length() - 1) {
          char c1 = text.charAt(index-1);
          char c2 = text.charAt(index+1);
          if(Character.isDigit(c1) 
              && Character.isDigit(c2)) {
            index++;
            continue;
          }
        }
        //        if(index ==  text.length() - 1) return index;
        return index;
      }

      if( (c == 'é' || c == 'e') 
          && index < text.length() - 1
          && text.charAt(index+1) == 't') { 
        return index+2;
      }
      
      if(Character.isLetter(c) && index > start) {
        return index-1;
      }
      
      break;
    }


    if(index >= text.length()) return text.length();

    return -1;
  }

}
