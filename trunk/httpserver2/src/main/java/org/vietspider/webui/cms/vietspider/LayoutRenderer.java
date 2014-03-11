package org.vietspider.webui.cms.vietspider;

import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.vietspider.bean.Article;
import org.vietspider.bean.Domain;
import org.vietspider.bean.Meta;
import org.vietspider.bean.MetaRelation;
import org.vietspider.bean.NLPData;
import org.vietspider.bean.NLPRecord;
import org.vietspider.common.Application;
import org.vietspider.common.Install;
import org.vietspider.common.io.LogService;
import org.vietspider.db.database.MetaList;
import org.vietspider.db.log.user.UserLog;
import org.vietspider.db.log.user.UserLogDatabase;
import org.vietspider.db.log.user.UserLogDatabases;
import org.vietspider.webui.cms.BufferWriter;

/**
 * Created by VietSpider Studio
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 9, 2006
 */
public class LayoutRenderer extends BufferWriter {
  
  public LayoutRenderer() {
  }
  
  public void write(OutputStream out, String viewer, MetaList list) {  
    output = out;
    CommonResources resources = CommonResources.INSTANCE;
    try {
      String html = resources.getLayoutTemplate();
      
      String pattern = "$iterator";
      int idx = html.indexOf(pattern);
      int start = 0;
      if(idx > -1){
        append(html.substring(start, idx));
        if(list.getExtension() == null 
            || list.getExtension().trim().isEmpty()){
          buildPageIteratorByIndex(viewer, list);
        } else {
          buildPageIterator2(viewer, list);
        }
        start = idx + pattern.length();
      }
      
      pattern = "$metas";
      idx = html.indexOf(pattern, start);
      if(idx > -1){
        append(html.substring(start, idx));
        
        String meta = resources.getMetaTemplate();
        String image = resources.getImageTemplate();
        String relation = resources.getRelationTemplate();
        
        boolean single = Application.LICENSE != Install.SEARCH_SYSTEM && Application.GROUPS.length == 1;
        if(list.getData().size() < 1) {
          append("<tr><td height=\"5\" align=\"center\" width=\"450\">");
          append(resources.getEmptyData());
          append("</td></tr>");
        } else {
          for(Article ele : list.getData()) {
            if(ele == null) continue;
            createMeta(viewer, ele, meta, image, relation, single);      
          }  
        }
        start = idx + pattern.length();
      }
      
      pattern = "$iterator";
      idx = html.indexOf(pattern, start);
      if(idx > -1){
        append(html.substring(start, idx));
        if(list.getExtension() == null 
            || list.getExtension().trim().isEmpty()) {
          buildPageIteratorByIndex(viewer, list);
        } else {
          buildPageIterator2(viewer, list);
        }
        start = idx + pattern.length();
      }
      
      if(start < html.length()) append(html.substring(start));
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }  
  
  void createPage(String viewer, MetaList list)  throws Exception {  
    if(list.getExtension() == null 
        || list.getExtension().trim().isEmpty()) {
      buildPageIteratorByIndex(viewer, list);
    } else {
      buildPageIterator2(viewer, list);
    }
    append("\n<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" valign=\"center\"><tbody>\n");   
  }

  void createEndPage(String viewer, MetaList list)  throws Exception {
    append("\n</tbody></table>");
    if(list.getExtension() == null 
        || list.getExtension().trim().isEmpty()) {
      buildPageIteratorByIndex(viewer, list);
    } else {
      buildPageIterator2(viewer, list);
    }
  }
  
  private void buildPageIteratorByIndex(String viewer, MetaList list) throws Exception {
    int page  = list.getCurrentPage();
    int total = list.getTotalPage();
    int start = Math.max(page-3, 1);
    int end = Math.min(start+6, total);
    
    if(start > 1){
      append("\n&nbsp;&nbsp;&nbsp;");append("<a class=\"other_page\" href=\"");
      append("/");append(viewer);append("/");append(list.getAction());append("/1/");
      append(list.getUrl());
     
      append("\">");      
      append("1</a>&nbsp;&nbsp;...&nbsp;&nbsp;&nbsp;");
    }
    
    for(int idx = start ; idx <= end; idx++){
      if(idx == page) {
        append("<font color=\"red\" class=\"page_\">");       
        append(String.valueOf(idx));
        append("</font>");
      } else {
        append("<a class=\"other_page\" href=\"");
        append("/");append(viewer);append("/");append(list.getAction());append("/");
        append(String.valueOf(idx));append("/");
        append(list.getUrl());
        append("\">");
        append(String.valueOf(idx));
        append("</a>");      
      }
      if(idx == end) break;
      append("&nbsp;&nbsp;|&nbsp;&nbsp;");
    }    
    
    if(end < total){
      append("\n&nbsp;&nbsp;&nbsp;...&nbsp;&nbsp;");append("<a class=\"other_page\" href=\"");
      append("/");append(viewer);append("/");append(list.getAction());append("/");
      append(String.valueOf(total));append("/");
      append(list.getUrl());
      append("\">");      
      append(String.valueOf(total));append("</a>&nbsp;&nbsp;&nbsp;");
    }
    
    append(" &nbsp;&nbsp;&nbsp; <span class=\"other_page\">( ");
    append(String.valueOf(total));
    append(" ) </span>");
  }
  
  private void buildPageIterator2(String viewer, MetaList list) throws Exception {
    int page  = list.getCurrentPage();
    int total = list.getTotalPage();
    
    append("\n&nbsp;&nbsp;&nbsp;");append("<a class=\"other_page\" href=\"");
    append("/");append(viewer);append("/");append(list.getAction());append("/1/");
    append(list.getUrl());append("/");append(list.getExtension());
    append("\">");      
    append("<strong>|<<</strong></a>&nbsp;&nbsp;&nbsp;");
    
    if(page > 1) {
      append("\n&nbsp;&nbsp;&nbsp;");append("<a class=\"other_page\" href=\"");
      append("/");append(viewer);append("/");append(list.getAction());append("/");
      append(String.valueOf(page-1));append("/");
      append(list.getUrl());append("/");append(list.getExtension());
      append("\">");  
      append("<strong><</strong></a>&nbsp;&nbsp;&nbsp;");
    }
    
    append("<font color=\"red\" class=\"page_\"><strong>");       
    append(String.valueOf(page)); append(" of "); append(String.valueOf(total)); 
    append("</strong></font>");
    
    if(page < total) {
      append("\n&nbsp;&nbsp;&nbsp;");append("<a class=\"other_page\" href=\"");
      append("/");append(viewer);append("/");append(list.getAction());append("/");
      append(String.valueOf(page+1));append("/");
      append(list.getUrl());append("/");append(list.getExtension());
      append("\">");  
      append("<strong>></strong></a>&nbsp;&nbsp;&nbsp;");
    }
    
    append("\n&nbsp;&nbsp;&nbsp;");append("<a class=\"other_page\" href=\"");
    append("/");append(viewer);append("/");append(list.getAction());append("/");
    append(String.valueOf(total));append("/");
    append(list.getUrl());append("/");append(list.getExtension());
    append("\">");      
    append("<strong>>>|</strong></a>&nbsp;&nbsp;&nbsp;");
  }
  
  void createMeta(String viewer, Article article, String metaTemplate, 
                  String imageTemplate, String relationTempl, boolean single) throws Exception {  
    Meta  meta  = article.getMeta();
    Domain domain = article.getDomain();
    if(meta  == null || meta.getId() == null || meta.getId().equals("null")) return;
    String pattern = "$link";
    int idx = metaTemplate.indexOf(pattern);
    int start = 0;
    
    if(idx > -1){
      append(metaTemplate.substring(start, idx));
      append("/");append(viewer);append("/DETAIL/");append(meta.getId());
      start = idx + pattern.length();
    }
//    System.out.println("  " + meta.getTitle() + " || " + article.getStatus());
    pattern = "$meta_title";
    idx = metaTemplate.indexOf(pattern);
    
    UserLogDatabase userLogDb = UserLogDatabases.getService().getUserLogDb(meta.getId(), false);
    UserLog userLog = userLogDb == null ? null : userLogDb.readTopAction(meta.getId());
    int status = Article.WAIT;
    if(userLog == null) {
      status = article.getStatus();
    } else  if(article.getStatus() > userLog.getAction()) {
      status = article.getStatus();
    } else if(article.getStatus() > userLog.getAction()) {
      status = userLog.getAction();
    } else {
      status = userLog.getAction();
    }
//    System.out.println("  ===  => "+ article.getStatus()+ " : "+ userLog.getAction());
    
    if(idx > -1){
      append(metaTemplate.substring(start, idx));
      switch (status) {
      case Article.READ:
        append("meta_title_visited");
        break;
      case Article.SYNCHRONIZED:
        append("meta_title_synchronized");
        break;
      default:
        append("meta_title");
        break;
      }
      start = idx + pattern.length();
    }


    String title = meta.getPropertyValue("hl.title");
    if(title == null || title.trim().isEmpty()) title = meta.getTitle();
    
    pattern = "$title";
    idx = metaTemplate.indexOf(pattern);
    if(idx > -1) {
      append(metaTemplate.substring(start, idx));append(title);
      start = idx + pattern.length();
      generateUserLog(status, userLog);
    }

    pattern = "$image";
    idx = metaTemplate.indexOf(pattern);
    if(idx > -1){
      append(metaTemplate.substring(start, idx));
      String img = meta.getImage();
      if(img != null && img.trim().length() > 1 && !img.trim().equals("null")) {
        createImage(imageTemplate, img);
      }
      start = idx + pattern.length();
    }

    String desc = meta.getPropertyValue("hl.desc");
    if(desc == null || desc.trim().isEmpty()) desc = meta.getDesc();
    
    pattern = "$description";
    idx = metaTemplate.indexOf(pattern);
    if(idx > -1){
      append(metaTemplate.substring(start, idx));
      append(desc);
      append("<br/>");
      start = idx + pattern.length();
    }
    
    NLPRecord record = article.getNlpRecord();
    if(record != null) {
//      builder.append("<tr><td >");
      StringBuilder builder = new StringBuilder();
      
//      String value = meta.getPropertyValue("nlp.product");
//      if(value != null && value.length() > 0) {
//        builder.append("<p class=\"properties\">&nbsp;&nbsp;&nbsp;");
//        builder.append(value);
//        builder.append("</p>");
//      }
      
      builder.append("<p class=\"properties\">");
      boolean next = false;
      
      Collection<?> collection = record.getData(NLPData.ACTION_OBJECT);
      if(collection != null && collection.size() > 0) {
        Iterator<?> iterator = collection.iterator();
        int counter = 0;
        while(iterator.hasNext()) {
          String value = iterator.next().toString();
          if(counter > 0) builder.append(',').append(' ');
          builder.append(NLPData.action_object(value));
          counter++;
          next = true;
        }
      }
      
      String owner = meta.getPropertyValue("owner");
      if(owner != null && "true".equals(owner)) {
        if(next) builder.append("  <b>-</b> ");
        builder.append("Chính chủ");
        next = true;
      }
      
      collection = record.getData(NLPData.AREA);
      if(collection != null && collection.size() > 0) {
        if(next) builder.append("  <b>-</b> ");
        builder.append("Diện tích: ");
        Iterator<?> iterator = collection.iterator();
        int counter = 0;
        while(iterator.hasNext()) {
          String value = iterator.next().toString();
          if(counter > 1) builder.append(',').append(' ');
          builder.append(value);
          counter++;
          next = true;
        }
      }
      
//      String value = meta.getPropertyValue("nlp.area");
//      if(value != null && value.length() > 0) {
//        if(next) builder.append("  <b>-</b> ");
//        builder.append("Diện tích: ").append(value);
//        next = true;
//      }
      
//      value = meta.getPropertyValue("nlp.price");
//      if(value != null && value.length() > 0) {
//        if(next) builder.append("  <b>-</b> ");
//        builder.append("Giá: ").append(value);
//        next = true;
//      }
      
      collection = record.getData(NLPData.PRICE);
      if(collection != null && collection.size() > 0) {
        if(next) builder.append("  <b>-</b> ");
        builder.append("Giá: ");
        Iterator<?> iterator = collection.iterator();
        int counter = 0;
        while(iterator.hasNext()) {
          String value = iterator.next().toString();
          if(counter > 1) builder.append(',').append(' ');
          builder.append(value);
          counter++;
          next = true;
        }
      }
      
//      value = meta.getPropertyValue("nlp.phone");
//      if(value != null && value.length() > 0) {
//        if(next) builder.append("  <b>-</b> ");
//        builder.append(value);
//        next = true;
//      }
      
      List<String> phones = new ArrayList<String>();
      collection = record.getData(NLPData.MOBILE);
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
      
      if(phones.size() > 0 && next) {
        builder.append("  <b>-</b> Phone: ");
      }
      
      for(int i = 0; i < phones.size(); i++) {
        if(i > 0) builder.append(',').append(' ');
//        subBuilder.append(phones.get(i));
        builder.append(phones.get(i));
        next = true;
      }
      
      collection = record.getData(NLPData.ADDRESS);
      if(collection != null && collection.size() > 0) {
        Iterator<?> iterator = collection.iterator();
        while(iterator.hasNext()) {
          if(next) builder.append("  <b> - </b> ");
          builder.append(" <b>").append(iterator.next().toString()).append("</b>");
          next = true;
        }
      }
      
//      value = meta.getPropertyValue("region");
//      if(value != null && value.length() > 0) {
//        if(next) builder.append("  <b> - </b> ");
//        builder.append(" <b>").append(value).append("</b>");
//      }
      
      builder.append("</p>");
      append(builder.toString());
    }

    pattern = "$relation";
    idx = metaTemplate.indexOf(pattern);
    if(idx > -1){
      append(metaTemplate.substring(start, idx));
      createRelation(output, viewer, article.getMetaRelations(), relationTempl, meta.getId());
      start = idx + pattern.length();
    }

    pattern = "$info";
    idx = metaTemplate.indexOf(pattern);
    if(idx > -1){
      append(metaTemplate.substring(start, idx));
      if(domain != null) {
        if(single) {
          String label = domain.getCategory();
          if(label != null) append(label.substring(label.indexOf('.')+1));
        } else {
          if(domain.getCategory() != null) append(domain.getCategory());
        }
        append(" / ");  append(domain.getName());append(" / ");
      } else {
        append(""); 
      }
      String sourceTime = meta.getSourceTime();
      if(sourceTime == null) {
        try {
          URL url  = new URL(meta.getSource());
          append(" ~");append(url.getHost());append(" /");
        } catch (Exception e) {
          append(" ~ /");
        }
      } else {
        sourceTime = sourceTime.replace('/', '.');
        append(sourceTime);append(" / ");
      }
      String time = meta.getTime();
      if(time != null) {
        append(time.substring(time.indexOf(' ')+1));
      } else {
        LogService.getInstance().setMessage(null, meta.getId() + " - time is null");
      }
      start = idx + pattern.length();
    }

    if(start < metaTemplate.length()) append(metaTemplate.substring(start));
  }
  
  void createImage(String html, String img) throws Exception {
    String pattern = "$image";
    int idx = html.indexOf(pattern);
    int start = 0;
    if(idx > -1){
      append(html.substring(start, idx));append(img);
      start = idx + pattern.length();
    }
    if(start < html.length()) append(html.substring(start));
  }

  public void createRelation(OutputStream out, 
      String viewer, List<MetaRelation> list, String html, String metaId) throws Exception {
    this.output = out;
    if(list == null) return;
    String pattern = "$relation";
    int idx = html.indexOf(pattern);
    int start = 0;
    List<MetaRelation> sames = new ArrayList<MetaRelation>();
    if(idx > -1){
      for(MetaRelation ele : list) {
        if(ele.getPercent() >= 80) sames.add(ele);
      }
      append(html.substring(start, idx));      
      if(sames.size() > 0) {
        append("<tr>");
        append("  <td>"); 
        boolean separator = false;
        for(MetaRelation ele : sames) {
          if(separator) append(", ");
          append("  <a ");
          renderTooltip(ele);
          append("     class=\"othernews_link\"");
          append("     href=\"/");append(viewer);append("/DETAIL/");append(ele.getId());append("\">");
          append(ele.getName());
          append(" - ");append(String.valueOf(ele.getPercent()));append("%");
          append("  </a>");
          if(!separator) separator = true;
          list.remove(ele);
        }
        append("  </td>");
        append("</tr>");
      }
      
      int i = 0;
      for(; i < Math.min(3, list.size()); i++){
          append("<tr>");
          append("  <td class=\"othernews_link\">"); 
          append("    <span class=\"othernews_nut\">-</span>");
          append("    <a ");
          renderTooltip(list.get(i));
          append("       href=\"/");append(viewer);append("/DETAIL/");append(list.get(i).getId());append("\" class=\"othernews_link\">");
          append(       list.get(i).getTitle());
          append(" - ");append(String.valueOf(list.get(i).getPercent()));append("%");
          append("    </a>");        
          append("  </td>");
          append("</tr>");
      }
      
      if(i < list.size()){
        append("<tr>");
        append("  <td>"); 
        boolean separator = false;
        int max =  metaId != null ? Math.min(i+9, list.size()) : list.size();
        for(; i < max; i++){
          if(separator) append(", ");
          append("  <a ");
          renderTooltip(list.get(i));
          append("     class=\"othernews_link\"");
          append("     href=\"/");append(viewer);append("/DETAIL/");append(list.get(i).getId());append("\">");
          append(list.get(i).getName());
          append(" - ");append(String.valueOf(list.get(i).getPercent()));append("%");
          append("  </a>");
          if(!separator) separator = true;
        }
        
        if(i < list.size()) {
          append(",  <a class=\"othernews_link\"");
          append("     href=\"/");append(viewer);append("/THREAD/1/");append(metaId);append("\">");
          append("...");
          append("  </a>");
        }
          
        append("    </div>");
        append("  </td>");
        append("</tr>");
      }
      start = idx + pattern.length();
    }
    if(start < html.length()) append(html.substring(start));
  }
  
  private void renderTooltip(MetaRelation meta) throws Exception {
//    meta.setTitle(meta.getTitle().replace("'","'"));
    meta.setTitle(meta.getTitle().replace("\"","'"));
//    meta.setDes(meta.getDes().replace("'","\\'"));
    meta.setDes(meta.getDes().replace("\"","'"));
    append(" title=\"cssbody=[tip_body] cssheader=[tip_header] ");
    append("             header=[");
    append(meta.getTitle());append(" (<i>"); append(meta.getName());append("</i>)"); 
    append("] ");
    append("             body=[");
    String img = meta.getImage();
    if(img != null && img.trim().length() > 1 && !img.trim().equals("null")) {
      append("<table align='left' border='0' cellpadding='0' cellspacing='0'>");
      append("  <tbody>");
      append("    <tr>");
      append("      <td> ");
      append("        <img src='");append(img);append("' border='0' height='80'>\n");
      append("      </td>");
      append("    </tr>");
      append("  </tbody>");
      append("</table>");
//      createImage(imgTemplate, img);
    }
    append(meta.getDes()); 
    append(" (<i>"); append(meta.getTime()); append("</i>)");
    append("]\" ");
  }
  
  private void generateUserLog(int status, UserLog userLog) throws Exception  {
    CommonResources resources = CommonResources.INSTANCE;
    
    switch (status) {
    case Article.READ:
      append("   <span class=\"updated_time_local\">(");
      if(userLog != null) {
        append(userLog.getUserName());append(" ");
        append(resources.getReadContent().toLowerCase());
      } else {
        append(resources.getReadContent());
      }
      append(")</span>");
      break;
    case Article.EDITED:
      append("   <span class=\"updated_time_local\">(");
      if(userLog != null) {
        append(userLog.getUserName());append(" "); 
        append(resources.getEditedContent().toLowerCase());
      } else {
        append(resources.getEditedContent());
      }
      append(")</span>");
      break;
    case Article.SYNCHRONIZED:
      append("   <span class=\"updated_time_local\">(");
      if(userLog != null) {
        append(userLog.getUserName());append(" ");
        append(resources.getSynchronizedContent().toLowerCase());
      } else {
        append(" <i>");append(resources.getSynchronizedContent());append("</i>");
      }
      append(")</span>");
      break;
    default:
      break;
    }
  }
  
}
