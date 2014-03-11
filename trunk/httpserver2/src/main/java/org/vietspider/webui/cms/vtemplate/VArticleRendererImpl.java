package org.vietspider.webui.cms.vtemplate;

import java.io.File;
import java.net.URLDecoder;
import java.util.Date;
import java.util.List;

import org.vietspider.bean.Article;
import org.vietspider.bean.Content;
import org.vietspider.bean.Domain;
import org.vietspider.bean.Meta;
import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.common.text.CalendarUtils;
import org.vietspider.locale.vn.VietnameseConverter;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Feb 9, 2007
 */
public class VArticleRendererImpl {

  private String html;
  private ContentCleaner cleaner = new ContentCleaner();

  public VArticleRendererImpl() {
    File file = UtilFile.getFile("system/cms/vtemplate", "Detail.html");
    try {
      html = new String(RWData.getInstance().load(file), Application.CHARSET);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
      html = e.toString();
    }
  }

  public void write(HttpRequestData hrd, Article article) throws Exception {
    Domain domain = article.getDomain();

    Meta meta = article.getMeta();
    Content content = article.getContent();
    String contentValue = "";
    if(content != null && content.getContent() != null) {
      contentValue += content.getContent();
    }

    String uriFolder = hrd.getUriFolder();
    hrd.write(CommonRenderer.getInstance().renderHeader(hrd, meta.getTitle()));

    int start = 0;

    String pattern = "$menu";
    int idx = html.indexOf(pattern);
    if(idx > -1) {
      hrd.write(html.substring(start, idx));
    
      String [] items = getDomainItems(hrd.getReferer(), domain);
      hrd.write(CommonRenderer.getInstance().menu.render(hrd, items));
      
      start = idx + pattern.length();
    }
    
    pattern = "$content";
    idx = html.indexOf(pattern);
    if(idx > -1) {
      StringBuilder builder = new StringBuilder();
      builder.append(html.substring(start, idx));
      builder.append("<p class=\"detail_item_title\">").append(meta.getTitle()).append("</p>"); 
      
//      builder.append("<div class=\"detail-content\">");
//      System.out.println(meta.getPropertyValue("desc.extractor.remove") 
//          + " : "+"true".equals(meta.getPropertyValue("desc.extractor.remove")));
      if("true".equals(meta.getPropertyValue("desc.extractor.remove"))) {
        builder.append("<p class=\"detail-content\">").append(meta.getDesc()).append("</p>");
      }
      cleaner.buildContent(builder, contentValue, uriFolder);
      
//      builder.append("</div>");
      //      builder.append(contentValue);
      hrd.write(builder.toString());
      start = idx + pattern.length();
    } 

    hrd.write(generateLinks(hrd, article));

    if(start < html.length()) {
      hrd.write(html.substring(start));
    }

    hrd.write(CommonRenderer.getInstance().renderBottom(hrd));
  }

  private String generateLinks(HttpRequestData hrd, Article article) throws Exception {
    Meta meta  = article.getMeta();
    StringBuilder builder = new StringBuilder();

    builder.append("<table width=\"100%\" align=\"left\"  border=0><tr><td align=\"right\" valign=top>");

    boolean hasRelation = article.getMetaRelations() != null && article.getMetaRelations().size() > 0;

    builder.append("&nbsp;<span class=\"updated_time_local\">(");

    if(meta.getSourceTime() != null && meta.getSourceTime().trim().length() > 0) {
      try {
        Date date = CalendarUtils.getDateTimeFormat().parse(meta.getSourceTime());
        builder.append(CalendarUtils.getDateTimeFormat().format(date));
      } catch (Exception e) {
        try {
          Date date = CalendarUtils.getDateFormat().parse(meta.getSourceTime());
          builder.append(CalendarUtils.getDateTimeFormat().format(date));
        }catch (Exception e2) {
          LogService.getInstance().setMessage(e, meta.getSourceTime());
        }
      }
    }
    builder.append(")&nbsp;&nbsp;&nbsp;&nbsp;");

    builder.append("<a class=\"othernews_title\" href=\"").append(meta.getSource());
    builder.append("\" style=\"text-decoration: none;\">[");
    builder.append("Xem tin tại nguồn");
    builder.append("]</a>&nbsp;&nbsp;&nbsp;");

    builder.append("<a class=\"othernews_title\" href=\"JavaScript:history.go(");
    String [] params = hrd.getParams();
    if(params != null && params.length > 2) {
      builder.append(params[1]);  
    } else {
      builder.append("-1");
    }
    builder.append(") \">[Trở lại]</a>");

    builder.append("</td></tr>");

    if(hasRelation) {
      try{
        builder.append("<tr><td align=\"left\" valign=top>");
        CommonRenderer.getInstance().rel.render(builder, hrd, 
            article.getMetaRelations(), article.getRelations(), null);
        builder.append("</tr></td>");
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
    
    List<Article> others = article.getPropertyValue("others");
    if(others != null && others.size() > 0) {
      builder.append("<tr><td align=\"left\" valign=top>");
      builder.append("<br/><p><b> Tin khác:</b></p>");
      buildOthers(builder, hrd, others);
      builder.append("</tr></td>");
    }
    builder.append("</table>");
    return builder.toString();
  }

  private String[] getDomainItems(String referer, Domain domain) {
    String [] items = getDomainItems(referer); 
    if(items != null || domain == null) return items;
    items = new String[3];
    items[0] = domain.getDate();
    items[0] = items[0].replace('/', '.');
    items[1] = domain.getGroup()+ "." +domain.getCategory();
    items[2] = domain.getName();
    return items;
  }

  private String[] getDomainItems(String referer) {
    if(referer == null || referer.trim().isEmpty()) return null;
    String separator = "/domain/";
    int index = referer.indexOf(separator);
    if(index < 0) return null;
    try {
      index = referer.indexOf('/', index+separator.length()+1);
      String value = referer.substring(index+1);
      value = URLDecoder.decode(value, "utf-8");
      return value.split("/");
    } catch (Exception e) {
    }
    return null;
  }
  
  private void buildOthers(StringBuilder builder, HttpRequestData hrd, List<Article> articles) {
    builder.append("<ul>");

    for(int i = 0; i < articles.size(); i++) {
      if(articles.get(i) == null) continue;
      Meta meta = articles.get(i).getMeta();
      if(meta == null) continue;
      Domain domain = articles.get(i).getDomain();
      if(domain == null) continue;
//      System.out.println(" bebe " + articles.get(i).getContent());
      
      builder.append("<li><a ");
      builder.append(RRelationRenderer.renderTooltip(hrd, meta.getTitle(), 
          meta.getDesc(), meta.getTime(), domain.getName(), meta.getImage()));
      builder.append(" class=\"othernews_link\" href=\"");
      builder.append(hrd.getUriFolder()).append("/detail/").append(meta.getId());
      String alias = VietnameseConverter.toAlias(meta.getTitle());
      builder.append("/").append(alias).append("/");
      builder.append("\">");
      builder.append(meta.getTitle());
      builder.append(" (<i>").append(domain.getName()).append("</i>)");
      //          append(" - ");append(String.valueOf(ele.getPercent()));append("%");
      builder.append("</a>");
      //      System.out.println(" bebebe ===  >" + meta.getId() + " : "+ meta);
    }
    
    builder.append("</ul>");
  }

}
