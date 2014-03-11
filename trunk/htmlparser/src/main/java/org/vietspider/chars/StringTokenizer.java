/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.chars;

import org.vietspider.common.util.Queue;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Aug 17, 2006
 */
public class StringTokenizer {
  
  private char[] regexes;
 
  public StringTokenizer(char[] reg){
    this.regexes = reg;
  }
  
  public Queue<char[]> toQueue(char [] chars){
    Queue<char[]> list  = new Queue<char[]>();
    int start = 0;
    int idx = 0;
    TypeRegex check;
    char c;
    while(idx < chars.length){
      c = chars[idx];
      check = isSplitChar(c);
      if(check == TypeRegex.ADD_REGEX) {
        if(start < idx) add(CharsUtil.copyOfRange(chars, start, idx), list);
        add(new char[]{c}, list);        
        start = idx +1;
      }else if(check == TypeRegex.REGEX) {  
        add(CharsUtil.copyOfRange(chars, start, idx), list);
        start = idx +1;              
      }
      idx++;
    }    
    if(start < chars.length){
      add(CharsUtil.copyOfRange(chars, start, chars.length), list);
    }
    return list;
  }
  
  private void add(char [] value, Queue<char[]> queue){
    value = CharsUtil.cutAndTrim(value, 0, value.length);
    if(value.length < 1) return;
    queue.push(value);
  }
  
  private TypeRegex isSplitChar(char c){    
    for(char e : regexes) 
      if(e == c) return TypeRegex.ADD_REGEX;
      if(Character.isSpaceChar(c) || Character.isWhitespace(c)) return TypeRegex.REGEX;
    return TypeRegex.NOT_REGEX;
  }
  
  public enum TypeRegex {  
    ADD_REGEX, NOT_REGEX, REGEX;
  }

  public static void main(String[] args) {
    StringTokenizer tokenizer = new StringTokenizer(new char[]{'\"' });
    String text = "class= \"newsTitle\" target='_self' href='/read.asp?id=287'";
    Queue<char[]> values = tokenizer.toQueue(text.toCharArray());
    
    System.out.println("Mang la : ");
    
    while(values.hasNext()) {
      System.out.print(new String(values.pop()) +"\", \"");
    }
    
  }

}