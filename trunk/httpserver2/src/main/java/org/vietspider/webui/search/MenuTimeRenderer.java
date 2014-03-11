/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.webui.search;

import java.net.URLEncoder;

import org.vietspider.common.io.LogService;
import org.vietspider.index.SearchQuery;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 1, 2011  
 */
class MenuTimeRenderer {

  String render(SearchQuery query) {
    String [][] values = {
        {"-1", "Mọi lúc"}, 
        {"1", "Hôm nay"}, 
        {"2", "Hai ngày"}, 
        {"3", "Ba ngày"}, 
        {"7", "Một tuần"}, 
        {"14", "Hai tuần"} 
//        {"", ""}, 
    };
    
    StringBuilder builder = new StringBuilder();
    
    builder.append("<div class=\"menu\"><strong><dfn>&nbsp;›&nbsp;</dfn>Thời điểm</strong></div>");
    for(int i = 0; i < values.length; i++) {
      builder.append("<div class=\"menu\">&nbsp;&nbsp;&nbsp;");
      boolean link  = !values[i][0].equals(String.valueOf(query.getDate()));
      if(link) {
        builder.append("<a class=\"menu\" href=\"/site/search/1/?query=");
        try {
          builder.append(URLEncoder.encode(query.getPattern(), "utf-8"));
        } catch (Exception e) {
          LogService.getInstance().setMessage(e, e.toString());
        }
        if(query.getPrice() != null) builder.append("&price=").append(query.getPrice());
        builder.append("&sdate=").append(values[i][0]);
        builder.append("\">");
      }
      builder.append(values[i][1]);
      if(link) builder.append("</a>");
      builder.append("</div>");
    }
    
    return builder.toString();
  }
  
}
