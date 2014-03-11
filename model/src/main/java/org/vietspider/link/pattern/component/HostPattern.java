/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.link.pattern.component;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 20, 2009  
 */
public class HostPattern {
  
  private SimplePattern [] patterns;
  
  public HostPattern(String pattern) {
    String [] elements = pattern.split(".");
  }
  
  public boolean match(String value) {
    return false;
  }
  
}
