/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.db.dict.non.NWord;
import org.vietspider.db.dict.non.NonSignDictionary;
import org.vietspider.index.analytics.TextAnalyzer;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 14, 2009  
 */
public class TextTranslator2 extends TextAnalyzer {

  public List<String> translate(String text) {
    int i = 0;
    int length = text.length();
    TranslateNode node = new TranslateNode("");
    
    while( i < length){
      TranslateKey key = search(text, i, 25);
      if(key != null ) {
        node.addKey(key);
        i = key.getEnd();
      } else {
        int [] values = searchEnd(text, i);
        node.addKey(text.substring(values[0], values[1]));
        i = values[1] + 1;
      }
    }
    
    List<StringBuilder> builders = node.build();
    List<String> list = new ArrayList<String>(builders.size());
    for(int k = 0; k < builders.size(); k++) {
      list.add(builders.get(k).toString());
    }

    return list;
  }
  
  private int[] searchEnd(String text, int start) {
    int index = start + 1;
    int length = text.length();
    while(index < length) {
      char c = text.charAt(index);
      if(Character.isWhitespace(c) 
          || Character.isSpaceChar(c)) {
        index++;
        continue;
      }
      break;
    }
    
    while(index < length) {
      char c = text.charAt(index);
      index++;
      if(Character.isWhitespace(c) || Character.isSpaceChar(c)) break;
    }
    
    return new int[]{start, index};
    
  }

  TranslateKey search(String text, int index, int size) {
//    System.out.println("================================");
    while(size > 0) {
      TranslateKey key = searchWord(text, index, size);
//      String sub  = text.substring(key.getStart(), key.getEnd());
//      System.out.println(sub + " / "+ size+ " / " + " key "+ key.getLength());
      if(key.getValue() != null) return key;
      size = key.getLength();
      size--;
      
    }
    return null;
  }
  
  TranslateKey searchWord(String text, int index, int size) {
    TranslateKey key = next(text, index, size);
    String value = clean(text.substring(key.getStart(), key.getEnd()));
      //clean(text.substring(index, Math.min(key.getEnd(), text.length())));
//    System.out.println(" vao day "+ value);
    NWord nword = NonSignDictionary.getInstance().search(value.toLowerCase());
//    System.out.println("====>" + value + "/" + nword);
    if(nword != null) key.setValue(nword);
    return key;
  }
  
  TranslateKey next(String text, int start, int total) {
    int index = start + 1;
    int length = text.length();
    while(index < length) {
      char c = text.charAt(index);
      if(Character.isWhitespace(c) 
          || Character.isSpaceChar(c)) {
        index++;
        continue;
      }
      break;
    }
    
    int counter = 1;
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
        if(counter >= total) break;
        counter++;
      }
      index++;
    }
    return  new TranslateKey(start, index, counter);
  }
  

}
