/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp.place;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.nlp.text.TextElement;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 12, 2011  
 */
public class PlaceFilter  {

  private final static String [] IGNORES = {
    "dt ", "tầng ", "dịch vụ", "phát triển", "quận ", "huyện "
  };

  List<String> filter(TextElement element, int max) {
    String upper = element.getValue();

    List<String> list = new ArrayList<String>();

    //    System.out.println(" === > "+ starts);

    int  index = max-1;
    int end = index;

    while(index > -1) {
      char c = upper.charAt(index);
      if(Character.isLetterOrDigit(c)) {
        end = index+1;
        break;
      }
      index--;  
    }

    while(index > -1) {
      char c = upper.charAt(index);
      if(c == '.' || c == ',') {
        if(isBreakDot(upper, index)) {
          add(list, upper.substring(index+1, end));
          break;
        }
      }

      if(Character.isDigit(c)) {
        index--;
        continue;
      }

      if(Character.isWhitespace(c) 
          || Character.isSpaceChar(c)) {
        if(index < upper.length() - 1) {
          c = upper.charAt(index+1);
          if(Character.isDigit(c) || 
              (Character.isLetter(c) && Character.isLowerCase(c))) {
            int start = upper.indexOf(' ', index+1);
            if(start > index && start < end-2) {
              add(list, upper.substring(start+1, end));
              break;
            }
          }
        }
        index--;
        continue;
      }

      if(!Character.isLetterOrDigit(c)) {
        add(list, upper.substring(index+1, end));
        break;
      }

      index--;
    }

    return list;
  }

  private void add(List<String> list, String value) {
    value = value.trim();
    if(value.startsWith("Đường ")
        || value.startsWith("MT ")
        || value.startsWith("MB ")
        || value.startsWith("HXH ")
        || Character.isDigit(value.charAt(0))
        || Character.isLowerCase(value.charAt(0))) {
      int index = value.indexOf(' ');
      value = value.substring(index+1).trim();
    }
    int index = value.indexOf(' ');
    if(index < 0 || isIgnoreValue(value)) return;
    list.add(value);
  }

  //  private int[] searchNext(String upper, int index) {
  //    int counter = 1;
  //    int start = index;
  //    while(index > -1) {
  //      
  //    }
  //    
  //    return new int[]{start, counter};
  //  }

  private boolean isBreakDot(String upper, int index) {
    char c1 = upper.charAt(index+1);
    //    if(!Character.isLetterOrDigit(c1)) return true;
    if(Character.isDigit(c1) || Character.isUpperCase(c1)) {
      if(index+2 >= upper.length()) return false;
      char c2 = upper.charAt(index+2);
      if(Character.isDigit(c2) && Character.isUpperCase(c2)) return false;
    }
    return true;
  }

  private static boolean isIgnoreValue(String value) {
    String lower = value.toLowerCase();
    for(int i = 0; i < IGNORES.length; i++) {
      if(lower.startsWith(IGNORES[i])) return true;
    }
    int index = 0;
    boolean correct = false;
    while(index < value.length()) {
      char c = value.charAt(index);
      if(Character.isLowerCase(c)) {
        correct = true;
        break;
      }
      index++;
    }
    if(!correct) return true;
    
    correct = false;
    index = 0;
    while(index < value.length()) {
      char c = value.charAt(index);
      if(Character.isUpperCase(c)) {
        correct = true;
        break;
      }
      index++;
    }
    if(!correct) return true;
    
    return false;
  }
  
  public static void main(String[] args) {
    String value = "biệt thự";
    System.out.println(isIgnoreValue(value));
  }


}
