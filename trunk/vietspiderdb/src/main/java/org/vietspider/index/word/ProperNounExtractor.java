/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.index.word;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.index.analytics.Analyzer;
import org.vietspider.index.analytics.PhraseData;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 21, 2009  
 */
public class ProperNounExtractor extends Analyzer {
  
  public List<PhraseData> extract(List<PhraseData> sequences) {
    List<PhraseData> values = new ArrayList<PhraseData>();
    for (int i = 0; i < sequences.size(); i++) {
      PhraseData phrase = sequences.get(i);
      if(phrase.isAtomic()) {
        values.add(phrase);
      } else {
        values.addAll(extract(phrase));
      }
    }
    return values;
  }
  
  public  List<PhraseData> extract(PhraseData data) {
    String text = data.getValue();
    List<PhraseData> sequences = new ArrayList<PhraseData>();
    int i = 0;
    int start = 0;
    int length = text.length();
    while( i < length){
      char c = text.charAt(i);
      if(Character.isUpperCase(c)) {
        int s = i;
        Word word = search(text, i, 25, 2);
        if(word != null ) {
          sequences.add(new PhraseData(false, text.substring(start, s), 0));
//          sequences.add(text.substring(start, s));
          sequences.add(new PhraseData(true, word.getValue(), word.getLength()));
//          nouns.add(word.value);
          i = word.getEnd() + 1;
          start = i-1;         
        } else {
          int [] values = searchEnd(text, i+1, 1);
          if(values[1] > 1) {
            int e = values[2];
//          try {
            sequences.add(new PhraseData(false, text.substring(start, s), 0));
//            } catch (Exception exp) {
//              exp.printStackTrace();
//            }
//            sequences.add(text.substring(start, s));
//            nouns.add(text.substring(s, e));
            sequences.add(new PhraseData(true, text.substring(s, e), 0));
            start = e+1;
          } 
          i = values[2] + 1;
        }
       
        continue;
      }
      i++;
    }
    if(start < 1) {
      sequences.add(new PhraseData(false, text, 0));
    } else if(start < length) {
      sequences.add(new PhraseData(false, text.substring(start, length), 0));
    }
    
    return sequences;
  }
  
  private int[] searchEnd(String text, int index, int counter) {
    int length = text.length();
    while(index < length) {
      char c = text.charAt(index);
      index++;
      if(Character.isWhitespace(c) || Character.isSpaceChar(c)) break;
    }
    if(index >= length) return new int[]{index, counter, length};
    char c = text.charAt(index);
    if(Character.isDigit(c) 
        || (Character.isLetter(c) && Character.isUpperCase(c))
        ) {
     return searchEnd(text, index+1, counter+1);
    }
    return new int[]{index, counter, index-1};
  }
  
  
}
