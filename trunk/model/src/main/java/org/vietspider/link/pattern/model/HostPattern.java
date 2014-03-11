/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.link.pattern.model;

import org.vietspider.link.regex.URIElement;
import org.vietspider.link.regex.URIParser;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 22, 2008  
 */
public class HostPattern implements IPattern {
  
  private URIElement regex;
  
  public HostPattern() {
  }
  
  public HostPattern(String pattern) {
    regex = URIParser.parse(pattern);
  }
  
  public boolean match(String value) {
    return regex.match(value);
  }
  
  public void setValue(String... pattern) {
    regex = URIParser.parse(pattern[0]);
  }
  
//    if(components.length < 1) return true;
//    int start = 0; 
//    for(int j = 0; j < components.length; j++) {
//      start = match(value, components[j].text, start);
//      if(start < 0) return false;
//      
//      if(j < components.length-1) {
//        char nc = components[j+1].text.charAt(0);
//        while(start < value.length()) {
//          char c = value.charAt(start);
//          if(c == nc) break;
//          start++;
//        }
//      } else {
//        while(start < value.length()) {
//          char c = value.charAt(start);
//          if(c == '/' || c == '?'
//            || c == '&' || c == '=' || c == '#') break;
//          start++;
//        }
//      }
//    }
//    
//    if(start == value.length() - 1) {
//      char c = value.charAt(start);
//      if(c == '/' || c == '?'
//        || c == '&' || c == '=' || c == '#') return true;
//    }
//    
//    return start >= value.length() - 1 || !Character.isLetterOrDigit(value.charAt(start+1));
//  }
}
