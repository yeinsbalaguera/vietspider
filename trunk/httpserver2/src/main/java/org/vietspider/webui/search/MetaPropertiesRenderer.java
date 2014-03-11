/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.webui.search;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.vietspider.bean.Article;
import org.vietspider.bean.Meta;
import org.vietspider.bean.NLPData;
import org.vietspider.bean.NLPRecord;
import org.vietspider.chars.CharsUtil;
import org.vietspider.chars.TextSpliter;
import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.index.CommonSearchQuery;
import org.vietspider.index.SearchQuery;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 22, 2011  
 */
class MetaPropertiesRenderer {

  static void buildProperties(CommonSearchQuery query, 
      boolean bot, StringBuilder builder, Article article) {
    Meta meta = article.getMeta();
    NLPRecord record = article.getNlpRecord();

    StringBuilder subBuilder = new StringBuilder();
    if(record == null) {
      builder.append("<div></div>");
      return;
    }

    
    String address = meta.getPropertyValue("nlp.address");
    if(address != null) {
      String value = uppercase(address);
      String label = CharsUtil.cutLabel(value, 30);
      String link = "#";

      if(!bot && value.length() > 0) {
        link = "/site/search/1/?query=field:address:";
        try {
          link += URLEncoder.encode(value, Application.CHARSET);
        } catch (Exception e) {
          link = "#";
        }
      }

      subBuilder.append("<a href=\"").append(link).append("\" ");
      subBuilder.append("title=\"").append(value).append("\" class=\"properties\">");
      subBuilder.append(label);
      subBuilder.append("</a>");
    }

    String value = meta.getPropertyValue("owner");
    if("true".equals(value)) {
      if(subBuilder.length() > 0) subBuilder.append(" - ");
      subBuilder.append("<span class=\"properties2\">Chính chủ</span>");
    }

    value = meta.getPropertyValue("nlp.area"); 
    //        record.getOneData(NLPData.AREA_SHORT);
    if(value != null && value.length() > 0) {
      if(subBuilder.length() > 0) subBuilder.append(" - ");
//      if(value.length() > 30) value = value.substring(0, 30);
      subBuilder.append("<span class=\"properties2\">");
      subBuilder.append(buildArea(article, value, query)).append("</span>");
    }

    value = meta.getPropertyValue("nlp.price");
    if(value != null && value.length() > 0) {
      if(subBuilder.length() > 0) subBuilder.append(" - ");
      subBuilder.append("<span class=\"properties2\">");
      subBuilder.append(buildPrice(article, value, query)).append("</span>");
    }

    List<String> phones = new ArrayList<String>();
    Collection<?> collection = record.getData(NLPData.MOBILE);
    if(collection != null && collection.size() > 0) {
      Iterator<?> iterator = collection.iterator();
      while(iterator.hasNext()) {
        phones.add(iterator.next().toString());
      }
    }
    collection = record.getData(NLPData.TELEPHONE);
    if(collection != null && collection.size() > 0) {
      Iterator<?> iterator = collection.iterator();
      while(iterator.hasNext()) {
        phones.add(iterator.next().toString());
      }
    }

    if(phones.size() > 0 
        && subBuilder.length() > 0) subBuilder.append(" - ");
    int max = phones.size();
    max = Math.min(2, phones.size());
    for(int i = 0; i < max; i++) {
      if(i > 0) subBuilder.append(", ");
      if(!bot) {
        subBuilder.append("<a  href=\"/site/search/1/?query=");
        subBuilder.append("field:phone:").append(phones.get(i));
        //        subBuilder.append("&search=Tìm+kiếm\" class=\"properties\">");
        subBuilder.append("\" class=\"properties\">");
      }
      subBuilder.append(phones.get(i));
      if(!bot) subBuilder.append("</a>");
      if(builder.length() > 500) break;
    }

    builder.append("<div class=\"properties\">");
    builder.append(subBuilder);
    builder.append("</div>");
  }

  static String uppercase(String text) {
    if(text == null) return null;
    StringBuilder builder = new StringBuilder();
    int index = 0; 
    boolean upper = true;
    while(index < text.length()) {
      char c = text.charAt(index);
      if(upper) {
        builder.append(Character.toUpperCase(c));
      } else {
        builder.append(c);
      }
      upper = Character.isWhitespace(c) || c == '-';
      index++;
    }
    return builder.toString();
  }
  
