/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.index.analytics;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.chars.CharsUtil;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 18, 2009  
 */
public class SequenceSplitter extends Analyzer {
  
//  public static char [] PUNCTUATE  = {';', '.', ':', '?' , '\n'};

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
      case '\"':
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
    String seq = text.substring(start, i);
    seq = CharsUtil.normalize(seq);
    if(!seq.isEmpty()) {
      sequences.add(new PhraseData(false, seq, 0));
    }
    
    return sequences;
  }
  
}
