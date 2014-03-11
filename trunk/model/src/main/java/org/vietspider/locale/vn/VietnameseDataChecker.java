/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.locale.vn;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 17, 2009  
 */
public class VietnameseDataChecker   {
  
  private Pattern [] localePatterns;
  
  private Pattern wordPatternOnlyLetter = Pattern.compile("\\b[\\p{L}]*");
  
  private boolean checkData = false;
  
  private VietnameseSingleWords dict;
  private VietnameseSingleWords noMarkDict;
  
  public VietnameseDataChecker() {
    String [] elements = {
        "vietnam", "viet nam", "vietnamese", "hanoi", "ha noi", "tp hcm",
        "Ho Chi Minh", "Hồ Chí Minh", "Việt Nam", "Đà Nẵng", "Da Nang",
        "Hà Nội", "Sài Gòn", "saigon", "Sai gon"
    };
    
    localePatterns = new Pattern[elements.length];
    int style = Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE;
    for(int i = 0; i < elements.length; i++) {
      elements[i] = elements[i].trim().toLowerCase();
      try {
        localePatterns[i] = Pattern.compile(elements[i], style);
      } catch (Exception e) {
      }
    }
    
    dict = new VietnameseSingleWords();
    noMarkDict = new VietnameseSingleWords();
  }
  
  public boolean onlyCheckData() {  return checkData; }
  
  public void setOnlyCheckData(boolean checkData) { this.checkData = checkData; }
  
  public boolean checkTextData(String value) {
    int total = 0;
    int word = 0;
    int wordNotMark = 0;
    
    Matcher matcher = wordPatternOnlyLetter.matcher(value);
    int start  = 0;
    
    while(matcher.find(start)) {
      int s = matcher.start();
      int e = matcher.end();
      String w = value.substring(s, e).toLowerCase();
      
      if(!(w = w.trim()).isEmpty()) {
        if(dict.contains(w)) {
          word++;
        } else if(noMarkDict.contains(w)) {
          wordNotMark++;
        }
      }
      
      total++;
      start = e + 1;
      if(start >= value.length()) break;
    }
    
    if(total < 1) return false;
    
    int rate1 = (word*100)/total;
    int rate2 = (wordNotMark*100)/total;
    
//    System.out.println("vietnamese work  " + rate1 + " : "+ rate2);
    
    if(total < 20) {
      return rate1 >= 90 || rate2 >= 90;
    }
    
    return rate1 >= 50 || rate2 >= 50;
  }
  
  public boolean checkLocalePattern(String value) {
    for(int i = 0; i < localePatterns.length; i++) {
      if(localePatterns[i] == null) continue;
      Matcher matcher = localePatterns[i].matcher(value);
      if(matcher.find()) return true;
    }
    return false;    
  }
  
}
