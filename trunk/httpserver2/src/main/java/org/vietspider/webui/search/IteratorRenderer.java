/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.webui.search;

import java.net.URLEncoder;
import java.text.DecimalFormat;

import org.vietspider.common.Application;
import org.vietspider.db.database.MetaList;
import org.vietspider.index.SearchQuery;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 4, 2009  
 */
public class IteratorRenderer {
  
  private boolean bot = false;
  
  IteratorRenderer(boolean bot) {
    this.bot = bot;
  }
  
  String render1(MetaList metas, SearchQuery query) throws Exception {
    StringBuilder builder = new StringBuilder();
    
    if(metas.getData().size() < 1) {
      builder.append(" &nbsp;&nbsp;&nbsp; ");
      buildInfo(builder, query);
      builder.append("<span class=\"other_page\">");
      builder.append("<strong> Không có kết quả</strong> nào phù hợp với mẫu truy vấn.</span>");
      return builder.toString();
    }
    
    int page  = metas.getCurrentPage();
    int start = (page - 1)*10 + 1;
    int end  = start + metas.getData().size() - 1;//(page)*10;
    
    builder.append(" &nbsp;&nbsp;&nbsp; ");
    buildInfo(builder, query);
    builder.append("<span class=\"other_page\"> Kết quả từ <strong>");
    builder.append(start).append("</strong> đến <strong>").append(end);
    builder.append("</strong> trong <strong>");
    DecimalFormat format = new DecimalFormat("###,###.###"); 
    builder.append(format.format(metas.getTotalData())).append("</strong> kết quả.");
//    float time = format.format(query.getTime()/1000);
    if(query != null) {
      builder.append(" (<strong>").append(format.format(query.getTime()/1000f));
      builder.append("</strong> giây)</span>");
    } else {
      builder.append(" (<strong>1</strong> giây)</span>");
    }
//    builder.append(" Khoảng ").append(metas.getTotalPage()).append(" trang phù hợp nhất.");
    
    return builder.toString();
  }
  
