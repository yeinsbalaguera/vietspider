/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.link.pattern;

import org.vietspider.link.pattern.model.JSOnclickPattern;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 17, 2008  
 */
public class JSOnclickPatterns extends AbsPatterns<JSOnclickPattern> {
  
  public JSOnclickPatterns(JSOnclickPattern [] patterns) {
    super(patterns);
  }
  
  public String create(String value) {
    if(patterns == null) return null;
    for(JSOnclickPattern pattern : patterns) {
//      if(value.length() < 100) System.out.println(" check valu "+ value);
      if(pattern == null || !pattern.match(value)) continue;
      String url = pattern.create(value);
//      System.out.println("===== >tao duoc "+ url );
      if(url != null) return url; 
    }
    return null;
  }

 
}
