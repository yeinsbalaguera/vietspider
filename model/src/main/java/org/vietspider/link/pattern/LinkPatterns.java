/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.link.pattern;

import org.vietspider.link.pattern.model.IPattern;
import org.vietspider.link.pattern.model.URIPattern;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 21, 2008  
 */
public class LinkPatterns extends AbsPatterns<URIPattern> {
  
  public LinkPatterns(URIPattern [] patterns) {
    super(patterns);
  }
  
  public boolean match(String url) {
//    System.out.println(" thay co cai ni "+ patterns.length);
//  System.out.println("thay rang "+patterns.length);
    boolean match = true;
    for(int i = 0; i< patterns.length; i++) {
      IPattern ipattern = patterns[i];
      if(ipattern == null) continue;
//      System.out.println(ipattern.toString() + " sdsa : "+ url);
      if(ipattern.match(url)) return true;
      match = false;
//      System.out.println(pattern+ " : "+ url);
    }
    return match;
  }
  
}
