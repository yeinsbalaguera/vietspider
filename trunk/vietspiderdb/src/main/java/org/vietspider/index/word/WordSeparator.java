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
 * Aug 18, 2009  
 */
public class WordSeparator  extends Analyzer {
  
  public List<PhraseData> split(PhraseData input, PhraseFilter filter) {
    List<PhraseData> values = new ArrayList<PhraseData>();
    int index = 0;
    String text = input.getValue();
    while(index < text.length()) {
      Word word = search(text, index, 5, 1);
      PhraseData phrase = new PhraseData(true, word.getValue(), word.getLength());
      if(filter == null || filter.isValid(phrase)) values.add(phrase);
      index = word.getEnd();
    }
    
    for(int i = 0; i < values.size() - 1; i++) {
      PhraseData phrase1 = values.get(i);
      if(phrase1.getLength() < 2) continue;
      PhraseData phrase2 = values.get(i+1);
      PhraseData phrase = merge(phrase1, phrase2);
      if(phrase == null || phrase.getLength() < 2)  continue;
      if(filter == null || filter.isValid(phrase)) values.add(i+1, phrase);
      i++;
    }
    
    return values;
  }
  
  private PhraseData merge(PhraseData phrase1, PhraseData phrase2) {
    StringBuilder builder = new StringBuilder(phrase1.getValue());
    builder.append(' ').append(phrase2.getValue());
    int index = builder.indexOf(" ");
    builder.delete(0, index);
//    System.out.println(builder);
//    System.out.println(VietnameseDictDatabase.getInstance().contains("nhân quả"));
    int size = phrase1.getLength() + phrase2.getLength() - 1;
    Word word = search(builder.toString(), 0, size, 1);
    if(word != null) return new PhraseData(true, word.getValue(), word.getLength());
    return null;
  }
  
  
/*  private int searchNext(List<String> values, List<String> singles, int index, String word) {
    if(temp.size() < 1 || index >= singles.size()) {
      values.add(word);
      return index;
    }
    
    
    
    StringBuilder builder = new StringBuilder(word);
    while(index < singles.size()) {
      builder.append(' ').append(singles.get(index));
//      System.out.println("tim thay? "+startsWith(temp, builder.toString()));
      if(startsWith(temp, builder.toString())) {
        word = builder.toString(); 
        index++;
      } else {
//        System.out.println("chuan bi add " + word);
        values.add(word);
        break;
      }
    }
    return index;
  }*/
  
 /* private List<String> splitSingle(String text) {
    List<String> values = new ArrayList<String>();
    int i = 0;
    int start = 0;
    int length = text.length();
    while(i < length){
      char c = text.charAt(i);
      if(c == ' ') {
        String seq = text.substring(start, i);
        start = i+1;
        if(!isNotEmpty(seq)) values.add(seq.trim());
      }
      i++;
    }
    
    i = Math.min(length, i);
    String seq = text.substring(start, i);
    if(!isNotEmpty(seq)) values.add(seq.trim());
    
    return values;
  }*/
  
 /* private List<String> searchStartWith(String prefix) {
    int index = search(prefix);
    List<String> list = new ArrayList<String>();
    if(index < 0) return list;
    for(int i = index; i < dict.length; i++) {
      if(!startsWith(dict[i], prefix)) break;
      if(dict[i].length() > prefix.length() 
          && Character.isLetterOrDigit(dict[i].charAt(prefix.length()))) break;
//      System.out.println("=======> "+dict[i]);
      list.add(dict[i]);
    }
    return list;
  }
  
   boolean startsWith(List<String> values, String prefix) {
    for(int i = 0; i < values.size(); i++) {
      if(startsWith(values.get(i), prefix)) return true;
    }
    return false;
  }*/
}
