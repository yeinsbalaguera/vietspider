/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content;

import org.vietspider.db.dict.non.NWord;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 14, 2009  
 */
public class TranslateKey {
  
  private int start;
  private int end;
  private NWord value;
 
  private int length;

  public TranslateKey(int start, int end, int length) {
    this.start = start;
    this.end = end;
    this.length = length;
  }
  
  public int getStart() { return start;  }

  public int getEnd() { return end; }
  public void setEnd(int end) { this.end = end; }

  public NWord getValue() { return value; }
  public void setValue(NWord value) { this.value = value; }

  public int getLength() { return length; }
  public void setLength(int length) { this.length = length; }
}
