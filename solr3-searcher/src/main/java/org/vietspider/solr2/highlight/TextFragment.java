/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.solr2.highlight;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 15, 2011  
 */
public class TextFragment {

  private String value;
  private int score;
  
  public TextFragment(String value, int score) {
    this.value = value;
    this.score = score;
  }

  public String getValue() { return value; }

  public int getScore() { return score; }
  
  
  
}
