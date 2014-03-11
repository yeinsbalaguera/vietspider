/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.link.pattern.model;

import java.util.regex.Pattern;

import org.vietspider.common.text.SWProtocol;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 22, 2008  
 */
public class BeanCreator {
  
  private static final int PATTERN_TYPE = Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE;
  
  public static IPattern create(String value) {
    if(SWProtocol.isHttp(value)) return new URIPattern(value);
    if(value.indexOf('(') > -1 
        && value.indexOf(')') > -1) return new JSPattern(value);
    try {
      JavaPattern javaPattern = new JavaPattern();
      javaPattern.setValue(value);
      return javaPattern;
    } catch (Exception e) {
      return null;
    }
  }
  
  public static Pattern toPattern(String value) {
    int i = 0;
    StringBuilder builder = new StringBuilder();
    while(i < value.length()) {
      char c = value.charAt(i);
      if(Character.isLetterOrDigit(c)) {
        builder.append(c);
      } else if(c == '*') {
        builder.append("[^/\\?&#]*");
      } else {
        builder.append("\\s*[").append(c).append(']');
      }
      i++;
    }
    return Pattern.compile(builder.toString(), PATTERN_TYPE);
  }
  
}
