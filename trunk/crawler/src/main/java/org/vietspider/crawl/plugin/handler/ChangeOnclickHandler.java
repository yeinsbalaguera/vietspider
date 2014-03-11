/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.plugin.handler;

import java.util.Properties;

import org.vietspider.crawl.io.SaveSourceThread;
import org.vietspider.model.Source;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 3, 2008  
 */
public final  class ChangeOnclickHandler {
  
  public void handleOldOnclickPattern(Source source, Properties properties) {
    if(!changeOnclick(source, properties)) return;
    new SaveSourceThread(source);
  }
  
  @SuppressWarnings("unused")
  private boolean changeOnclick(Source source, Properties properties) {
    //will remove on 6/2008
    /*String onclick = properties.getProperty(ONCLICK_PATTERN_PROPERTIES);
    if(onclick != null && !onclick.trim().isEmpty()) {
      String [] onclickElements = onclick.split("\n");
      String templateURL =  properties.getProperty(TEMPLATE_URL);
      if(templateURL == null) {
        properties.remove(ONCLICK_PATTERN_PROPERTIES);
        source.setProperties(properties);
        return true;
      }
      String [] templateElements = templateURL.split("\n");
      
      StringBuilder builder = new StringBuilder();
      for(int i = 0; i < onclickElements.length; i++) {
        if(i >= templateElements.length) break;
        builder.append(onclickElements[i]).append('\n').append(templateElements[i]).append('\n');
      }
      properties.setProperty(JS_ONCLICK_PATTERN, builder.toString().trim());
      properties.remove(TEMPLATE_URL);
      properties.remove(ONCLICK_PATTERN_PROPERTIES);
      source.setProperties(properties);
      return true;
    }*/
    return false;
  }
  
}
