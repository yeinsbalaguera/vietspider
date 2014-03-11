/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.link.pattern;

import org.vietspider.link.pattern.model.OnclickPatternBak;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 21, 2008  
 */
public class OnclickPatterns {
  
  private OnclickPatternBak [] patterns;
  
  public OnclickPatterns(OnclickPatternBak [] patterns) {
    this.patterns = patterns;
  }
  
  public String create(String value) {
    if(patterns == null) return null;
    for(OnclickPatternBak pattern : patterns) {
      if(pattern == null) continue;
      String url = pattern.create(value);
      if(url != null) return url; 
    }
    return null;
  }
  
}
