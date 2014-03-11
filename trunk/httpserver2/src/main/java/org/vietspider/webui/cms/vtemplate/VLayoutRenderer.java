package org.vietspider.webui.cms.vtemplate;

import java.net.URLEncoder;
import java.util.List;

import org.vietspider.bean.Article;
import org.vietspider.bean.Domain;
import org.vietspider.bean.Meta;
import org.vietspider.common.io.LogService;
import org.vietspider.db.database.MetaList;

/**
 * Created by VietSpider Studio
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 9, 2006
 */
public class VLayoutRenderer {

  public VLayoutRenderer() {
  }

  public void write(HttpRequestData hrd, MetaList list) throws Exception {
    hrd.write("\n\n\n");
    List<Article> articles = list.getData();
    
    for(int i = 0; i < articles.size(); i++) {
      Article article = articles.get(i);
      if(article == null) continue;
      writeMenu(hrd, i, article);
      String text = createMeta(hrd, article);
//      System.out.println("=============================================================");
//      System.out.println(text);
      hrd.write(text);
    }  
    writeOthers(hrd);
    
    hrd.write("\n\n\n");
  } 
  
  private void writeMenu(HttpRequestData hrd, int index, Article article) {
    if(hrd.getPageType() != HttpRequestData.EVENT || index%2 != 1) return;
    String tag = article.getMeta().getPropertyValue("event.tag");
    if(tag == null) return;
    
    StringBuilder builder = new StringBuilder("<div class=\"title_cate_box\">");
    builder.append("<a class=\"head_menu\" href=\"");
    builder.append(hrd.getCurrentDateLink());
    try {
      builder.append(URLEncoder.encode(tag, "utf-8"));
    } catch (Exception e) {
      LogService.getInstance().setMessage(e, e.toString());
    }
    builder.append("/").append("\">");
    String label = tag;
    int idx = tag.indexOf('.');
    if(idx > 0) label = label.substring(idx+1);
    builder.append(label).append("</a>");
    builder.append("</div>");
    hrd.write(builder.toString());
  }
  
  @SuppressWarnings("all")
  private void writeOthers(HttpRequestData hrd) {
    if(hrd.getPageType() != HttpRequestData.EVENT) return;
    List<Article> articles = (List<Article>)hrd.getProperties().get("event.others");
    if(articles == null || articles.size() < 3) return;
    
    StringBuilder builder = new StringBuilder("<div class=\"title_cate_box\">");
    builder.append("<a class=\"head_menu\" href=\"");
    builder.append(hrd.getCurrentDateLink()).append("\">");      
    builder.append("Tin khác trong ngày</a>");
    builder.append("</div>");
    
    builder.append("<ul>");
    for(int i = 0; i < Math.min(10, articles.size()); i++){
      builder.append("<li><dfn>&nbsp;›&nbsp;</dfn>");      
      builder.append("<a ");
      builder.append(CommonRenderer.getInstance().rel.renderTooltip(hrd, articles.get(i)));
      builder.append("       href=\"").append(hrd.getUriFolder()).append("/detail/");
      builder.append(articles.get(i).getId());builder.append("\" class=\"othernews_link\">");
      builder.append(       articles.get(i).getMeta().getTitle());
      //          builder.append(" - ");builder.append(String.valueOf(list.get(i).getPercent()));builder.append("%");
      builder.append("</a>");        
      builder.append("</li>");
    }
    builder.append("</ul>");
    
    
    hrd.write(builder.toString());
  }

