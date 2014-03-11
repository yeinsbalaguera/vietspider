/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.content.tp.vn;

import java.util.Arrays;

import org.vietspider.common.Application;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.content.tp.word.CharUtil;
import org.vietspider.content.tp.word.WordList;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Jul 24, 2006
 */
public class VietnameseAnalyzer {
  
  public static char [] PUNCTUATE  = {';', '.', ':', '?' , '\n'};
  public static char [] BROKEN  = {',', '\'', '"', '(', ')', '<', '>', '{', '}', '[', ']'};
  public static String [] CONJUNCTION = {"v\u1EABn", "\u0111\u01B0\u1EE3c", "nh\u01B0", "cho"
    , "v\u00E0o", "n\u00E0y", "c\u1EE7a", "r\u1ED3i", "trong", "v\u1EDBi", "\u1EDF", "c\u00E1c"
    , "c\u00F2n", "v\u1EC1", "m\u00E0", "c\u00F3", "m\u1EB7c", "\u0111\u00E3", "th\u00EC"
    , "n\u00EAn", "c\u00F9ng", "n\u1EBFu", "v\u1EADy", "nh\u1EEFng", "ho\u1EB7c", "nh\u01B0ng"
    , "khi", "v\u00E0", "do", "l\u00E0", "\u0111\u1EBFn", "m\u1ED9t", "\u0111ang", "theo"
    , "t\u1EEB", "t\u1EA1i", "s\u1EBD", "hay", "tr\u00EAn", "b\u1EDFi", "c\u1EA3"
    , "c\u1EA7n", "t\u1EDBi", "c\u0169ng", "v\u00EC", "r\u1EB1ng", "\u0111\u1EC3", "\u1EA5y"
    , "n\u1EEFa", "r\u1EA5t", "g\u1EEDi", "gi\u1EEFa", "qua", "and", "or"};
  
  public String [] nounsIgnores;
  
  static{
    Arrays.sort(PUNCTUATE);
    Arrays.sort(BROKEN);
  }
  
  public VietnameseAnalyzer() throws Exception {
    String url = UtilFile.getFile(getClass(), "NounsIgnore.cfg");
    String text  = new String(RWData.getInstance().load(url), Application.CHARSET);
    nounsIgnores = text.split(",");   
  }
  
  public void breakSequence(CharSequence chars, WordList phrases, WordList nouns){
    int i = 0;
    int start = 0;
    while( i < chars.length()){
      char c = chars.charAt(i);
      if(Arrays.binarySearch(PUNCTUATE, c) > -1){
        if(c == '.' && i > 0 && i < (chars.length()-1) 
            && Character.isUpperCase(chars.charAt(i-1))
            && Character.isUpperCase(chars.charAt(i+1))){
          StringBuilder builder = new StringBuilder(chars.subSequence(0, i)).
                                  append(' ').append(chars.subSequence(i+1, chars.length()));
          chars = builder;
          i++;
          continue;
        }
        CharSequence seq = chars.subSequence(start, i);
        start = i+1;
        if(isNotEmpty(seq)) breakBroken(seq, phrases, nouns);
      }
      i++;
    }
    i = Math.min(chars.length(), i);
    CharSequence seq = chars.subSequence(start, i);
    if(isNotEmpty(seq)) breakBroken(seq, phrases, nouns); 
  }
  
  public void breakBroken(CharSequence chars, 
                          WordList phrases, WordList nouns){
    chars = trimStart(chars);
    if(chars.length() < 20) return;
    int empty = 0;
    int i = 0;
    int count = 0;   
    while(i < chars.length()){
      if(Character.isUpperCase(chars.charAt(i))) {
        if(i > 0 && count == 0){
          count = 2;
          break;
        }
        count++;
      }
      if(Character.isWhitespace(chars.charAt(i))) empty++;
      if(empty >= 2) break;
      i++;
    }   
    if(count < 2 && chars.length()>0){
      StringBuilder builder = new StringBuilder();
      builder.append(Character.toLowerCase(chars.charAt(0)));      
      builder.append(chars.subSequence(1, chars.length()));
      chars = builder;
    }
    int start = 0;
    i = 0;
    while( i < chars.length()){
      char c = chars.charAt(i);
      if(Arrays.binarySearch(BROKEN, c) < 0){
        i++;
        continue;
      }
      CharSequence seq = chars.subSequence(start, i);      
      if(isNotEmpty(seq)) breakUppercase(seq, phrases, nouns);
      start = i;
      i++;
    }
    i = Math.min(chars.length(), i);
    CharSequence seq = chars.subSequence(start, i);
    if(isNotEmpty(seq)) breakUppercase(seq, phrases, nouns); 
  }  
  
