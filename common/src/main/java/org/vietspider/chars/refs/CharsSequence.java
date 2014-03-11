/***************************************************************************
 * Copyright 2004-2006 The VietSpider All rights reserved.  *
 **************************************************************************/
package org.vietspider.chars.refs;

/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 8, 2006
 */
public class CharsSequence {
  
  private int index;
  
  private char [] values;
  
  public CharsSequence(int max){
    values = new char[max];
    index = 0;
  }
  
  public void append(char c){
    if(index >= values.length) return;
    values[index] = c;
    index++;
  }
  
  public void append(String string){
    char [] cs = string.toCharArray();
    for(int i = 0; i < cs.length; i++){
      if(index >= values.length) return;
      values[index] = cs[i];
      index++;
    }
  }

  public  char[] getValues() {
    char[] newValues = new char[index];
    System.arraycopy(values, 0, newValues, 0, index);
    return newValues;
  }  
}
