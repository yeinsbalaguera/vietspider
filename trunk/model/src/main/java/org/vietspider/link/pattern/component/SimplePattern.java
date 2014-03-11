/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.link.pattern.component;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 20, 2009  
 */
public class SimplePattern {
  
  private String pattern;
  
  public SimplePattern(String pattern) {
    this.pattern = pattern;
  }
  
  public boolean match(String value) {
    if("*".equals(pattern)) return true;
    return pattern.equals(value);
  }
  
}