  public void breakUppercase(CharSequence chars, WordList phrases, WordList nouns){ 
    chars =  trimStart(chars);    
    int i = 0;
    int start = 0;
    while( i < chars.length()){
      char c = chars.charAt(i);
      if(!Character.isUpperCase(c)){
        i++;
        continue;
      }
      if(start < i-1){
        CharSequence seq = chars.subSequence(start, i);       
        if(isNotEmpty(seq)) breakConjunction(seq, phrases);
        start = i;
      }else start = Math.max(0, i-1);      
      
      i = goEnd(start, chars);
      CharSequence seq = chars.subSequence(start, i);    
      seq = CharUtil.clean(seq);
      int count = countProperNoun(chars) ;
      if(isNotEmpty(seq) && !checkNounIgnores(count, seq)) nouns.set(seq.toString(), count);
      start = i;
      i++;
    }
    i = Math.min(chars.length(), i);
    CharSequence seq = chars.subSequence(start, i);
    if(isNotEmpty(seq)) breakConjunction(seq, phrases);
  }
  
  public void breakConjunction(CharSequence chars, WordList phrases){
    chars = trimStart(chars);
    int i = 0;
    int start = 0;
    int end = 0;
    while( i < chars.length()){
      char c = chars.charAt(i);
      if(!Character.isLetter(c)){
        i++;
        continue;
      }
      end = i;        
      while(i<chars.length()){
        if(Character.isWhitespace(chars.charAt(i))) break;
        i++;
      }
      CharSequence seq = chars.subSequence(end, i);
      if(isConjunction(seq)){
        seq = chars.subSequence(start, end);
        seq = CharUtil.clean(seq);
        int count  = CharUtil.countChar(seq, ' ');
        if(isNotEmpty(seq) && count > 1) phrases.set(seq.toString(), count);  
        start = i;
      }        
      i++;
    }
    i = Math.min(chars.length(), i);
    CharSequence seq = chars.subSequence(start, i);
    seq = CharUtil.clean(seq);
    int count = CharUtil.countChar(seq, ' ');
    if(count < 1) return;
    phrases.set(seq.toString(), count);      
  }
  
  private boolean isConjunction(CharSequence chars){
    for(CharSequence ele : CONJUNCTION){
      if(CharUtil.equals(chars, ele)) return true;
    }
    return false;
  }
  
  private boolean isNotEmpty(CharSequence chars){
    int i = -1;    
    while(i < chars.length()){
      i++;
      if(i >= chars.length()) break;
      if(Character.isWhitespace(chars.charAt(i))) continue;
      return true;
    }
    return false;
  }
  
  private CharSequence trimStart(CharSequence chars){
    int i = -1;    
    while(i < chars.length()){
      i++;
      if(i >= chars.length()) break;
      char c = chars.charAt(i);
      if(!Character.isLetterOrDigit(c) ) continue;
      break;
    }
    return chars.subSequence(i, chars.length());
  }
  
  private int goEnd(int idx, CharSequence chars){    
    int i = idx + 1;    
    while(i < chars.length()){
      char c = chars.charAt(i);
      if(Character.isWhitespace(chars.charAt(i-1)) 
          && Character.isLetter(c) && Character.isLowerCase(c)) break;
      if(Character.isDigit(c)) break;
      if(Arrays.binarySearch(CharUtil.SEPARATORS, c) > -1) break;
      i++;
    }
    return i;
  }
  
  private boolean checkNounIgnores(int count, CharSequence chars){
    if(count < 1) return true;
    for(String ele : nounsIgnores){
      if(CharUtil.equalsIgnoreCase(ele, chars)) return true;
    }
    return false;
  }  
  
  public int countProperNoun(CharSequence chars){
    int count = 0;
    for(int i = 0 ; i < chars.length(); i++){
      if(Character.isUpperCase(chars.charAt(i)) ) count++;
    }
    return count;
  }
  
  public static void main(String[] args) throws Exception {
    new VietnameseAnalyzer();
  }
}
