/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.solr2.highlight;

import java.util.ArrayList;
import java.util.List;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 15, 2011  
 */
public class Word {

  String value;
  String nomark;
  
  boolean equals = false;
  
  Word(String value) {
    this.value = value;
    this.nomark = TextElement.toTextNotMarked(value);
    
    equals = value.intern() == nomark.intern();
  }
  
  public String getValue() { return value; }

  public String getNomark() { return nomark;  }
  
  public boolean isEquals() { return equals; }

  static public List<Word> split(String pattern) {
    int index = 0;
    List<Word> list = new ArrayList<Word>();
    while(index < pattern.length()) {
      while(index < pattern.length()) {
        char c = pattern.charAt(index);
        if(Character.isLetterOrDigit(c)) break;
        index++;
      }
      int start = index;
      while(index < pattern.length()) {
        char c = pattern.charAt(index);
        if(!Character.isLetterOrDigit(c)) break;
        index++;
      }
      String value = pattern.substring(start, index).trim();
      if(value.length() > 0) list.add(new Word(value));
    }
    return list;
  }
  
}
