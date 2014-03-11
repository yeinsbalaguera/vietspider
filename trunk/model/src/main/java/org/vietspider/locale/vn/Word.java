/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.locale.vn;

import java.io.Serializable;

import org.vietspider.chars.TextSpliter;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 7, 2011  
 */
public class Word  implements Serializable {
  
  private static final long serialVersionUID = 1L;
  
  private String value;
  private String [] elements;
  
  private int noun = -1;
  
  public Word(String element) {
    this.elements = new String[]{element};
  }
  
  public Word(String element, int noun) {
    this.elements = new String[]{element};
    this.noun = noun;
  }
  
  public Word(String[] array, int start, int end, int noun) {
    this.elements = new String[end - start];
    int index = 0;
//    System.out.println(" ===  > "+ start + " : "+ end + " : "+ array.length);
    for(int i = start; i < end ; i++) {
      this.elements[index] = array[i];
      index++;
    }
    if(noun < 0) {
      boolean upper = true;
      for(int i = 0; i < elements.length; i++) {
        if(Character.isLowerCase(elements[i].charAt(0))) {
          upper = false;
          break;
        }
      }
      if(upper) noun = 1;
    }
//    System.out.println("=== >"+ getValue() + " : "+ noun);
    this.noun = noun;
  }

  public String getValue() { 
    if(value != null) return value;
    StringBuilder builder = new StringBuilder();
    for(String ele : elements) {
      if(builder.length() > 0) builder.append(' ');
      builder.append(ele);
    }
    value = builder.toString();
    return value; 
  }
  
  public String[] getElements() { return elements; }
  
  public void setValue(String text) {
    TextSpliter spliter = new TextSpliter();
    this.elements = spliter.toArray(text, ' ');
    this.value = null;
  }
  
  public int getNoun() { return noun; }
  public void setNoun(int v) { noun = v; }

}
