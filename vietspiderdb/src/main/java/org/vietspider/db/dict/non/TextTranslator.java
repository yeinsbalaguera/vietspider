/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db.dict.non;

import org.vietspider.index.analytics.TextAnalyzer;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 15, 2009  
 */
public class TextTranslator extends TextAnalyzer {

  public String compile(String text) {
    StringBuilder builder = new StringBuilder();
    int index = 0;
    int length = text.length();

    while(index < length) {
      char c = text.charAt(index);
      while(!Character.isLetterOrDigit(c)) {
        builder.append(c);
        index++;
        if(index >= length) break;
        c = text.charAt(index);
      }

      Word word = search(builder, text, index, 25);

      //      System.out.println(" ra ngoai "+ data.text + " ==  >"+ text.charAt(data.end));
      builder.append(word.getValue());
      index = word.getEnd();
      if(index > 0 && index < length) {
        char c1 = text.charAt(index - 1);
        if(Character.isWhitespace(c1)
            || Character.isSpaceChar(c1)) builder.append(c1);
      }
//      index++;
    }

    return builder.toString();
  }

 /* private Data next(String text, int start, int from) {
    int end = searchEnd(text, from);
    //    if(end < 0) end  = text.length();
    String key = text.substring(start, end);
    NWord nWord = NonSignDictionary.getInstance().search(key.toLowerCase());
    System.out.println("==== >"+key +" / "+ nWord + " : "+ end);
    if(nWord != null) {
      if(end >= text.length()) {
        return new Data(valueOf(text, nWord), end);
      }

      Data newData = next(text, start, end+1);
      if(newData != null) return newData;
      return new Data(valueOf(text, nWord), end);
    }
    if(start == from) return new Data(key, end);
    return null;
  }
*/
 /* private int searchEnd(String text,  int from) {
    int index = from;
    while(index < text.length()) {
      char c = text.charAt(index);
      if(!Character.isLetterOrDigit(c)) break;
      index++;
    }
    return index;
  }*/

  private String valueOf(StringBuilder builder,  NWord nWord) {
//    System.out.println(" dang check "+nWord.getKey());
    NWordValue [] nWordValues = nWord.getValues();
    for(int i = 0; i < nWordValues.length; i++) {
      String [] refs = nWordValues[i].getRefs();
      for(int j = 0; j < refs.length; j++) {
        if(builder.indexOf(refs[j]) > -1) return nWordValues[i].getValue(); 
      }
    }
    if(nWordValues.length > 0) return nWordValues[0].getValue();
    return nWord.getKey();
  }

  Word search(StringBuilder result, String text, int index, int size) {
    //  System.out.println(text+ " / "+ size+ " / "+ min);
    while(size > 0) {
      Word word = searchWord(result, text, index, size);
      if(word.getValue() != null) return word;
      size = word.getLength() - 1;
    }
    Word word = next(text, index, size);
    word.setValue(clean(text.substring(index, Math.min(word.getEnd(), text.length()))));
    return word;
  }

  Word searchWord(StringBuilder result, String text, int index, int size) {
    Word word = next(text, index, size);
    String value = clean(text.substring(index, Math.min(word.getEnd(), text.length())));
//    System.out.println("=====> " +value + " : "+ size );
    NWord nWord = NonSignDictionary.getInstance().search(value.toLowerCase());
    if(nWord != null) return new Word(valueOf(result, nWord), word.getEnd(), size);
    return word;
  }
  
  Word next(String text, int start, int total) {
    int index = start + 1;
    int counter = 0;
    int length = text.length();
    while(index < length) {
      char c = text.charAt(index);
      if(c == ' ') {
        while(c == ' ') {
          index++;
          if(index >= text.length()) break;
          c = text.charAt(index);          
        }
        counter++;
        if(counter >= total) break;
      } 
      if(!Character.isLetter(c)) break;
      index++;
    }
    return new Word(null, index, counter);
  }
  
}
