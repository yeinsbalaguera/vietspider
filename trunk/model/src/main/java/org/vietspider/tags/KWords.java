/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.tags;

import java.util.ArrayList;
import java.util.List;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 25, 2011  
 */
public class KWords {
  
  private List<KWord> words = new ArrayList<KWord>();
  private String name;
  private String label;
  private boolean _default = false;
  
  public KWords(String text) {
    split(text);
  }
  
  public boolean isDefault() { return _default; }
  public void setDefault(boolean _default) { this._default = _default; }

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  
  public String getLabel() { return label; }
  public void setLabel(String label) { this.label = label; }

  private void split(String text) {
    int index = 0;
    KWord current = null;
    while(index < text.length()) {
      char c = text.charAt(index);
      while(!Character.isLetterOrDigit(c) && c != '-') {
        index++;
        if(index >= text.length()) break;
        c = text.charAt(index);
      } 

      int start = index;
      
      while(Character.isLetterOrDigit(c) || c == '-') {
        index++;
        if(index >= text.length()) break;
        c = text.charAt(index);
      } 
      
//      System.out.println(text.substring(start, index));
      
      String value = text.substring(start, index);
      if(value.trim().length() < 1) {
        index++;
        continue;
      }
      KWord word = new KWord(value);
      if(current == null) {
        current = word;
        words.add(current);
      } else {
        current.setNext(word);
        current = word;
      }

      if(c == '.'
          || c == '?'
            || c == ','
              || c == '!'
                || c == ':'
                  || c == '"'
                    || c == ';'
                      || c == '\\'
                        || c == '/'
                          || c == '\n'
      ) current = null;
    }
  }

  public List<KWord> getWords() {
    return words;
  }
  
  public void print() {
    for(int i = 0; i < words.size(); i++) {
      words.get(i).print();
      System.out.println();
    }
  }
  

}
