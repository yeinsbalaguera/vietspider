/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.link.pattern.model;

import java.util.List;
import java.util.regex.Matcher;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 22, 2008  
 */
public class PatternExtractor extends JavaPattern {
  
  public PatternExtractor() {
  }
  
  public String extract(String value, List<String> list) {
    if(pattern == null) return null;
    Matcher matcher = pattern.matcher(value);
    int lastStart = 0;

    StringBuilder builder = new StringBuilder();
    while(matcher.find(lastStart)) {
      int start = matcher.start();
      int end = matcher.end();

      int index = searchWhiteSpace(value, start, end);
      if(index > 1) end = index;

      String subValue = value.substring(lastStart, start);
      if(!(subValue = subValue.trim()).isEmpty()) {
        builder.append(value.subSequence(lastStart, start));
      }
      lastStart = end;

      String url  = value.substring(start, end);
      char c = url.charAt(url.length() - 1);
      if(c == '\"') {
        url = url.substring(0, url.length()-1);
      } else if(c == ')' && value.charAt(end) == ')') { 
        url = url.substring(1, url.length()-1);
      }
      if(!url.trim().isEmpty()) list.add(url);
    }

    if(lastStart == 0) return value;

    if(lastStart < value.length()) {
      builder.append(value.substring(lastStart, value.length()).trim());  
    }

    return builder.toString();
  }

  public String extract(String value) {
    if(pattern == null) return null;
    Matcher matcher = pattern.matcher(value);

    if(matcher.find()) {
      int start = matcher.start();
      int end = matcher.end();

      int index = searchWhiteSpace(value, start, end);
      if(index > 1) end  = index;

      if(end < value.length() && value.charAt(end) == ')') {
        if(start > 0 && value.charAt(start-1) == '(') end--;
      }

      String url = value.substring(start, end);
      return url.trim().isEmpty() ? null : url;
    }

    return null;
  }

  private int searchWhiteSpace(String value, int start, int end) {
    int index = start;
    while(index < end) {
      char c = value.charAt(index);
      if(Character.isWhitespace(c) 
          || Character.isSpaceChar(c)) return index;
      index++;
    }
    return -1;
  }
}
