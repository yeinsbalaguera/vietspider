/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.webui.search;

import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import org.vietspider.bean.Article;
import org.vietspider.bean.Meta;
import org.vietspider.bean.Relation;
import org.vietspider.common.Application;
import org.vietspider.index.CommonSearchQuery;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 4, 2009  
 */
class MetaRenderer {

  private boolean bot = false;

  public MetaRenderer(boolean bot) {
    this.bot = bot;
  }

  byte[] render(Article article, CommonSearchQuery baseQuery) throws Exception {  
    Meta  meta  = article.getMeta();
    if(meta  == null 
        || meta.getId() == null 
        || meta.getId().equals("null")) return new byte[0];
    
    String query = null;
    if(baseQuery != null) query = baseQuery.getPattern();

    StringBuilder builder = new StringBuilder();
    builder.append("<div class=\"meta\"><a href=\"/site/cached/");
    builder.append(meta.getId());
    builder.append("/").append(meta.getAlias());
    if(query != null) {
      try {
        builder.append("/?query=").append(URLEncoder.encode(query, Application.CHARSET));
      } catch (Exception e) {
        builder.append("/?query=").append(query);
      }
    }
    builder.append("\" target=\"_blank\" class=\"meta_title\"");
    //    builder.append("<li><a href=\"").append(meta.getSource()).append("\" class=\"meta_title\">");

    //System.out.println("  " + meta.getTitle() + " || " + article.getStatus());

    String hlTitle = meta.getPropertyValue("hl.title");
    if(hlTitle == null)  hlTitle = meta.getTitle();
    if(hlTitle.endsWith("...")) {
      builder.append(" title=\"").append(meta.getTitle()).append("\"");
    }
    
    builder.append(">");

    builder.append(hlTitle);
    //    String score = article.getMeta().getPropertyValue("score");
    //    if(score == null) score = "-1";
    //    builder.append(" (").append(score).append(')');
    builder.append("</a><div>");

    //    builder.append(meta.getTitle()).append("</a><div>");


    //build image if has

    boolean image  = meta.getImage() != null && !meta.getImage().trim().isEmpty();
    if(image) {
      builder.append("<table align=\"left\" border=\"0\" width=\"85\" cellpadding=\"0\" cellspacing=\"0\">");
      builder.append("<tbody><tr><td class=\"LoadBox\">");
      builder.append("<img src=\"").append(meta.getImage()).append("\" border=\"0\" height=\"80\" width=\"80\">");
      builder.append("</td></tr> </tbody></table>");
    }

    String hlDesc = meta.getPropertyValue("hl.desc");
    if(hlDesc == null) hlDesc = meta.getDesc();

    buildLink(builder, article, query);
    builder.append("<span class=\"text\">").append(hlDesc).append("...</span>");;
    MetaPropertiesRenderer.buildProperties(baseQuery, bot, builder, article);

    if(image) {
      builder.append("</div></div><div class=\"Separator2\"></div>");
    } else {
      builder.append("</div></div><div class=\"Separator\"></div>");
    }

    return builder.toString().getBytes(Application.CHARSET);
  }

  private String cutURL(String value, int total) {
    if(value == null) return "";
    int max = total - 10;
    StringBuilder builder = new StringBuilder();
    builder.append(value.subSequence(0, Math.min(max, value.length())));
    int min = value.length() - 10;
    if(min < 1) return  builder.toString(); 
    builder.append("...");
    builder.append(value.subSequence(min, value.length()));
    return builder.toString();
  }

  final static short META = 0;
  final static short DETAIL = 1;
 

  private void buildLink(StringBuilder builder, Article article, String query) {
    Meta meta = article.getMeta();
    
    builder.append("<div class=\"othernews_link\">");
    
    String ao =  article.getMeta().getPropertyValue("nlp.action_object");
    if(ao != null) {
      builder.append(ao).append(" - ");
    }
    
    if(!bot) {
      builder.append("<a class=\"othernews_link\" href=\"").append(meta.getSource()).append("\">");
    }
    String host = "";
    try {
      host = new URL(meta.getSource()).getHost();
    } catch (Exception e) {
      host = cutURL(meta.getSource(), 40);
    }
    builder.append(host);//cutURL(meta.getSource(), 50));
    if(!bot) builder.append("</a>");

    String value = meta.getSourceTime();
    if(value != null && value.length() > 0) {
      int idx = value.indexOf(' ');
      if(idx > 0) value = value.substring(0, idx);
      builder.append(" - Rao ngày: ").append(value);
    }

    String time = meta.getTime();
    //    int index = time.indexOf(' ');
    //    if(index > 0) time  = time.substring(0, index);
    builder.append(" - Cập nhật: ").append(time);

//    builder.append(" (").append(article.getScore()).append(')');

    if(!bot && query != null) {
      List<Relation> relations = article.getRelations();
      if(relations != null && relations.size() > 0) {
        builder.append(" - <a class=\"othernews_link\" href=\"/site/thread/1/?query=");
        builder.append(query).append("&meta=").append(meta.getId());
        builder.append("\"><b>");
        builder.append(relations.size()).append(" nội dung </b> tương tự</a>");
      }
    }

    builder.append("</div>");
  }


  public static String uppercase(String text) {
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


}
