/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.index.word;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.chars.CharsUtil;
import org.vietspider.index.analytics.PhraseData;
import org.vietspider.index.analytics.SequenceSplitter;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 18, 2009  
 */
public class SearchSequenceSplitter extends SequenceSplitter {
  
//  public static char [] PUNCTUATE  = {';', '.', ':', '?' , '\n'};

  @Override()
  public List<PhraseData> split(PhraseData data) {
    List<PhraseData> sequences = new ArrayList<PhraseData>();
    String text = data.getValue();
    int i = 0;
    int start = 0;
    int length = text.length();
    while(i < length){
      char c = text.charAt(i);
      if(Character.isIdentifierIgnorable(c)) c = '\"';
      switch (c) {
      case ';':
      case ':':
      case '?':
      case '(':
      case ')':
      case '\n':
      case '\'':
      case '!':
      case '“':
      case '”':
      case '‘':
      case '’':
      case '/':
      case '-':
      case '_':
      case '´':
        String seq = text.substring(start, i);
        start = i+1;
        seq = CharsUtil.normalize(seq);
        if(!seq.isEmpty()) {
          sequences.add(new PhraseData(false, seq, 0));
        }
        break;
      case '\"':
        int end  = text.indexOf('\"', i+1);
        if(end <= i) end = text.length();
        seq = text.substring(start, i);
        seq = CharsUtil.normalize(seq);
        if(!seq.isEmpty()) {
          sequences.add(new PhraseData(false, seq, 0));
        }

        start = i;
        seq = text.substring(start+1, end);
        seq = CharsUtil.normalize(seq);
        if(!seq.isEmpty()) {
          sequences.add(new PhraseData(true, seq, 0));
        }
        i = end;
        start = i + 1;
        break;
      case '.':
      case ',':
      case '…':
        if(i < (length-1) && i > 0 
            && Character.isDigit(text.charAt(i-1))
            && Character.isDigit(text.charAt(i+1))) {
          break;
        }
        seq = text.substring(start, i);
        start = i+1;
        seq = CharsUtil.normalize(seq);
        if(!seq.isEmpty()) {
          sequences.add(new PhraseData(false, seq, 0));
        }
        break;
      default:
        break;
      }
      i++;
    }
    
    i = Math.min(length, i);
    if(start < i) {
      String seq = text.substring(start, i);
      seq = CharsUtil.normalize(seq);
      if(!seq.isEmpty()) {
        sequences.add(new PhraseData(false, seq, 0));
      }
    }
    
    return sequences;
  }
  
  public static void main(String[] args) {
    String text = "thuan \"nhu thuan\" hi ha ha hi";
    PhraseData phraseData = new PhraseData(false, text, 0);
    SearchSequenceSplitter splitter = new SearchSequenceSplitter();
    List<PhraseData> phrases = splitter.split(phraseData);
    for(int i = 0; i < phrases.size(); i++) {
      System.out.println(phrases.get(i).getValue());
    }
    
    
  }
  
}
