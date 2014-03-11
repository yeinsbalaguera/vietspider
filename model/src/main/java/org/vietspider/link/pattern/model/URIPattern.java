/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.link.pattern.model;

import org.vietspider.link.regex.URIElement;
import org.vietspider.link.regex.URIParser;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 6, 2011  
 */
public class URIPattern implements IPattern {
  
  private URIElement regex;
  
  public URIPattern() {
  }

  public URIPattern(String pattern) {
    regex = URIParser.parse(pattern);
  }

  public boolean match(String value) {
    return regex.match(value);
  }

  public void setValue(String... pattern) {
    regex = URIParser.parse(pattern[0]);
  }

  
}