  private String createMeta(HttpRequestData hrd, Article article) {  
//    append("\n\n\n<!------- start-------->");
    StringBuilder builder = new StringBuilder();
    
    Meta  meta  = article.getMeta();
    Domain domain = article.getDomain();
    if(meta  == null || meta.getId() == null || meta.getId().equals("null")) {
      return builder.toString();
    }

    StringBuilder href = new StringBuilder();
    href.append(hrd.getUriFolder()).append('/').append("domain").append("/1/");
    String date = null;
    String encCategory = null;
    
    builder.append("<div class=\"box_can_move hpMod\">");
    builder.append("<div id=\"a\" class=\"box_can_move_content hpMod\">");               
    builder.append("<div style=\"DISPLAY: block; HEIGHT: auto\" class=\"box_scroll\">");
    builder.append("<div class=\"hpSet\">");
    builder.append("<div class=\"hpData\">");
    builder.append("<div style=\"DISPLAY: block; HEIGHT: auto\" class=\"hpData_content\">");
    
    builder.append("<div class=\"hpData intro_item\">");

    //render title
    builder.append("<div>");
    
    builder.append("<a class=\"intro_item_title\" href=\"");
    builder.append(hrd.getUriFolder()).append("/detail/").append(meta.getId());
    builder.append("/").append(meta.getAlias()).append("/");
    builder.append("\" >"); 
    builder.append(meta.getTitle());
    builder.append("</a>");
    builder.append("</div>");

  //generate image link

    String img = meta.getImage();
    if(img != null && (img.trim().length() < 1
        || img.trim().equals("null"))) img = null;
    if(img != null) {
      int idx = img.indexOf('/', 1);
      if(idx > 0) img = hrd.getUriFolder() + img.substring(idx);
      builder.append("<img id=\"Col11__ctl0_imgAvatar\" src=\"").append(img);
      builder.append("\" width=\"120\" alt=\"\" align=\"Left\" border=\"0\" />");
    }
    
    String desc = cutDesc(meta.getDesc());
    
    if(desc.length() > 0) {
      int start = desc.indexOf('(');
      int end = desc.indexOf(')');
      if(start > -1 && start < 5 && end  > 0 && end < 20) {
        int idx = desc.indexOf('-', end);
        if(idx > end && idx < end + 4)  end = idx;
        desc = desc.substring(end+1);
      }
    }
    
    if(domain != null){
      String category = domain.getGroup()+ "."+ domain.getCategory();
      try {
        encCategory = URLEncoder.encode(category, "utf-8");
      } catch (Exception e) {
        LogService.getInstance().setMessage(e, e.toString());
      }
      date = domain.getDate().replace('/', '.');
    }

    builder.append("<div>");
//    append("<span align=\"justify\">");
    
    builder.append("<p align=\"justify\" class=\"intro_item_summary\">");
    if(domain != null){
      if(date != null && encCategory != null) {
        builder.append("(<a class=\"sub_menu\" href=\"");
        builder.append(href.toString()).append(date).append("/").append(encCategory);
        try {
          builder.append("/").append(URLEncoder.encode(domain.getName(), "utf-8"));
        } catch (Exception e) {
          LogService.getInstance().setMessage(e, e.toString());
        }
        builder.append("\">");
      }
      builder.append(domain.getName());
      if(date != null && encCategory != null) {
        builder.append("</a>");
      }
    }
    builder.append(")&nbsp;</span>");
    
    builder.append(desc);
    String time = meta.getSourceTime();
    time = time == null ? meta.getTime() : time.replace('/', '.');
    
    builder.append("&nbsp;<span class=\"updated_time_local\"> ("); 
    if(time != null) {
      builder.append(time.substring(time.indexOf(' ')+1));
    } 
    builder.append(")</span>");
    builder.append("</p>");
    
//    if(domain != null){
//      String category = domain.getGroup()+ "."+ domain.getCategory();
//      try {
//        encCategory = URLEncoder.encode(category, "utf-8");
//      } catch (Exception e) {
//        LogService.getInstance().setMessage(e, e.toString());
//      }
//      date =  domain.getDate().replace('/', '.');
//
//      builder.append("<dfn>&nbsp;›&nbsp;</dfn><a class=\"sub_menu\" href=\"");
//      builder.append(href.toString()).append(date).append("/").append(encCategory);
//      builder.append("\">");
//      if(domain.getCategory() != null) {
//        builder.append(domain.getCategory());
//      }
//      builder.append("</a>");
//    }
    
//    if(domain != null){
//      if(date != null && encCategory != null) {
//        builder.append("<dfn>&nbsp;›&nbsp;</dfn><a  href=\"");
//        builder.append(href.toString()).append(date).append("/").append(encCategory);
//        try {
//          builder.append("/").append(URLEncoder.encode(domain.getName(), "utf-8"));
//        } catch (Exception e) {
//          LogService.getInstance().setMessage(e, e.toString());
//        }
//        builder.append("\">");
//      }
//      builder.append(domain.getName());
//      if(date != null && encCategory != null) {
//        builder.append("</a>");
//      }
//    }
//    builder.append("&nbsp;)</span>");
    
    if(article.getMetaRelations() != null
        && article.getMetaRelations().size() > 0) {
//      builder.append("<br/>");
      CommonRenderer.getInstance().rel.render(builder, 
          hrd, article.getMetaRelations(), article.getRelations(), meta.getId());
    }
    
    builder.append("</div>");//end div for description
    
    builder.append("</div>");
    
//    append("<br/>");
    
    
//    builder.append("<div style=\"LINE-HEIGHT: 8px\">&nbsp;</div>");
    builder.append("</div>");
    builder.append("</div>");
    builder.append("</div>");
    builder.append("</div>");
    builder.append("</div>");
//    builder.append("<div style=\"LINE-HEIGHT: 1px\">");
//    builder. append("<div class=\"box_advertise_item\"></div>");
//    builder.append("</div>");
    builder.append("</div>");
    
    return builder.toString();
//    append("<!------- end-------->\n\n\n");
  }
  
  public static String cutDesc(String desc) {
    int start = 0;
    while(start < desc.length()) {
      char c = desc.charAt(start);
      if(Character.isWhitespace(c) || Character.isSpaceChar(c)) {
        start++;
        continue;
      }
      break;
    }
    int idx = desc.indexOf('.', start + 5);
    while(idx > -1 && idx < desc.length() - 1) {
      char c = desc.charAt(idx+1);
      if(Character.isWhitespace(c) || Character.isSpaceChar(c)) break;
      idx = desc.indexOf('.', idx + 5);
    }
    
    int max = 150;
    
    if(idx > 0 && idx - start < max) {
      return desc.substring(start, idx + 1);
    }
    
    idx = start;
    idx = desc.indexOf(',', idx + 5);
    while(idx > -1 && idx < desc.length() - 1) {
      idx = desc.indexOf(',', idx + 5);
      if(idx - start > max) break;
    }
    
    if(idx > 0 && idx - start < max) {
      return desc.substring(start, idx) + "...";
    }
    
    idx = start;
    idx = desc.indexOf(' ', idx + 5);
    while(idx > -1 && idx < desc.length() - 1) {
      idx = desc.indexOf(' ', idx + 5);
      if(idx - start > max) break;
    }

    if(idx > 0) return desc.substring(start, idx) + "...";
    return desc.substring(start);
  }
  
//  private void createImage(String html, String img) throws Exception {
//    String pattern = "$image";
////    System.out.println(" ====  > " + img);
//    int idx = html.indexOf(pattern);
//    int start = 0;
//    if(idx > -1){
//      append(html.substring(start, idx));append(img);
//      start = idx + pattern.length();
//    }
//    if(start < html.length()) append(html.substring(start));
//  }

}
