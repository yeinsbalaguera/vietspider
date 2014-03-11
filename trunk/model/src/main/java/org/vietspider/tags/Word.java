/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.tags;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 26, 2011  
 */
public class Word {

  private int start;
  private int end;
  private int length = -1;
  
  private Word next;
  
  public Word() {
    
  }
  
  public Word(int start, int end) {
    this.start = start;
    this.end = end;
  }

  public Word getNext() { return next; }
  public void setNext(Word next) { this.next = next; }
  
  public int length() {
    if(length < 0) length = end - start;
    return length;
  }

  public Word match(String root, KWord kword, ScoreValue value) {
    if(kword.length() != length()) return this;
    int start1 = 0;
    int start2 = start;
    while(start2 < end) {
      char c1 = kword.charAt(start1);
      char c2 = root.charAt(start2);
      
      char u1 = Character.toUpperCase(c1);
      char u2 = Character.toUpperCase(c2);
      if (u1 == u2) {
        start1++;
        start2++;
        continue;
      }
      
      if (Character.toLowerCase(u1) == Character.toLowerCase(u2)) {
        start1++;
        start2++;
        continue;
      }
      
      return this;
    }
    
//    System.out.println("==================================");
//    System.out.println(kword.getValue());
//    System.out.println(root.substring(start, end));
//    
//    if(next != null) {
//      System.out.println(root.substring(next.start, next.end));
//    } else {
//      System.out.println("next null");
//    }
//    if(kword.next() != null) {
//      System.out.println("===  >"+kword.next().getValue());
//    } else {
//      System.out.println(" kword null");
//    }
    
    
    if(next == null) {
      if(kword.next() == null) {
        value.score++;
        return null;
      }
      return this;
    }
    if(next != null && kword.next() == null) {
      value.score++;
      return next;
    }
    return next.match(root, kword.next(), value);
  }
  
//  private int wordScore(KWord word) {
//    word.getValue();
//  }
  
  public int getStart() { return start; }
  public void setStart(int start) { this.start = start; }
  
  public int getEnd() { return end; }
  public void setEnd(int _end) { this.end = _end; }
  
  public void print(String text) {
    System.out.print(text.substring(start, end));
    if(next != null) {
      System.out.print(' ');
      next.print(text);
    }
  }
  
}
