/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.link.pattern;

import static org.vietspider.model.SourceProperties.JS_COMPLETE_DOC;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 8, 2008  
 */
public class JsDocWriterGetter {

  public JsDocWriterGetter() {
  }

  public List<String> getJsDocWriters(Properties properties) {
    List<String> jsDocWriters = new ArrayList<String>();
    
    if(properties == null 
        || !properties.containsKey(JS_COMPLETE_DOC)) return jsDocWriters;
    
    String value = properties.getProperty(JS_COMPLETE_DOC);
    if(value == null || (value = value.trim()).length() < 1) return jsDocWriters;
    
    String [] elements = value.split("\n");
    for(String element : elements) {
      element = element.toLowerCase().trim();
      if(element.isEmpty()) continue;
      jsDocWriters.add(element);
    }
    return jsDocWriters;
  }
}
