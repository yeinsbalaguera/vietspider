package org.vietspider.webui.cms.vietspider;

import java.io.OutputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.vietspider.bean.Article;
import org.vietspider.bean.Content;
import org.vietspider.bean.Domain;
import org.vietspider.bean.Meta;
import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.text.CalendarUtils;
import org.vietspider.db.SystemProperties;
import org.vietspider.server.handler.cms.metas.EditContentHandler;
import org.vietspider.webui.cms.BufferWriter;
import org.vietspider.webui.cms.CMSService;
import org.vietspider.webui.cms.render.ArticleRenderer;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Feb 9, 2007
 */
public class ArticleRendererImpl extends BufferWriter implements ArticleRenderer {
  
  private String [] params;
  private boolean renderDesc = false;
  
  public void setParams(String [] params) {
    this.params = params;
    SystemProperties system = SystemProperties.getInstance();
    renderDesc = "true".equals(system.getValue("desc.extractor.remove"));
  }
  
  private Pattern htmlPattern  = Pattern.compile("<html", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
  private Pattern xmlPattern  = Pattern.compile("<[?]xml", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
//  private Pattern bodyPattern  = Pattern.compile("<body", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
  
  public String write(OutputStream output_, String viewer, String [] cookies, Article article, String referer) throws Exception {
    this.output = output_;
    CMSService cmsService = CMSService.INSTANCE;;
    boolean site = viewer.equals(cmsService.getSiteViewer());
    Domain domain = article.getDomain();
    
    Content content = article.getContent();
    String contentValue = "";
    if(content != null && content.getContent() != null) {
      contentValue += content.getContent();
    }
    
//    Meta meta = article.getMeta();
//    File file  = new File("D:\\Temp\\quan\\test2\\", meta.getId() + ".txt");
//    HTMLNode root =  new HTMLParser2().createDocument(contentValue).getRoot();
//    TextRenderer2 textRenderer = new TextRenderer2(root, TextRenderer.RENDERER);
//    String text = textRenderer.getTextValue().toString();
//    org.vietspider.common.io.RWData.getInstance().save(file, text.getBytes(Application.CHARSET));
    
    if(domain != null && domain.getGroup().equals("XML")) {
      append(contentValue);
      String osName = System.getProperty("os.name").toLowerCase();
      if(osName.equals("linux") || osName.indexOf("mac os") > -1) return "text/plain";
      return "text/xml";
    }
    
    Matcher htmlMatcher = htmlPattern.matcher(contentValue);
//    Matcher bodyMatcher = bodyPattern.matcher(contentValue);
    if(htmlMatcher.find() /*|| bodyMatcher.find()*/) {
//      System.out.println(article.getMeta().getSource());
      append(contentValue);
      generateLinks(viewer, article);
      return "text/html";
    }
    
    Matcher xmlMatcher = xmlPattern.matcher(contentValue);
    if(xmlMatcher.find()  && xmlMatcher.start() < 5) {
      append(contentValue);
      return "text/xml";
    }

    try {
      String html = CommonResources.INSTANCE.getDetailTemplate();
      
      if(!site){
        html = html.replaceFirst("width=\"761\"", ""); 
        html = html.replaceFirst("class=\"tintop_line\"", "");
      }
      
      String application = CommonResources.INSTANCE.getApplicationTemplate();
      String breadcumbs = CommonResources.INSTANCE.getBreadcumbdTemplate();
      String bottom = CommonResources.INSTANCE.getBottomTemplate();
      
      String pattern = "$header";
      int idx = html.indexOf(pattern);
      int start = 0;
      if(idx > -1){
        append(html.substring(start, idx));
        new HeaderRenderer().write(output, article.getMeta().getTitle());    
        start = idx + pattern.length();
      }
      
      pattern = "$application";
      idx = html.indexOf(pattern);
      if(idx > -1){
        append(html.substring(start, idx));
        if(site) generateApplication(application);
        start = idx + pattern.length();
      }
      
      String [] items = null;
      
      pattern = "$menu";
      idx = html.indexOf(pattern);
      if(idx > -1){
        append(html.substring(start, idx));
        if(site){
          items = getDomainItems(referer, domain);
          if(items == null) items = new String[]{""};
          new MenuRenderer().write(output, viewer, referer, cookies, items);
        }
        start = idx + pattern.length();
      }      
      
      pattern = "$breadcumbs";
      idx = html.indexOf(pattern);
      if(idx > -1) {
        append(html.substring(start, idx));
        if(site) generateBreadcumbs(breadcumbs, items);
        start = idx + pattern.length();
      }   
      
      pattern = "$content";
      idx = html.indexOf(pattern);
      if(idx > -1) {
        append(html.substring(start, idx));
        Meta meta = article.getMeta();
        append("<h4>"); append(meta.getTitle()); append("</h4>"); 
        if(renderDesc) {
          append("<p>"); append(meta.getDesc()); append("</p>");
        }
        append(contentValue);
        start = idx + pattern.length();
      } 
      
      generateLinks(viewer, article);
      
      pattern = "$right";
      idx = html.indexOf(pattern);
      if(idx > -1) {
        append(html.substring(start, idx));
        if(site) new EventRenderer().write(output, viewer);
        start = idx + pattern.length();
      }
      
      pattern = "$copyright";
      idx = html.indexOf(pattern);
      if(idx > -1) {
        append(html.substring(start, idx));
        if(site) generateBottom(bottom);
        start = idx + pattern.length();
      }
        
      if(start < html.length()) append(html.substring(start));
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    return "text/html";
  }
  
  private void generateBottom(String html) throws Exception {
    String pattern = "$copyright";
    int idx = html.indexOf(pattern);
    int start = 0;
    if(idx > -1){
      append(html.substring(start, idx));
      append(CommonResources.INSTANCE.getSiteCopyrightLabel());
      start = idx + pattern.length();
    }
    if(start < html.length()) append(html.substring(start));
  }
  
  private void generateBreadcumbs(String html, String [] items) throws Exception {
    String pattern = "$breadcumbs";
    int idx = html.indexOf(pattern);
    int start = 0;
    if(idx > -1){
      append(html.substring(start, idx));
      boolean single = Application.isSingleData();
      for(int i = 0; i < items.length; i++) {
        if(i == 1 && single && items[i].indexOf('.') > -1) {
          items[i] = items[i].substring(items[i].indexOf('.') + 1);
        }
        append("/");append(items[i]);
      }
      start = idx + pattern.length();
    }
    if(start < html.length()) append(html.substring(start));   
  }
  
  private void generateApplication(String html) throws Exception {
    String pattern = "$application";
    int idx = html.indexOf(pattern);
    int start = 0;
    if(idx > -1){
      append(html.substring(start, idx));
      append(CommonResources.INSTANCE.getSiteAppLabel());
      start = idx + pattern.length();
    }
    if(start < html.length()) append(html.substring(start));   
  }
  
  private void generateLinks(String viewer, Article article) throws Exception {
    Meta meta  = article.getMeta();
    
    append("<table width=\"100%\" align=\"left\"  border=0><tr><td align=\"right\" valign=top>");
    
    boolean hasRelation = article.getMetaRelations() != null && article.getMetaRelations().size() > 0;
    
//    if(!site) append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
    boolean site = viewer.equals(CMSService.INSTANCE.getSiteViewer());
    
    
    append("&nbsp;<span class=\"updated_time_local\">(");
    if(!site) {
      try {
        URL url = new URL(article.getMeta().getSource());
        append(url.getHost());
      } catch (Exception e) {
      }
    }
    if(meta.getSourceTime() != null && meta.getSourceTime().trim().length() > 0) {
      if(!site) append(":&nbsp;");
      try {
        Date date = CalendarUtils.getDateTimeFormat().parse(meta.getSourceTime());
        append(CalendarUtils.getDateTimeFormat().format(date));
      } catch (Exception e) {
        try {
          Date date = CalendarUtils.getDateFormat().parse(meta.getSourceTime());
          append(CalendarUtils.getDateTimeFormat().format(date));
        }catch (Exception e2) {
          LogService.getInstance().setMessage(e, meta.getSourceTime());
        }
      }
    }
    append(")&nbsp;&nbsp;&nbsp;&nbsp;");
    
    if(params == null || params.length < 2) {
      if(EditContentHandler.getInstance().isEdit() && !site) {
        append("<a class=\"othernews_title\" href=\"/"+viewer+ "/EDIT/");append(meta.getId());
        append("\" style=\"text-decoration: none;\">[");
        append(CommonResources.INSTANCE.getEditContent());
        append("]</a> &nbsp;&nbsp;&nbsp;");
      }
    }
    
    append("<a class=\"othernews_title\" href=\"");append(meta.getSource());
    append("\" style=\"text-decoration: none;\">[");
    
    append(CommonResources.INSTANCE.getViewSourceLabel());
    append("]</a>&nbsp;&nbsp;&nbsp;");
    
    append("<a class=\"othernews_title\" href=\"JavaScript:history.go(");
    if(params != null && params.length > 1) {
      append(params[1]);  
    } else {
      append("-1");
    }
    append(") \">[Back]</a>");
    
    append("</td></tr>");
    
    if(hasRelation) {
      try{
        String relationTempl = CommonResources.INSTANCE.getRelationTemplate();
        append("<tr><td align=\"left\" valign=top>");
        LayoutRenderer layout  = new LayoutRenderer();      
        layout.createRelation(output, viewer, article.getMetaRelations(), relationTempl, null);
        append("</tr></td>");
      }catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
    append("</table>");
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
  
  private String[] getDomainItems(String referer, Domain domain) {
    String [] items = getDomainItems(referer); 
    if(items != null || domain == null) return items;
    items = new String[3];
    items[0] = domain.getDate();
    items[0] = items[0].replace('/', '.');
    items[1] = domain.getCategory();
    items[2] = domain.getName();
    return items;
  }

  private String[] getDomainItems(String referer) {
    if(referer == null || referer.trim().isEmpty()) return null;
    String separator = "/DOMAIN/";
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
  
}
