/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.webui.search.page;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.vietspider.index.SearchQuery;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 26, 2010  
 */
public abstract class MenuFilter {
  
  private ComplexChunk itemChunk;
  
  public MenuFilter(String fileName) {
    itemChunk  = new ComplexChunk(fileName);
  }

  public void write(OutputStream output, SearchQuery query) {
    Map<String, String> properties = new HashMap<String, String>();
    properties.put("url", generateURL(query));
    itemChunk.write(output, properties);
  }
  
  abstract public String generateURL(SearchQuery query);
  
  /*private String generateURL(SearchQuery query) {
    StringBuilder builder = new StringBuilder();
    builder.append("/site/search/1/?query=").append(query.getEncodePattern());
    if(query.getRegion() != null && query.getRegion().length() > 0) {
      builder.append("&region=");  
      try {
        builder.append(URLEncoder.encode(query.getRegion(), Application.CHARSET));
      } catch (Exception e) {
      }
    }
    String [] actions = query.getActions();
    for(int i = 0; i < actions.length; i++) {
      builder.append("&action=");
      try {
        builder.append(URLEncoder.encode(actions[i], Application.CHARSET));
      } catch (Exception e) {
      }
    }
    
    return builder.toString();
  }*/
}
