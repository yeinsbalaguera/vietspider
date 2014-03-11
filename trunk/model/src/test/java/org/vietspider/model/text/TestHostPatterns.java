/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.model.text;

import static org.vietspider.link.pattern.LinkPatternFactory.createPatterns;

import org.vietspider.link.pattern.HostPatterns;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 12, 2008  
 */
public class TestHostPatterns {
  
  public static void main(String[] args) {
    String [] elements = {
        "*.vietnamnet.vn"
    };
    HostPatterns hostPattern = createPatterns(HostPatterns.class, elements);  
    System.out.println(hostPattern.match("http://lanhdao.vietnamnet.vn/"));
  }
  
}
