/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.index.analytics;

import org.vietspider.db.dict.VietnameseDictionary;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 22, 2009  
 */
public class Analyzer extends TextAnalyzer {
   
  public boolean isNotEmpty(String text) {
    int i = 0;    
    while(i < text.length()){
      char c = text.charAt(i);
      if(Character.isLetterOrDigit(c)) return false;
      i++;
    }
    return true;
  }
  
  public Word search(String text, int index, int size, int min) {
//    System.out.println(text+ " / "+ size+ " / "+ min);
    while(size > min) {
      Word word = searchWord(text, index, size);
      if(word.getValue() != null) return word;
      size = word.getLength() - 1;
    }
    if(size >= 2) return null;
    TextAnalyzer.Word word  = next(text, index, size);
    String value = clean(text.substring(index, Math.min(word.getEnd(), text.length())));
    word.setValue(value);
    return word;
  }
  
  
  public  Word searchWord(String text, int index, int size) {
    TextAnalyzer.Word word = next(text, index, size);
    String value = clean(text.substring(index, Math.min(word.getEnd(), text.length())));
//    System.out.println("====> " + value + " / "/*+VietnameseDictDatabase.getInstance().contains(value)*/);
    if(VietnameseDictionary.getInstance().contains(value) 
        || VietnameseDictionary.getInstance().contains(value.toLowerCase())) {
      word.setValue(value);
      return word;
    }
    
    return word;
  }
  
  public Word next(String text, int start, int total) {
    int index = start + 1;
    int counter = 0;
    int length = text.length();
    while(index < length) {
      char c = text.charAt(index);
      if(Character.isWhitespace(c) 
          || Character.isSpaceChar(c)) {
        while(index < length) {
          c = text.charAt(index);
          if(Character.isWhitespace(c) 
              || Character.isSpaceChar(c)) {
            index++;
            continue;
          }
          break;
        }
        counter++;
        if(counter >= total) break;
      }
      index++;
    }
    return  new TextAnalyzer.Word(null, index, counter);
  }
  
  
}
