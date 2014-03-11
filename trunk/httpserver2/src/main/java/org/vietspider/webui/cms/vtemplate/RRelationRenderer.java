/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.webui.cms.vtemplate;

import java.util.List;

import org.vietspider.bean.Article;
import org.vietspider.bean.Domain;
import org.vietspider.bean.Meta;
import org.vietspider.bean.MetaRelation;
import org.vietspider.bean.Relation;
import org.vietspider.locale.vn.VietnameseConverter;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 30, 2011  
 */
final class RRelationRenderer {
  
  void render(StringBuilder builder, HttpRequestData hrd, 
      List<MetaRelation> metaRels, List<Relation> relations, String metaId) {
    if(metaRels == null) return ;

    builder.append("<ul>");

//    List<MetaRelation> sames = new ArrayList<MetaRelation>();
//    for(MetaRelation ele : list) {
//      if(ele.getPercent() >= 80) sames.add(ele);
//    }
    
    int i = metaRels.size() - 1;
    for(; i > Math.max(metaRels.size() - 3, -1); i--){
      builder.append("<li><dfn>&nbsp;›&nbsp;</dfn>");      
      builder.append("<a ");
      builder.append(renderTooltip(hrd, metaRels.get(i)));
      builder.append(" href=\"").append(hrd.getUriFolder()).append("/detail/");
      builder.append(metaRels.get(i).getId());builder.append("\" class=\"othernews_link\">");
      builder.append(metaRels.get(i).getTitle());
      //          builder.append(" - ");builder.append(String.valueOf(list.get(i).getPercent()));builder.append("%");
      builder.append("</a>");        
      builder.append("</li>");
    }
    
    if(i > 0) builder.append("<li><dfn>&nbsp;›&nbsp;</dfn>");
    
    boolean separator = false;
    for(; i > -1; i--) {
      MetaRelation ele = metaRels.get(i);
      if(separator) builder.append(",&nbsp;");
      builder.append("<a ");
      builder.append(renderTooltip(hrd, ele));
      builder.append("class=\"properties\" href=\"");
      builder.append(hrd.getUriFolder()).append("/detail/").append(ele.getId());
      String alias = VietnameseConverter.toAlias(ele.getTitle());
      builder.append("/").append(alias).append("/");
      builder.append("\">");
      builder.append(ele.getName());
      //          append(" - ");append(String.valueOf(ele.getPercent()));append("%");
      builder.append("</a>");
      if(!separator) separator = true;
    }
    
//    System.out.println(" ===  > "+ metaRels.size() + " : "+ relations.size());
    
    if(metaRels.size() >= 5) {
      builder.append(",&nbsp;<span class=\"properties\"");
//      builder.append(" href=\"");
//      builder.append(hrd.getUriFolder()).append("/thread/1/").append(metaId).append("\">");
      builder.append("<b>tất cả ").append(relations.size()).append(" bài viết</b>");
      builder.append("</span>");
    }
    
    builder.append("</li>");
    
    builder.append("</ul>");
  }

  StringBuilder renderTooltip(HttpRequestData hrd, MetaRelation meta) {
    return renderTooltip(hrd, meta.getTitle(), 
        meta.getDes(), meta.getTime(), meta.getName(), meta.getImage());
  }
  
  StringBuilder renderTooltip(HttpRequestData hrd, Article article) {
    Meta meta = article.getMeta();
    Domain domain = article.getDomain();
    return renderTooltip(hrd, meta.getTitle(), 
        meta.getDesc(), meta.getTime(), domain.getName(), meta.getImage());
  }
  
  static StringBuilder renderTooltip(HttpRequestData hrd, 
      String title, String desc, String time, String source, String img) {
    StringBuilder builder = new StringBuilder();
    if(hrd.isMobile()) return builder;
    
    String uriFolder = hrd.getUriFolder();
    //    meta.setTitle(meta.getTitle().replace("'","'"));
    title = title.replace("\"","'");
    //    meta.setDes(meta.getDes().replace("'","\\'"));
    desc = desc.replace("\"","'");
    builder.append(" title=\"cssbody=[tip_body] cssheader=[tip_header] ");
    builder.append("             header=[");
    builder.append(title);
    if(source != null) {
      builder.append(" (<i>").append(source).append("</i>)");
    }
    builder.append("] ");
    builder.append("             body=[");
    if(img != null && img.trim().length() > 1 && !img.trim().equals("null")) {

      if(img.startsWith("/vietspider/IMAGE/")) {
        img = uriFolder + "/IMAGE/" +  img.substring("/vietspider/IMAGE/".length());
      }

      builder.append("<table align='left' border='0' cellpadding='0' cellspacing='0'>");
      builder.append("  <tbody>");
      builder.append("    <tr>");
      builder.append("      <td> ");
      builder.append("        <img src='").append(img).append("' border='0' height='80'>\n");
      builder.append("      </td>");
      builder.append("    </tr>");
      builder.append("  </tbody>");
      builder.append("</table>");
      //      createImage(imgTemplate, img);
    }
    builder.append(VLayoutRenderer.cutDesc(desc)); 
    builder.append(" (<i>").append(time).append("</i>)");
    builder.append("]\" ");

    return builder;
  }
}
