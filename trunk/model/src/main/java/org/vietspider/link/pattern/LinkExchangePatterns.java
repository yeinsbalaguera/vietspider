/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.link.pattern;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.link.pattern.model.URIExchangePattern;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 21, 2008  
 */
public class LinkExchangePatterns extends AbsPatterns<URIExchangePattern> {
  
  public LinkExchangePatterns(URIExchangePattern [] patterns) {
    super(patterns);
  }
  
  public List<String> create(String value) {
    List<String> list  = new ArrayList<String>(patterns.length);
    if(patterns == null) return list;
    for(URIExchangePattern pattern : patterns) {
      String url = pattern.create(value);
      if(url == null) continue;
      list.add(url);
    }
    return list;
  }
  
}