  String render(MetaList metas, SearchQuery query) throws Exception {
    StringBuilder builder = new StringBuilder();
    int page  = metas.getCurrentPage();
    int total = metas.getTotalPage();
    if(!bot) total = Math.min(total, 100);
//    System.out.println("========  >"+ bot + " : "+ total);
    int start = -1;
    if(bot) {
      start = Math.max(page-20, 1);
    } else {
      start = Math.max(page-3, 1);
    }
    int end = -1;
//    System.out.println("============= >"+ total);
    if(bot) {
      end = Math.min(page+25, total);
    } else {
      end = Math.min(start+9, total);
    }
    
//    System.out.println(start + " : "+ end);
    
    String metaId = null;
    if(query != null) metaId = query.getArticleId();
    
    if(start > 1){
      builder.append("\n&nbsp;&nbsp;&nbsp;").append("<a class=\"other_page\"  href=\"").
      append("/site/").append(metas.getAction()).append("/1/");
      if(query != null) {
        builder.append("?query=").append(URLEncoder.encode(query.getPattern(), Application.CHARSET));
      }
//      if(query.getRegion() != null) {
//        builder.append("&region=").append(URLEncoder.encode(query.getRegion(), Application.CHARSET));
//      }
      
      if(query != null && query.getDate() > 0) {
        builder.append("&sdate=").append(query.getDate());
      }
      
      if(query != null && query.getPrice() != null) {
        builder.append("&price=").append(query.getPrice());
      }
      
//      String [] actions = query.getActions();
//      if(actions != null) {
//        for(int i = 0; i < actions.length; i++) {
//          builder.append("&action=").append(URLEncoder.encode(actions[i], Application.CHARSET));
//        }
//      }
      if(metaId != null) builder.append("&meta=").append(metaId);
      builder.append("\">").append("1</a>&nbsp;&nbsp;...&nbsp;&nbsp;&nbsp;");
    }
    
    for(int idx = start ; idx <= end; idx++){
      if(idx == page) {
        builder.append("<font color=\"red\" class=\"page_\">");       
        builder.append(String.valueOf(idx));
        builder.append("</font>");
      } else {
        builder.append("<a class=\"other_page\" href=\"");
        builder.append("/site/").append(metas.getAction()).append("/").append(idx).append("/");
        if(metas.getUrl() != null) builder.append("?query=").append(metas.getUrl());
//        if(query.getRegion() != null) {
//          builder.append("&region=").append(URLEncoder.encode(query.getRegion(), Application.CHARSET));
//        }
        if(query != null && query.getDate() > 0) {
          builder.append("&sdate=").append(query.getDate());
        }
        
        if(query != null && query.getPrice() != null) {
          builder.append("&price=").append(query.getPrice());
        }
        
        String [] actions = null;
        if(query != null) actions = query.getActions();
        if(actions != null) {
          for(int i = 0; i < actions.length; i++) {
            builder.append("&action=").append(URLEncoder.encode(actions[i], Application.CHARSET));
          }
        }
        if(metaId != null) builder.append("&meta=").append(metaId);
        builder.append("\">");
        builder.append(String.valueOf(idx));
        builder.append("</a>");      
      }
      if(idx == end) break;
      if(bot) {
        builder.append("  |  ");
      } else {
        builder.append("&nbsp;&nbsp;|&nbsp;&nbsp;");
      }
    }    
    
    if(page < total - 1) {
      builder.append("\n&nbsp;&nbsp;&nbsp;...&nbsp;&nbsp;").append("<a class=\"other_page\" href=\"");
      builder.append("/site/").append(metas.getAction()).append("/").append(String.valueOf(page+1)).append("/");
      if(query != null) {
        builder.append("?query=").append(URLEncoder.encode(query.getPattern(), Application.CHARSET));
      }
//      if(query.getRegion() != null) {
//        builder.append("&region=").append(URLEncoder.encode(query.getRegion(), Application.CHARSET));
//      }
      if(query != null && query.getDate() > 0) {
        builder.append("&sdate=").append(query.getDate());
      }
      
      if(query != null && query.getPrice() != null) {
        builder.append("&price=").append(query.getPrice());
      }
      
      String [] actions = null;
      if(query != null) actions = query.getActions();
      if(actions != null) {
        for(int i = 0; i < actions.length; i++) {
          builder.append("&action=").append(URLEncoder.encode(actions[i], Application.CHARSET));
        }
      }
      if(metaId != null) builder.append("&meta=").append(metaId);
      builder.append("\">");
      builder.append("Trang tiếp</a>&nbsp; ");
    }
    
    return builder.toString();
  }
  
  private void buildInfo(StringBuilder builder, SearchQuery query) {
    String region  = null;
    if(query != null) region = query.getRegion();
    int date = -1;
    if(query != null) date = query.getDate();
    if(region != null){
      builder.append("<span class=\"other_page\">Tìm tại <strong>");
      int index = 0; 
      boolean upper = true;
      while(index < region.length()) {
        char c = region.charAt(index);
        if(upper) {
          builder.append(Character.toUpperCase(c));
        } else {
          builder.append(c);
        }
        upper = Character.isWhitespace(c);
        index++;
      }
      builder.append("</strong>");
      if(date > 0) {
        buildDateInfo(builder, date);
      } else {
        builder.append(".</span>");
      }
    } else  if(date > 0) {
      builder.append("<span class=\"other_page\"> Tìm"); buildDateInfo(builder, date);
    }
  }
  
  
  private void buildDateInfo(StringBuilder builder, int date) {
    builder.append(" trong khoảng <strong>");
    if(date == 7) {
      builder.append(" 1 tuần");
    } else if(date == 30) {
      builder.append(" 1 tháng");
    } else if(date == 90) {
      builder.append(" 3 tháng");
    } else if(date == 180) {
      builder.append(" 6 tháng");
    } else if(date == 365) {
      builder.append(" 1 năm");
    }  else {
      builder.append(date).append(" ngày");
    } 
    builder.append("</strong>.</span>");
  }
  
  
  
}
