/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.tags;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 26, 2011  
 */
public class KWord {

  private String value;
  
  private KWord next;
  
  private int totalElement = -1;
  
  public KWord(String value) {
    this.value = value;
  }
  
  public String getValue() { return value; }
  public void setValue(String value) { this.value = value; }

  public KWord next() { return next; }
  public void setNext(KWord next) { this.next = next; }
  
  public int length() { return value.length(); }
  
  public int totalElement() {
    if(totalElement > -1) return totalElement;
    totalElement = 1;
    KWord temp = next;
    while(temp != null) {
      totalElement++;
      temp = temp.next;
    }
    return totalElement;
  }
  
  
  
  public char charAt(int index) { return value.charAt(index); }

  public void print() {
    System.out.print(value);
    if(next != null) {
      System.out.print(' ');
      next.print();
    }
  }  
}
