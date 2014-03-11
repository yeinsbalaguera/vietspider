/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.link.pattern;

import org.vietspider.link.pattern.model.HostPattern;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 21, 2008  
 */
public class HostPatterns extends AbsPatterns<HostPattern> {

  public HostPatterns(HostPattern [] patterns) {
    super(patterns);
  }
  
  public boolean match(String url) {
//  System.out.println("thay rang "+patterns.length);
    for(int i = 0; i< patterns.length; i++) {
      HostPattern pattern = patterns[i];
      if(pattern == null) continue;
//    System.out.println(pattern+ " : "+ url);
      if(pattern.match(url)) return true;
//      System.out.println(pattern+ " : "+ url);
    }
    return false;
  }
  
}
