/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.content.tp.word;


/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Jul 19, 2006
 */
public class Word {
  
  private String value;
  
  private int time;
  private int count;
  
  public Word(String _value, int time, int count){
    this.value = _value;    
    this.time = time;
    this.count =  count;//CharUtil.countChar(chars, ' ') + 1;
  }
  
  public String getValue() { return value; }
  public void setValue(String value) { this.value = value; }

  public int getTime() { return time; }
  public void setTime(int time) {  this.time = time;  }

  public int getCount() { return count; }
  public void setCount(int count) { this.count = count; }

}