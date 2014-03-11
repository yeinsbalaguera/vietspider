/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp.impl.ao;

import org.vietspider.nlp.impl.TextProcessor;



/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 17, 2011  
 */
public class AOTextProcessor {
  
  static public int indexOf(String text, String key) {
    return indexOf(text, key, 0);
  }
  
  static public int indexOf(String text, String key, int from) {
    int index = text.indexOf(key, from);
//    if("house".equals(key) && index ==8 ) {
//      new Exception().printStackTrace();
//      ActionExtractor.println("====  >"+ key + " : " + index);
//    }
    if(index < 0) return -1;
    if(index > 0 && Character.isLetterOrDigit(text.charAt(index-1))) return -1;
    int end = index + key.length();
    if(end < text.length() && Character.isLetterOrDigit(text.charAt(end))) return -1;
    
    String next = TextProcessor.nextWord(text, index + key.length());
    String nextText = text.substring(index + key.length()).trim();
    String previous = TextProcessor.previousWord(text, index - 1);
    
    if(index > 0) {
      char c = text.charAt(index-1);
      if(c == '-' || c == '_') return -1;
    }
    
    if(index < text.length() - 1) {
      char c = text.charAt(index+1);
      if(c == '-' || c == '_') return -1;
    }
    
//    ActionExtractor.print(text);
//    ActionExtractor.print(nextText);
    
//    ActionExtractor.println(previous + " : " + key + " : " + next +  " : " + "floor".equals(previous));
//    ActionExtractor.println(previous + " : " + key + " : " + next +  " : " + index);
    
    if("nhà".equals(key)) {
//      ActionExtractor.print(key + " : "+ next + " : " + previous);
      if("bạn".equals(next)
          || "kho".equals(next)
          || "xưởng".equals(next)
          || "hàng".equals(next)
          || "nghỉ".equals(next)
          || "tập".equals(next)
          || "đất".equals(next)
          || "đâu".equals(next)
          || "đầu".equals(next)) return -1;
      if("tại".equals(previous)          
          || "tòa".equals(previous)
          || "thuế".equals(previous)
          || "nhận".equals(previous)) return -1;
    } else if("bán".equals(key)) {
//      ActionExtractor.print(" === > " + previous + " : "+ "mua".equals(previous));
      if("đảo".equals(next)
          || "kính".equals(next)) return -1;
      if("buôn".equals(previous)) return -1;
      if("mua".equals(previous)) return -1;
    } else if("ban".equals(key)) {
//      ActionExtractor.print(" === > " + previous + " : "+ "mua".equals(previous) 
//        +  "\n" +  next);
      if("đầu".equals(next)) return -1;
      if("tây".equals(previous)) return -1;
    } else if("ban".equals(key)) {
      if("nu".equals(next)) return -1;
    } else if("thiết kế".equals(key)) {
      if("hiện".equals(next)) return -1;
    } else if("phòng".equals(key)) {
      if("văn".equals(previous)) return -1;
      if(nextText.startsWith("giao dịch")) return -1;
    } else if("phong".equals(key)) {
      if("van".equals(previous)) return -1;
    } else if(key.startsWith("thuê ")) {
      if("cho".equals(previous)) return -1;
    } else if("nền".equals(key)) {
      if(nextText.startsWith("kinh tế")) return -1;
      if("hình".equals(previous)) return -1;
    } else if("cho thuê".equals(key)) {
      int preIndex = text.indexOf("cần tìm");
      if(preIndex > -1 && preIndex < index) return -1;
    }
    
    return index;
  }
  
  
  static int searchEnd(String text, int start) {
    int index = start;
    while(index < text.length()) {
      char c = text.charAt(index);
      if(/*c == ',' ||*/ c == '.'
        || c == '!' || c == '?' || c == ';'
          || c == ':') {
        if(index >= text.length() - 1) return index;
        c = text.charAt(index + 1);
        if(Character.isWhitespace(c) 
            || Character.isSpaceChar(c)) return index; 
      }
      index++;
    }
    return text.length();
  }

  
}
