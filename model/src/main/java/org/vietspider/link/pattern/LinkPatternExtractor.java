/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.       *
 **************************************************************************/
package org.vietspider.link.pattern;

import java.util.List;

import org.vietspider.link.pattern.model.PatternExtractor;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 21, 2008  
 */
public class LinkPatternExtractor extends AbsPatterns<PatternExtractor> {

  public LinkPatternExtractor(PatternExtractor [] patterns) {
    super(patterns);
  }
  
  public String extract(String value, List<String> list) {
//  System.out.println("thay rang "+patterns.length);
    for(int i = 0; i< patterns.length; i++) {
      if(patterns[i] == null) continue;
      value = patterns[i].extract(value, list);
      
    }
    return value;
  }
  
  public String extract(String value) {
    for(int i = 0; i< patterns.length; i++) {
      if(patterns[i] == null) continue;
      String url = patterns[i].extract(value);
      if(url != null) return url;
    }
    return null;
  }
  
}
