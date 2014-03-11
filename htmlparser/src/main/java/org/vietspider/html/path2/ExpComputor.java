/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.path2;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 11, 2007  
 */
public class ExpComputor {
  
  private final static char C_MULTIFLY = 'x';
  private final static char C_DIVIDE = ':';
  private final static char C_MOD = '%';
  private final static char C_MINUS = '-';
  private final static char C_ADD = '+';
  
  private final static char B_START = '(';
  private final static char B_END = ')';
  
  public final static char C_ANY = '*';
  
  private final static String S_MINUS = "[-]";
  private final static String S_ADD = "[+]";
  
  public boolean isCharOperator(char c) {
    return c == C_ANY ||
           c == B_START || c == B_END ||
           c == C_ADD || c == C_MINUS || 
           c == C_MULTIFLY || c == C_DIVIDE || c == C_MOD;
  }
  
  public StringBuilder multiply(StringBuilder builder) {
    int i = 0;
    while(i < builder.length()) {
      if(builder.charAt(i) == B_START) {
        i = computeSubExp(builder, i+1);
      }
      i++;
    }
    
    i = 0;
    while(i < builder.length()) {
      char c = builder.charAt(i);
      if(c == C_MULTIFLY) {
        int start = searchPreValue(builder, i-1);
        int preValue = parseInt(builder.substring(start, i));
        int end = searchNextValue(builder, i+1);
        int nextValue = parseInt(builder.substring(i+1, end));
        builder.delete(start, end);
        builder.insert(start, preValue*nextValue);
        i = start;
        continue;
      } else if(c == C_DIVIDE) {
        int start = searchPreValue(builder, i-1);
        int preValue = parseInt(builder.substring(start, i));
        int end = searchNextValue(builder, i+1);
        int nextValue = parseInt(builder.substring(i+1, end));
        builder.delete(start, end);
        builder.insert(start, preValue/nextValue);
        i = start;
        continue;
      } else if(c == C_MOD) {
        int start = searchPreValue(builder, i-1);
        int preValue = parseInt(builder.substring(start, i));
        int end = searchNextValue(builder, i+1);
        int nextValue = parseInt(builder.substring(i+1, end));
        builder.delete(start, end);
        builder.insert(start, preValue%nextValue);
        i = start;
        continue;
      } 
      i++;
    }
    return builder;
  }
  
  public int computeSubExp(StringBuilder builder, int start) {
    int i = start;
    while(i < builder.length()) {
      char c = builder.charAt(i);
      if(c == B_START) {
        i = computeSubExp(builder, i+1)-1;
      } else if(c == B_END) {
        StringBuilder subBuilder = new StringBuilder(builder.subSequence(start, i));
        int value = compute(subBuilder);
        builder.delete(start-1, i+1);
        builder.insert(start-1, value);
        break;
      } 
      i++;
    }
    return start;
  }
  
  private int parseInt(String value) {
    return value.isEmpty()? 1 : Integer.parseInt(value);
  }
  
  private int searchPreValue(StringBuilder builder, int index) {
    int i = index;
    while(i > -1) {
      char c = builder.charAt(i);
      if(!Character.isDigit(c)) {
        if(c != C_MINUS || i < index || !Character.isDigit(builder.charAt(i-1))) return i+1;
      }
      i--;
    }
    if(i < 0) return 0;
    return i;
  }
  
  private int searchNextValue(StringBuilder builder, int index) {
    int i = index;
    while(i < builder.length()) {
      char c = builder.charAt(i);
      if(!Character.isDigit(c)) {
        if(c != C_MINUS || i > index || !Character.isDigit(builder.charAt(i+1))) return i;
      }
      i++;
    }
    return i;
  }
  
  private int minus(String expression) {
    if(expression == null) return 0;

    String [] elements =  expression.split(S_MINUS);
    if(elements.length < 1) return 0;
    
    int value = elements[0].isEmpty() ? 0 : Integer.parseInt(elements[0]);

    for(int i = 1; i < elements.length; i++){
      if(elements[i].isEmpty()) continue; 
      value = value - Integer.parseInt(elements[i]);
    }
    return  value;
  }
  
  public int compute(StringBuilder builder) {
    if(builder.length() < 1)  return 0;
    if(builder.length() == 1 && builder.charAt(0) == C_ANY) return 0;
    
    String expression = multiply(builder).toString();
    String [] elements = expression.split(S_ADD);
    if(elements.length < 1) return 0;
    
    int value = 0;
    
    if(elements[0].indexOf(C_MINUS) > -1)
      value = minus(elements[0]);
    else if(!elements[0].isEmpty()) 
      value = Integer.parseInt(elements[0]);

    for(int i = 1;  i < elements.length;  i++){
      if(elements[i].indexOf(C_MINUS) > -1)
        value = value + minus(elements[i]);
      else if(!elements[i].isEmpty()) 
        value = value + Integer.parseInt(elements[i]);
    }
    return  value;
  }
}
