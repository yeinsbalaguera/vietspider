/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.index.word;

import java.util.Arrays;
import java.util.Comparator;

import org.vietspider.index.analytics.PhraseData;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 26, 2009  
 */
public class ConjuntionFilter implements PhraseFilter {
  
  private final static String [] PHRASES = {
    "cùng","của", "các", "có", "cũng", "chứ", "cho", "chưa", "cả",
    "do", "để", "đã",
    "hoặc",
    "khác",
    "là",
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
  
  public ConjuntionFilter() {
    Arrays.sort(PHRASES, comparator);
  }

  public boolean isValid(PhraseData phrase) {
//    String value  = phrase.getValue();
//    if(Arrays.binarySearch(PHRASES, value) > -1) return false;
    
    return true;
//    if(value.length() > 3) return true;
//    for(int i = 0; i < value.length(); i++) {
//      if(Character.isLetter(value.charAt(i))) return true;
//    }
//    
//    return false;
  }
  
}