  private static String buildArea(Article article, String value, CommonSearchQuery query) {
    Meta meta = article.getMeta();
    if(!"no".equals(meta.getPropertyValue("query.area"))) return value; 
    
    StringBuilder builder = new StringBuilder();
    int index = value.indexOf("->");
    if(index > -1) {
      builder.append("<a href=\"");
      String area = value.substring(0, index) + " " + value.substring(index+2);
      appendArea(builder, query, area);
      builder.append("\" class=\"properties2\">").append(value).append("</a>");
    } else {
      TextSpliter spliter = new TextSpliter();
      List<String> list = spliter.toList(value, '-');
      for(int i = 0; i < list.size(); i++) {
        if(i > 0) builder.append('-');
        builder.append("<a href=\"");
        appendArea(builder, query, list.get(i));
        builder.append("\" class=\"properties2\">");
        builder.append(list.get(i)).append("</a>");
      }
    }
    return builder.toString();
  }
  
  private static String buildPrice(Article article, String value, CommonSearchQuery query) {
    Meta meta = article.getMeta();
    if(!"no".equals(meta.getPropertyValue("query.price"))) return value; 
    
    StringBuilder builder = new StringBuilder();
    TextSpliter spliter = new TextSpliter();
    List<String> list = spliter.toList(value, ',');
    for(int i = 0; i < list.size(); i++) {
      if(i > 0) builder.append(", ");
      builder.append("<a href=\"");
      String price = list.get(i).trim();
      appendPrice(builder, query, price);
      builder.append("\" class=\"properties2\">");
      builder.append(list.get(i).trim()).append("</a>");
    }
    return builder.toString();
  }
  
  private static void appendPrice(StringBuilder builder, CommonSearchQuery query, String value) {
    value = value.trim();
    int index1 = value.indexOf('/');
    int index2 = value.indexOf(' ');
//    System.out.println(value + " == > " + index1 +  " : "+ index2 );
    if(index1 < 0 || index2 < 0 || index2 > index1) {
      builder.append('#');
      return;
    }
    String type = value.substring(index1+1);
    String unit = value.substring(index2+1, index1);
    String number = value.substring(0, index2);
//    System.out.println(number + " : "+ unit + " : "+ type);
    double real = -1;
    if(unit.equals("triệu")) {
      try {
        real = Double.valueOf(number);
      } catch (Exception e) {
      }
    } else if(unit.equals("tỷ")) {
      try {
        real = Double.valueOf(number)*1000;
      } catch (Exception e) {
      }
    }
    
    if(real < 0) {
      builder.append('#');
      return;
    }
    
    double max = real;
    double min = real;
    
    if(type.equals("tổng")) {
      max += 50;
      if(min > 50) min -= 50;
      type = "_1";
    } else if(type.equals("m2")) {
      max += 5;
      if(min > 5) min -= 5;
      type = "_2";
    } else {
      max += 1;
      if(min > 1) min -= 1;
      type = "_3";
    }
    
    builder.append("/site/search/1/?");
    try { 
      builder.append("query=").append(URLEncoder.encode(query.getPattern(), Application.CHARSET));
    } catch (Exception e) {
      LogService.getInstance().setMessage(e, e.toString());
    }
    
    if(query.getDate() > 0) {
      builder.append("&sdate=").append(query.getDate());
    }
    
    builder.append("&price=").append(min).append('-').append(max).append(type);
  }
  
  private static void appendArea(StringBuilder builder, CommonSearchQuery query, String value) {
    builder.append("/site/search/1/?");
    try { 
      String pattern = query.getPattern() + " " + value;
      builder.append("query=").append(URLEncoder.encode(pattern, Application.CHARSET));
    } catch (Exception e) {
      LogService.getInstance().setMessage(e, e.toString());
    }
    
    if(query.getDate() > 0) {
      builder.append("&sdate=").append(query.getDate());
    }
    
    if(query.getPrice() != null) {
      builder.append("&price=").append(query.getPrice());
    }
  }
  
  public static void main(String[] args) {
    SearchQuery query = new SearchQuery("aaa");
    StringBuilder builder = new StringBuilder();
    appendPrice(builder, query, "950 triệu/tổng");
    System.out.println(builder);
  }
  
  
}
