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
public abstract class AreaFilterPlugin {

  abstract Point filter(TextElement element, short type);

  int searchEnd(String text, int index) {
    index = nextDigit(text, index);
    if(index < 0) return -1;
    
    if(index >= text.length()) return -1;
    char c = text.charAt(index);
    
    while(index < text.length()) {
      c = text.charAt(index) ;
      if(c == '(' || isDigitValue(c)
          || c == 'x' || c == '*') { 
//        || Character.isSpaceChar(c) 
//          || Character.isWhitespace(c)) {
        index++;
        continue;
      }
      break;
    }
    
//    System.out.println(" ===  >" +c );

    if(c == 'm' ) {
      index++;
    } else if(c == 'h' && index < text.length() - 1
        && text.charAt(index+1) == 'a') {
      if(index < text.length() -2 
          && Character.isLetterOrDigit(text.charAt(index+2))) return -1;
      return index+2;
    } else {
      return -1;
    }

    //    System.out.println(text.substring(index));

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
//            System.out.println("====> " + c + " : "+ (c == '2'));
      if(c == '2' 
        || c == '²') return index + 1;
      else if(!Character.isSpaceChar(c) 
          && !Character.isWhitespace(c)) break;
      index++;
    }
    return -1;
  }

  String extractPreviousWord(String text, int index) {
    if(index >= text.length()) return "";
    while(index > 0) {
      char c = text.charAt(index);
      if(!Character.isWhitespace(c) 
          && !Character.isSpaceChar(c)) break;
      index--;
    }

    int end = index+1;

    while(index > 0) {
      char c = text.charAt(index);
      if(!Character.isLetterOrDigit(c)) break;
      index--;
    }

    return text.substring(index+1, end);
  }
  
  String extractNextWord(String text, int index) {
    int start = index;
    while(index < text.length()) {
      char c = text.charAt(index);
      if(!Character.isWhitespace(c) 
          && !Character.isSpaceChar(c)) break;
      index++;
    }
    return text.substring(start, index);
  }

  boolean existSepatator(String text, int index) {
    while(index < text.length()) {
      char c = text.charAt(index);
      if(Character.isSpaceChar(c) 
          || Character.isWhitespace(c)) {
        index++;
        continue;
      }
      if(c == ':') return true;
      return false;
    }
    return false;
  }

  TextElement previous(TextElement element) {
    TextElement pre = element.previous();
    if(pre == null) return null;
    if(pre.getValue().trim().isEmpty()) return previous(pre);
    return pre;
  }

  final int score(String key) {
//    if(key.indexOf("diện tích sử dụng") > -1
//        || key.indexOf("dtsd") > -1) return -1;
    key = cutKey(key);
//    System.out.println(key +  " : "+ key.length());
    if(!key.startsWith("diện tích") 
        && key.length() > 20) {
      return -1;
    }
    
//    if(key.indexOf("diện tích sử dụng") > -1) return -1;
    
    if(key.length() > 0 
        && key.charAt(key.length() - 1) == '+') return -1;
    
//    System.out.println(key);
    
    if(hasIgnoreTag(key)) return -1;

//    if(key.indexOf("dự án") > -1
//        || key.indexOf("sàn") > -1
//        || key.indexOf("xây dựng") > -1
//        || key.indexOf("tòa nhà") > -1) return 1;

    return 4;
  }
  
  final int score2(String key) {
    if(hasIgnoreTag(key)) return -1;
    
    key = key.trim();
    if(key.length() > 0 
        && key.charAt(key.length() - 1) == 'x') return -1;
    
    if(key.endsWith("mt")) return -1;
    
    return 4;
  }
  
  boolean hasIgnoreTag(String key) {
//    new Exception().printStackTrace();
//    System.out.println(key + " : ");
//    System.out.println(" ============ >"+ key);
    int idx = key.lastIndexOf(',');
    if(idx > 0) key = key.substring(idx+1);
    
    key = key.trim();
    
//    System.out.println(" ============ >"+ key);
    
    if(key.indexOf("khuôn viên") > -1
        || key.indexOf("quy hoạch") > -1
        || key.indexOf("tầng hầm") > -1
        || key.indexOf("khu ở") > -1
        || key.indexOf("mặt tiền") > -1
        || key.indexOf("dự án") > -1
        || key.indexOf("toàn khu") > -1
        || key.indexOf("khu đô thị") > -1
        || key.indexOf("tự nhiên") > -1
        || key.endsWith("đường nhựa") 
        || key.endsWith("đường")
        || key.endsWith("sân")
        || key.endsWith("có thêm")
        || key.indexOf("sân để xe") > -1
        || key.indexOf("nội dung") > -1
        || key.indexOf("diện tích sử dụng") > -1
        || key.indexOf("diện tích xây dựng") > -1 
        || key.indexOf("tổng diện tích") > -1
        || key.indexOf("nhà xây mb") > -1
        || key.indexOf("tầng 2") > -1
        || key.indexOf("tầng 3") > -1
        || key.indexOf("dtsd") > -1
        || key.indexOf("sân rộng") > -1
        || key.indexOf("dtxd") > -1
        || key.endsWith(" xd")
        || key.indexOf("dôi dư khoảng") > -1
        || (key.indexOf("cách đường") > -1 && key.indexOf("khoảng") > -1)
        || key.indexOf("sàn xây dựng") > -1) return true;
    return false;
  }
  
  int nextDigit(String text, int index) {
    while(index < text.length()) {
      char c = text.charAt(index) ;
      if(Character.isDigit(c) 
          || Character.isWhitespace(c) 
          || Character.isSpaceChar(c)
          || c == '-' || c == '–' || c == ';') {
        index++;
        continue;
      }
      
      if(c == '.' || c == ',') {
        if(index > 0) {
          c = text.charAt(index-1);
          if(Character.isWhitespace(c) 
              || Character.isSpaceChar(c)) return -1;
        }
        
        if(index < text.length()-1) {
          c = text.charAt(index+1);
          if(Character.isWhitespace(c) 
              || Character.isSpaceChar(c)) return -1;
        }
        index++;
        continue;
      }
      break;
    }
    return index;
  }
  
  boolean isDigitValue(char c) {
    return Character.isDigit(c) 
    || Character.isWhitespace(c) 
    || Character.isSpaceChar(c)
    || c == '.' || c == ',' || c == '-' || c == '–';
  }
  
  boolean isAreaValue(String text, int index) {
    char c = text.charAt(index);
    if(Character.isDigit(c) 
        || Character.isWhitespace(c) 
        || Character.isSpaceChar(c)
        || c == '-' || c == '–' || c == '(' || c == ')'
          || c == 'm' || c == '²') return true;
    
    if(c == '.' || c == ',') {
      if(index > 0) {
        c = text.charAt(index-1);
        if(Character.isWhitespace(c) 
            || Character.isSpaceChar(c)) return false;
      }
      if(index < text.length()-1) {
        c = text.charAt(index+1);
        if(Character.isWhitespace(c) 
            || Character.isSpaceChar(c)) return false;
      }
      return true;
    }
    return false;
  }
  
  private String cutKey(String key) {
    int index1 = key.lastIndexOf(", ");
    int index2 = key.lastIndexOf(" (");
    int index = -1;
    if(index1 > -1 && index2 < 0) index = index1;
    else if(index1 < 0 && index2 > -1) index = index2;
    else if(index1 > -1 && index2 > index1) index = index2;
    else if(index2 > -1 && index1 > index2) index = index1;
    if(index < 0) return key;
    String value = key.substring(index+2);
    if(value.length() < 4) return key; 
    return value;
  }
  
}
