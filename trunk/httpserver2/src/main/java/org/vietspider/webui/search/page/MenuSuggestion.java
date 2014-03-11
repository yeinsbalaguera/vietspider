/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.webui.search.page;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.vietspider.chars.CharsUtil;
import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 26, 2010  
 */
public class MenuSuggestion extends PageChunk {

  private ComplexChunk itemChunk;
  private ComplexLabelData label;
  
  public MenuSuggestion() {
    this("menu_suggestion.xml", "menu_suggestion_item.xml", "menu_suggestion_label.txt");
  }

  public MenuSuggestion(String fileName, String itemFile, String labelFile) {
    super(fileName, "$items");
    if(itemFile != null) {
      itemChunk = new ComplexChunk(itemFile);
    }
    
    if(labelFile != null) {
      label = new ComplexLabelData(labelFile);
    }
  }

  public void write(OutputStream output, String pattern) {
    String [] elements = label.generate(pattern);
    write(output, elements);
  }
  
  public void write(OutputStream output, String [] elements) {
    loadData();
    if(elements.length < 1) return;
    try {
      output.write(top);
      output.flush();
    } catch (Exception e) {
      LogService.getInstance().setMessage("SERVER", e, null);
    }

    if(bottom == null || itemChunk == null) return;
    
    for(int i = 0; i < elements.length; i++) {
//      System.out.println("=======  >"+ elements[i]);
      try {
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("pattern", elements[i]);
        properties.put("shortPattern", CharsUtil.cutLabel(elements[i], 12));
        properties.put("encodePattern", URLEncoder.encode(elements[i], Application.CHARSET));
        itemChunk.write(output, properties);
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }

    try {
      output.write(bottom);
      output.flush();
    } catch (Exception e) {
      LogService.getInstance().setMessage("SERVER", e, null);
    }
  }
  
  public String renderForPage(List<String> patterns) {
    StringBuilder builder = new StringBuilder();
    builder.append("<br>");
    builder.append("<span class=\"suggestion_text\" style=\"color: rgb(204, 0, 0);\">");
    builder.append("Từ khóa đã tìm</span>:<table><tr>");
    for(int i = 0; i < patterns.size(); i++) {
      builder.append("<td>");
      String encode = null;
      try {
        encode = URLEncoder.encode(patterns.get(i), "utf-8");
      } catch (Exception e) {
      }
      if(encode == null) continue;
      //          if(i > 0 && i < patterns.size()-1 ) {
      //            builder.append(", &nbsp;");
      //          } else if(i == patterns.size()-1){
      //            builder.append(" hoặc ");
      //          }
      builder.append("<a href=\"/site/search/1/?query=");
      builder.append(encode);
      builder.append("\" class=\"suggestion_text\">");
      builder.append(CharsUtil.cutLabel(patterns.get(i), 100)).append("</a>");
      builder.append("</td>");
      if(i%2 == 1) {
        builder.append("</tr>");
      } else {
        builder.append("<td width=\"15\"></td>");
      }
    }
    builder.append("</table>");
    
    return builder.toString();
  }

}
