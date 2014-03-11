/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.webui.search;

import java.io.OutputStream;
import java.util.List;

import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.index.SearchQuery;
import org.vietspider.webui.search.page.MenuSuggestion;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 23, 2009  
 */
public class MenuRenderer {

  private MenuTimeRenderer timeRenderer;
  private MenuSuggestion menuHistory;
  
  public MenuRenderer() {
    timeRenderer = new MenuTimeRenderer();
    menuHistory = new MenuSuggestion("menu_history.xml", "menu_suggestion_item.xml", null);
  }

  public void write(OutputStream output, SearchQuery query, int size) {  
    try {
      output.write("<div class=\"menu\">".getBytes());
      output.write("<input type=\"checkbox\" name=\"owner\" id=\"owner_id\" onchange=\"setOwner();\" />".getBytes());
      output.write("<strong>Tìm chính chủ</strong>".getBytes("utf-8"));
      output.write("</div>".getBytes());
      
      output.write(timeRenderer.render(query).getBytes("utf-8"));
      
      PriceSuggestRenderer priceRenderer = new PriceSuggestRenderer();
      StringBuilder builder = new StringBuilder();
      priceRenderer.render(builder, query);
      if(builder.length() > 0) {
        builder.insert(0, "<div class=\"menu\"><strong><dfn>&nbsp;›&nbsp;</dfn>Khoảng giá</strong></div>");
        output.write(builder.toString().getBytes(Application.CHARSET));
      }
      
      output.write(Regions.getInstance().getMenu().getBytes(Application.CHARSET));
      
      if(size > 9) {
        List<String> histories = query.getHistory();
        if(histories != null) {
          menuHistory.write(output, histories.toArray(new String[0]));
        }
      }
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }

  public MenuSuggestion getHistory() { return menuHistory; }


}
