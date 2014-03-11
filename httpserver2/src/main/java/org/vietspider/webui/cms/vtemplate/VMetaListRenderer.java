/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.webui.cms.vtemplate;

import java.io.File;
import java.util.Calendar;

import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.common.text.CalendarUtils;
import org.vietspider.db.database.MetaList;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 10, 2011  
 */
public class VMetaListRenderer {

  private VLayoutRenderer layout = new VLayoutRenderer();
  private String html;

  public VMetaListRenderer() {
    File file = UtilFile.getFile("system/cms/vtemplate", "Page.html");
    try {
      html = new String(RWData.getInstance().load(file), Application.CHARSET);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
      html = e.toString();
    }
  }

  public void write(HttpRequestData hrd, MetaList data) {
    hrd.write(CommonRenderer.getInstance().renderHeader(hrd, data.getTitle()));
    
    int start = 0;

    String [] params = hrd.getParams();
    String [] items = new String[0];
    if(params != null && params.length > 0) { 
      items = new String[params.length >= 2 ? params.length-1 : 2];
      if(params.length < 2) {
        Calendar cal = Calendar.getInstance();    
        items[0] = CalendarUtils.getParamFormat().format(cal.getTime());
        items[1] = params[0];
      } else {
        System.arraycopy(params, 1, items, 0, items.length);
      }
    }
    
    String pattern = "$menu";
    int idx = html.indexOf(pattern);
    if(idx > -1) {
      hrd.write(html.substring(start, idx));
      hrd.write(CommonRenderer.getInstance().menu.render(hrd, items));
      start = idx + pattern.length();
    }

    pattern = "$layout";
    idx = html.indexOf(pattern);
    if(idx > -1) {
      hrd.write(html.substring(start, idx));
      try {
        layout.write(hrd, data);
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
      start = idx + pattern.length();
    } 

    pattern = "$page_iterator";
    idx = html.indexOf(pattern);
    if(idx > -1) {
      StringBuilder builder = new StringBuilder();
      builder.append(html.substring(start, idx));
      builder.append("\n\n\n");
      if(data.getTotalPage() < 1) {
        builder.append("<div align=\"right\" id=\"iterator\">");
        builder.append("\n&nbsp;&nbsp;&nbsp;").append("<a class=\"other_page\" href=\"");
        builder.append(hrd.getCurrentDateLink()).append("\">");      
        builder.append("Xem các tin khác trong ngày</a>");
        builder.append("</div>");
      } else {
        builder.append(CommonRenderer.getInstance().iterator.render(hrd, data));
      }
      builder.append("\n\n\n");

      hrd.write(builder.toString());

      start = idx + pattern.length();
    }

    if(start < html.length()) {
      hrd.write(html.substring(start));
    }

    hrd.write(CommonRenderer.getInstance().renderBottom(hrd));
  }


}
