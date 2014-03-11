/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.index.analytics;

import org.vietspider.locale.vn.VietnameseConverter;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 24, 2009  
 */
public class PhraseData {
  
  private String value;
  private String nomarkValue;
  private boolean atomic = false;
  private int length = 0;
  private boolean isNoun = false;
  
  public PhraseData(boolean atomic, String value, int length) {
    this.atomic = atomic;
    this.value = value;
    this.length = length;
  }

  public boolean isAtomic() { return atomic; }
  public void setAtomic(boolean atomic) { this.atomic = atomic; }
  
  public String getValue() { return value; }
  public void setValue(String value) { this.value = value; }
  
  public String getNomarkValue() {
    if(nomarkValue != null) return nomarkValue;
    nomarkValue = VietnameseConverter.toTextNotMarked(value);
    return nomarkValue; 
  }

  public int getLength() { return length; }
  public void setLength(int length) { this.length = length; }

  public boolean isNoun() { return isNoun; }
  public void setNoun(boolean isNoun) { this.isNoun = isNoun; }
  
  public PhraseData clone() {
    PhraseData phrase = new PhraseData(atomic, value, length);
    phrase.setNoun(isNoun);
    return phrase;
  }
  

}
