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
public class AreaFilter2Plugin extends  AreaFilterPlugin  {

  Point filter(TextElement element, short type) {
    String text = element.getLower();

    int start = text.indexOf("dt");
    if(start < 0) return null;

//    boolean separator = existSepatator(text, start+2);
    Point point = null;
    int index = start;
    int max  = text.indexOf("giá", start);
    if(max < 0) max = text.length();

    while(index < Math.min(max, index + 20)) {
      char c = text.charAt(index);
      //      System.out.println(c);
      if(!Character.isDigit(c)) {
        index++;
        continue;
      }
      //            System.out.println(" duoc tai day "+ text.substring(index));

      if(index - start > 30) {
        //                System.out.println(" duoc tai day "+ text.substring(index));
        break;
      }

      //      System.out.println(" duoc tai day "+ text.substring(index));

      int end = searchEnd(text, index/*, separator*/);
//      System.out.println(" ===  > "+ text.substring(start) +  " \n "+ end );
      while(end > 0) {
        String label = text.substring(start, index);
        if(label.trim().endsWith("đường")) {
          index = end+1;
          break;
        }
        //        System.out.println(" ===  > "+ text.substring(start) +  " : "+ end + " : " + separator);
        Point p = new Point(score2(label), index, end);
        if(AreaFilter.TEST) {
          System.out.println(" filter 2: "+ label + " : " + text.substring(index, end)  + " : "+  score2(label));
        }
        if(point == null) {
          point = p;
          element.putPoint(type, point);
        } else {
          point.setNext(p);
          point = p;
        }
        index = end+1;
        end = searchEnd(text, index/*, separator*/);
      }
      index++;
    }
    return point;
  }

   int searchEnd(String text, int index/*, boolean separator*/) {
    //    if(!separator) return searchEnd(text, index);
//     System.out.println(text.substring(index));

    while(index < text.length()) {
      char c = text.charAt(index) ;
      if(Character.isDigit(c)
          || c == '-' || c == '–' 
            || Character.isSpaceChar(c) 
            || Character.isWhitespace(c) ) {
        index++;
        continue;
      }

      if(c == '.' || c == ',') {
        if(index > 0) {
          c = text.charAt(index-1);
          if(Character.isWhitespace(c) 
              || Character.isSpaceChar(c)) {
            int i = next(text, index);
            i = searchEndWith(text, i);
//            System.out.println(" bibi "+ i);
            if(i < 0) return -1;
            return index;
          }
        }

        if(index < text.length()-1) {
          c = text.charAt(index+1);
          if(Character.isWhitespace(c) 
              || Character.isSpaceChar(c)) {
            int i = next(text, index);
            i = searchEndWith(text, i);
            if(i < 0) return -1;
            return index;
          }
        }
        //        int counter = count(text, index, cs);
        //        if(counter > 2) {
        //          int i = next(text, index);
        //          i = searchEndWith(text, i);
        //          if(i < 0) return -1;
        //          return index;
        //        }
        index++;
        continue;
      }
      break;
    }
    return searchEndWith(text, index);
  }

  //  private int count(String text, int index, char separator) {
  //    int counter = 0;
  //    while(index < text.length()) {
  //      char c = text.charAt(index) ;
  //      if(separator == c) counter++;
  //      index++;
  //    }
  //    return counter;
  //  }

  private int next(String text, int index) {
    while(index < text.length()) {
      char c = text.charAt(index) ;
      if(Character.isDigit(c)
          || c == '-' || c == '–'
            || c == '.' || c == ','
              || Character.isSpaceChar(c) 
              || Character.isWhitespace(c) ) {
        index++;
        continue;
      }
      break;
    }
    return index;
  }

  private int searchEndWith(String text, int index) {
    if(index >= text.length()) return -1;
    char c = text.charAt(index) ;
    
//    System.out.println(text.substring(index));

    if(c == 'h' && index < text.length() - 1
        && text.charAt(index+1) == 'a') return index+2;

    if(c == 'x'
      || c == '*') return searchEnd2(text, index);

    if(c != 'm' ) return -1;
    index++;

//            System.out.println(text.substring(index));

    while(index < text.length()) {
      c = text.charAt(index) ;
      if(c == '&' 
        && index < text.length() - 4
        && text.charAt(index+1) == 's'
          && text.charAt(index+2) == 'u'
            && text.charAt(index+3) == 'p') {
        index += 4;
        continue;
      }
//            System.out.println("====  >"+ c);
      if(c == ',' || c == '.') {
        if(index < text.length() - 1) {
          c = text.charAt(index+1);
          if(Character.isWhitespace(c) 
              || Character.isSpaceChar(c)) return index;
        }
      }

      if(c == 'x' 
          || c == '*') {
        return searchEnd2(text, index);
      }
      
      if( (c == 'é' || c ==  'e') 
        && index < text.length() - 1
        && text.charAt(index+1) == 't') {
        index += 2;
        continue;
      }
//            System.out.println("====> " + c + " : "+ (c == '2'));
      if(c == '2' 
        || c == '²') return index + 1;
      else if(!Character.isSpaceChar(c) 
          && !Character.isWhitespace(c)) break;
      index++;
    }
    
    return -1;
  }

  private int searchEnd2(String text, int index) {
    index++;
//    System.out.println(text.substring(index));
    while(index < text.length()) {
      char c = text.charAt(index);
      if(Character.isDigit(c) 
          || Character.isWhitespace(c) 
          || Character.isSpaceChar(c)
          || c == 'm' || c == '²') {
        index++;
        continue;
      }
      
      if(c == 't') {
        index--;
        while(index > 0) {
          c = text.charAt(index);
          if(Character.isDigit(c)
              || Character.isWhitespace(c)
              || Character.isSpaceChar(c)) {
            index--;
            continue;
          }
          break;
        }
//        System.out.println(text.substring(index));
        if(c == 'x') return index ;
        return -1;
      }

//                  System.out.println("bebebe "+c);
//      System.out.println(text.substring(index));
      if(c == '.' || c == ',') {
        if(index < text.length() - 1) {
          c = text.charAt(index+1);
          if(Character.isWhitespace(c) 
              || Character.isSpaceChar(c)) return index;
          index++;
          continue;
        }
        if(index ==  text.length() - 1) return index;
      }
      
      if( (c == 'é' || c == 'e') 
          && index < text.length() - 1
          && text.charAt(index+1) == 't') { 
        return index+2;
      }
      break;
    }
    return -1;
  }

}
