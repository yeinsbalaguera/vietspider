/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db.dict.non;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.index.analytics.TextAnalyzer;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 18, 2009  
 */
public class DictSequenceSplitter extends TextAnalyzer {
  
  private DictVietnameseDataChecker checker = new DictVietnameseDataChecker();

  public List<String> split(String text) {
    List<String> sequences = new ArrayList<String>();
    int i = 0;
    int start = 0;
    int length = text.length();
    while(i < length){
      char c = text.charAt(i);
      if(Character.isLetter(c) || c == ' ') {
        i++;
        continue;
      }
      String seq = clean(text.substring(start, i));
      if(isValid(seq)) sequences.add(seq);
      start = i+1;
      while(start < length) {
        c = text.charAt(start);
        if(Character.isLetter(c) || c == ' ') break;
        start++;
      }
      i = start+1;
      i++;
    }
    
    i = Math.min(length, i);
    String seq = clean(text.substring(start, i));
    if(isValid(seq)) sequences.add(seq);
    return sequences;
  }
  
  private boolean isValid(String text) {
    text = text.trim();
    if(text.length() < 2 
        || text.indexOf(' ') < 0) return false; 
    if(!checker.checkTextData(text)) return false;
    int i = 0;
    while(i < text.length()) {
      char c = text.charAt(i);
      if(!(Character.isLetter(c) 
          || Character.isWhitespace(c))) return false;
      i++;
    }
    return isTextNotMarked(text);
  }
  
  private boolean isTextNotMarked(String text) {
    for(int i = 0; i < text.length(); i++) {
      char c = text.charAt(i);
      switch (c) {
      case 'À':
      case 'Á':
      case 'Ả':
      case 'Ã':
      case 'Ạ': 
        
      case 'Ă': 
      case 'Ằ': 
      case 'Ắ': 
      case 'Ẳ': 
      case 'Ẵ': 
      case 'Ặ': 
        
      case 'Â': 
      case 'Ầ': 
      case 'Ấ': 
      case 'Ẩ': 
      case 'Ẫ': 
      case 'Ậ':
        return true;
        
      case 'à':
      case 'á':     
      case 'ả':
      case 'ã':
      case 'ạ':
        
      case 'ă': 
      case 'ằ': 
      case 'ắ': 
      case 'ẳ': 
      case 'ẵ': 
      case 'ặ':
        
      case 'â': 
      case 'ầ': 
      case 'ấ': 
      case 'ẩ': 
      case 'ẫ': 
      case 'ậ':
        return true;
        
      case 'Đ': 
        return true;
        
      case 'đ': 
        return true;
      case 'È': 
      case 'É': 
      case 'Ẻ': 
      case 'Ẽ': 
      case 'Ẹ':
        
      case 'Ê':
      case 'Ề': 
      case 'Ế': 
      case 'Ể': 
      case 'Ễ': 
      case 'Ệ':
        return true;
        
      case 'è': 
      case 'é': 
      case 'ẻ': 
      case 'ẽ': 
      case 'ẹ': 
        
      case 'ê':
      case 'ề': 
      case 'ế': 
      case 'ể': 
      case 'ễ': 
      case 'ệ':
        return true;
        
      case 'Ì': 
      case 'Í': 
      case 'Ỉ': 
      case 'Ĩ': 
      case 'Ị':
        return true;
        
      case 'ì': 
      case 'í': 
      case 'ỉ': 
      case 'ĩ': 
      case 'ị':
        return true; 
      case 'Ò': 
      case 'Ó': 
      case 'Ỏ': 
      case 'Õ': 
      case 'Ọ':
        
      case 'Ô':
      case 'Ồ': 
      case 'Ố': 
      case 'Ổ': 
      case 'Ỗ': 
      case 'Ộ':
        
      case 'Ơ': 
      case 'Ờ': 
      case 'Ớ': 
      case 'Ở': 
      case 'Ỡ': 
      case 'Ợ': 
        return true;
        
      case 'ò': 
      case 'ó': 
      case 'ỏ': 
      case 'õ': 
      case 'ọ': 
        
      case 'ô':
      case 'ồ': 
      case 'ố': 
      case 'ổ': 
      case 'ỗ': 
      case 'ộ': 
        
      case 'ơ': 
      case 'ờ':
      case 'ớ': 
      case 'ở': 
      case 'ỡ': 
      case 'ợ': 
        return true;
      case 'Ù': 
      case 'Ú': 
      case 'Ủ': 
      case 'Ũ': 
      case 'Ụ':
        
      case 'Ư': 
      case 'Ừ': 
      case 'Ứ': 
      case 'Ử': 
      case 'Ữ': 
      case 'Ự':
        return true;
        
      case 'ù': 
      case 'ú': 
      case 'ủ':
      case 'ũ': 
      case 'ụ': 
        
      case 'ư': 
      case 'ừ':
      case 'ứ': 
      case 'ử': 
      case 'ữ': 
      case 'ự':
        return true;
        
      case 'Ỳ': 
      case 'Ý': 
      case 'Ỷ':
      case 'Ỹ': 
      case 'Ỵ': 
        return true;
        
      case 'ỳ': 
      case 'ý': 
      case 'ỷ': 
      case 'ỹ': 
      case 'ỵ': 
        return true;
      default: 
      break;
      }
    }
    return false;
  }
  
}
