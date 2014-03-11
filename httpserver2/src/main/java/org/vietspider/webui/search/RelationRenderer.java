/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.webui.search;

import java.net.URL;
import java.util.List;

import org.vietspider.bean.MetaRelation;
import org.vietspider.common.io.LogService;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 4, 2009  
 */
class RelationRenderer {

  StringBuilder createRelation(List<MetaRelation> metas, String metaId, String query) throws Exception {
    StringBuilder builder = new StringBuilder();

    if(metas == null || metas.size() < 1) return  builder;
    builder.append("<table align=\"left\" border=\"0\" valign=\"top\"><tbody>");
    int i = 0; 
    builder.append("<tr>");
    builder.append("  <td> "); 
    for(;i < Math.min(2, metas.size()); i++){
      MetaRelation mr = metas.get(i);
      if(mr == null) continue;
      builder.append("<a  href=\"/site/cached/");
      builder.append(mr.getId()).append('/').append(mr.getAlias()).append("/?query=").append(query);
      builder.append("\" class=\"sub_title\">").append(mr.getTitle()).append("</a>"); 
      builder.append("&nbsp;&nbsp;&nbsp;<span class=\"source_name\">");
      String site  = mr.getName();
      if(mr.getSource() != null) {
        try {
          site = new URL(mr.getSource()).getHost();
        } catch (Exception e) {
          LogService.getInstance().setThrowable(e);
        }
      }
      builder.append(site).append("</span>").append("<br>");
    }
    builder.append("  </td>");
    builder.append("</tr>");
    
    builder.append("<tr>");
    builder.append("  <td class=\"othernews_link\"> "); 
    
    for(; i < Math.min(6, metas.size()); i++){
      MetaRelation mr = metas.get(i);
      if(mr == null) continue;
      if(i > 2) builder.append("  -  ");
      builder.append("<a  href=\"/site/cached/");
      builder.append(mr.getId()).append('/').append(mr.getAlias()).append("/?query=").append(query);
      String site  = mr.getName();
      if(mr.getSource() != null) {
        try {
          site = new URL(mr.getSource()).getHost();
        } catch (Exception e) {
          LogService.getInstance().setThrowable(e);
        }
      }
      builder.append("\" class=\"othernews_link\">").append(site).append("</a>");
    }
    
    if(metas.size() > 5) {
      builder.append("&nbsp;&nbsp;&nbsp;&nbsp;<a class=\"othernews_link\" href=\"/site/thread/1/?query=");
      builder.append(query).append("&meta=").append(metaId);
      builder.append("\"><b>có ");
      builder.append(metas.size()).append(" nội dung tương tự »</b> </a>");
    }
    builder.append("  </td>");
    builder.append("</tr>");
    
    builder.append("</tbody></table>");
    return builder;
  }
 
}
