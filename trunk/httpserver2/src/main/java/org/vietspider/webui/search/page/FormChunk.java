/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.webui.search.page;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.vietspider.common.io.LogService;
import org.vietspider.index.SearchQuery;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 26, 2010  
 */
public class FormChunk extends Chunk {

  private ComplexChunk itemChunk;
//  private LabelData labelData;
  
  @SuppressWarnings("unused")
  public FormChunk(String fileName, String labelFile) {
    itemChunk  = new ComplexChunk(fileName);
//    labelData = new LabelData(labelFile);
  }
  
  public FormChunk(String fileName) {
    itemChunk  = new ComplexChunk(fileName);
  }

  public void write(OutputStream output, SearchQuery query) {
    Map<String, String> properties = new HashMap<String, String>();
    String pattern  = query != null ? query.getPattern() : "";
    try {
      if(pattern == null) {
        pattern = "";
      } else if(pattern.indexOf('\"') > -1) {
        pattern = encode(pattern);
      }
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    properties.put("pattern", pattern);
//    properties.put("actions", generateAction(query));
    itemChunk.write(output, properties);
  }
  
  public void write(OutputStream output, String pattern) {
    if(pattern == null) pattern = "";
    Map<String, String> properties = new HashMap<String, String>();
    try {
      if(pattern.indexOf('\"') > -1) {
        pattern = encode(pattern);
      }
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    properties.put("pattern", pattern);
    itemChunk.write(output, properties);
  }
  
  
//  private String generateAction(SearchQuery query) {
//    String [] elements = labelData.generate();
//    StringBuilder builder = new StringBuilder();
//    for(int i = 0; i < elements.length; i++) {
//      builder.append("<input name=\"action\" value=\"");
//      builder.append(elements[i]).append("\" type=\"checkbox\"");
//      if(query.containsAction(elements[i])) {
//        builder.append(" checked");
//      }
//      builder.append("><font size=\"-1\">").append(elements[i]).append("</font>&nbsp;");
//
//    }
//    return builder.toString();
//  }
  
}
