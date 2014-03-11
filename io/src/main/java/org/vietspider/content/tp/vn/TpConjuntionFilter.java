/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.tp.vn;

import java.util.Arrays;
import java.util.Comparator;

import org.vietspider.index.analytics.PhraseData;
import org.vietspider.index.word.PhraseFilter;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 26, 2009  
 */
public class TpConjuntionFilter implements PhraseFilter {
  
  private final static String [] PHRASES = {
    "cùng","của", "các", "có", "cũng", "chứ", "cho", "chưa", "cả",
    "do", "để", "đã",
    "hoặc",
    "khác",
    "là",
    "ở",
    "mà", "mặc dù", 
    "nhưng", "nên", "nếu", "này",
    "sao cho",
    "thì", "thế mà", "tuy nhiên", "tuy", "trong", "tại",
    "và", "vẫn", "vì", "với", "về"
  };
  
  private Comparator<String> comparator = new Comparator<String>() {
    public int compare(String arg0, String arg1) {
      return arg0.compareToIgnoreCase(arg1);
    }
  };
  
  public TpConjuntionFilter() {
    Arrays.sort(PHRASES, comparator);
  }

  public boolean isValid(PhraseData phrase) {
    return Arrays.binarySearch(PHRASES, phrase.getValue()) < 0;
  }
  
}
