/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.content.tp.word;

import java.util.Arrays;


/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Jul 18, 2006
 */
final public class CharUtil {
  
  public static char [] SEPARATORS  = {';', ',', '.', ':','\'', '"', 
                                       '?', '(', ')', '<', '>', '{', '}', '[', ']'};  
  static{
    Arrays.sort(SEPARATORS);
  }
    
  public static CharSequence toLowerCase(CharSequence chars){
    StringBuilder builder = new StringBuilder();
    char c;
    for(int i=0; i<chars.length(); i++){
      c = chars.charAt(i);
      if(Character.isUpperCase(c)) c = Character.toLowerCase(c);
      builder.append(c);      
    }
    return builder.toString();
  }  
  
  public static final int charDifference(CharSequence s1, CharSequence s2) {
    int len1 = s1.length();
    int len2 = s2.length();
    int len = len1 < len2 ? len1 : len2;
    for (int i = 0; i < len; i++) {
      if (s1.charAt(i) != s2.charAt(i)) {
        return i;
      }
    }
    return len;
  }
  
  public static int countChar(CharSequence chars, char c){
    int count = 0;
    for(int i = 0 ; i < chars.length(); i++){
      if(chars.charAt(i) == c) count++;
    }
    return count;
  }   
  
  public static int indexOf(CharSequence pattern, CharSequence matcher){
    if(pattern.length() > matcher.length() ) return -1;
    int start = -1;
    int i=0;    
    while(i < matcher.length()){
      if(matcher.charAt(i) == pattern.charAt(0)){
        start = i;
        if(matcher.length() - start < pattern.length()) return -1;
        int j = 0;        
        while(start<matcher.length()){
          if(j >= pattern.length()) break;
          if(matcher.charAt(start) != pattern.charAt(j)) break;
          start++;
          j++;
        }
        if(j == pattern.length()) return start - pattern.length();
      }
      i++;      
    }
    return -1;
  }
  
  public static boolean equals(CharSequence seq, CharSequence matches){
    if(seq == null || matches == null) return false;
    if(seq.length() != matches.length()) return false;
    int i = 0;
    while(i < seq.length()){      
      if(seq.charAt(i) != matches.charAt(i)) return false;
      i++;
    }
    return true;
  }  
  
  public static boolean equalsIgnoreCase(CharSequence seq, CharSequence matches){
    if(seq.length() != matches.length()) return false;
    int i = 0;
    while(i < seq.length()){      
      if(Character.toLowerCase(seq.charAt(i)) 
            != Character.toLowerCase(matches.charAt(i))) return false;
      i++;
    }
    return true;
  }  
  
  public static CharSequence clean(CharSequence chars){
    StringBuilder cbuff = new StringBuilder(chars.length());
    int i=0;
    boolean add = false;
    while(i < chars.length()){
      if(Character.isLetterOrDigit(chars.charAt(i))){ 
        cbuff.append(chars.charAt(i));
        add = false;
      }else if (cbuff.length() > 0 && !add){
        cbuff.append(' ');
        add = true;
      }
      i++;
    }
    i = cbuff.length()-1;
    while(i > 0){      
      if(Character.isLetterOrDigit(cbuff.charAt(i))) break;
      i--;
    }
    i++;
    chars = cbuff.subSequence(0, i);    
    return chars;
  } 
  
  public static String floatToString(float boost) {
    if (boost != 1.0f)  return "^" + Float.toString(boost);
    return "";
  }
  
//  static Comparator<Word> createComparator(){
//    return new Comparator<Word>(){
//      public int compare(Word w1, Word w2) {        
//        int i = 0;        
//        CharSequence o1 = w1.getChars();
//        CharSequence o2 = w2.getChars();
//        while(i<Math.min(o1.length(), o2.length())){          
//          if(o1.charAt(i) < o2.charAt(i)) return -1;
//          if(o1.charAt(i) > o2.charAt(i)) return 1;
//        }
//        if(o1.length() == o2.length()) return 0;
//        return o1.length() > o2.length() ? 1 : -1;
//      }     
//    };
//  }
  
}
