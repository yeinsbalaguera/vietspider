/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.tags;

import java.util.ArrayList;
import java.util.List;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 26, 2011  
 */
public class Document {
  
  static boolean print = false;

  private List<Word> words = new ArrayList<Word>();
  private String text;
  private int total = 1;
  private String id;
  
  public Document(String text) {
    this.text = text;
    split();
  }
  
  public String getId() { return id; }
  public void setId(String value) { this.id = value; }

  public int total() { return total; }

  private void split() {
    int index = 0;
    Word current = null;
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
      
      Word word = new Word(start, index);
      total++;
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
  
  public int score(List<KWord> kwords) {
    ScoreValue value = new ScoreValue();
    for(int i = 0; i < kwords.size(); i++) {
      score(kwords.get(i), value);
    }
    return value.score;
  }
  
  public String getText() { return text; }

  public List<Word> getWords() { return words; }

  private void score(KWord kword, ScoreValue value) {
    for(int i = 0; i < words.size(); i++) {
      //print
      int pre = value.score;
      
      Word word = words.get(i).match(text, kword, value);
      while(word != null && word.getNext() != null) {
        word = word.getNext().match(text, kword, value);
      }
      //print
      int after = value.score;
//      if(Document.print) {
//        kword.print();
//        System.out.println( " : "+ after);
//      }
      if(after > pre && kword.totalElement() > 4) value.score++;
      
      
      if(print) {
        if(after != pre) {
          kword.print();
          System.out.println();
          words.get(i).print(text);
          System.out.println("====  >"+ value.score);
        }
      }
    }
  }

  public void print() {
    for(int i = 0; i < words.size(); i++) {
      words.get(i).print(text);
      System.out.println();
    }
  }


}
