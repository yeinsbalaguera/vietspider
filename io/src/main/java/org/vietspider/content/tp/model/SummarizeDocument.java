/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.content.tp.model;

import org.vietspider.content.tp.word.WordList;


/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Aug 22, 2006
 */
public class SummarizeDocument {
  
  public static String  FIELD_ID = "id";
  public static String  FIELD_RELATION="relations";
  public static String  FIELD_NOUN = "nouns";
  public static String  FIELD_SECTION = "sections";
  
  private Object id;
  private WordList sections;
  private WordList nouns;
  
  public SummarizeDocument(Object id, WordList sections, WordList nouns) {
    this.id = id;
    this.sections = sections;
    this.nouns = nouns;
  }
  
  public WordList getSections() { return sections; }
  public void setSections(WordList section) { this.sections = section; }
  
  public WordList getNouns() { return nouns; }
  public void setNouns(WordList noun) { this.nouns = noun; }
  
  public Object getId() { return id; }
  public void setId(Object id) { this.id = id;  }
}
