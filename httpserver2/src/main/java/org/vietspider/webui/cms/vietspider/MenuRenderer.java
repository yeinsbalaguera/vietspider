package org.vietspider.webui.cms.vietspider;

import java.io.OutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.text.CalendarUtils;
import org.vietspider.db.idm2.EIDFolder2;
import org.vietspider.model.Track;
import org.vietspider.user.Filter;
import org.vietspider.users.UserFilterService;
import org.vietspider.webui.cms.BufferWriter;
import org.vietspider.webui.cms.CMSService;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Jan 11, 2007
 */
public class MenuRenderer extends BufferWriter { 
  
  public void write(OutputStream out, String viewer, String referer, String [] cookies, String ... items) throws Exception {
    output = out;
//    SourceLogUtils log = new SourceLogUtils();
    String [] dates = EIDFolder2.loadDates();//log.loadDate();
    String date = "";
    try {
      CalendarUtils.getParamFormat().parse(items[0]);
      date = items[0].replace('.', '/');
    } catch (Exception e) {
      items[0] = dates[0].replace('/', '.');
      date = dates[0];
    }
    
    StringBuilder href = new StringBuilder();
    href.append('/').append(viewer).append('/').append("DOMAIN").append("/1/");
    
    CommonResources resources = CommonResources.INSTANCE;
    
    try {
      String html = resources.getMenuTemplate();
      String username = CMSService.INSTANCE.getUsername(cookies);
      
      String pattern = "$date";
      int idx = html.indexOf(pattern);
      int start = 0;
      if(idx > -1) {
        append(html.substring(start, idx));append(resources.getDateLabel());
        start = idx + pattern.length();
      }

      pattern = "$combo_date";
      idx = html.indexOf(pattern);
      if(idx > -1){
        append(html.substring(start, idx));
        generateDate(dates, href, date);
        start = idx + pattern.length();
      }

      pattern = "$category";
      idx = html.indexOf(pattern);
      if(idx > -1){
        Track track = EIDFolder2.loadTrack(date);
//        MenuInfo domain = SourceLogHandler.getInstance().loadData(date);
//        AccessCheckerService.getInstance().getAccessChecker(username).computeMenu(domain, false);
        List<Track> categories = track.getTrackIds(Track.CATEGORY);
        
        append(html.substring(start, idx));
        generateMenu(href, categories, items);
        start = idx + pattern.length();
      }
      
      pattern = "$link_date";
      idx = html.indexOf(pattern);
      if(idx > -1){
        append(html.substring(start, idx));append(href.toString());append(items[0].toString());
        start = idx + pattern.length();
      }
      
      pattern = "$label_date";
      idx = html.indexOf(pattern);
      if(idx > -1){
        append(html.substring(start, idx));
        append(resources.getViewContentOnDateLabel());
        start = idx + pattern.length();
      }
      
      pattern = "$link_event";
      idx = html.indexOf(pattern);
      if(idx > -1){
        append(html.substring(start, idx));
        append("/");append(viewer);append("/");append("EVENT");append("/1/");append(items[0]);
        start = idx + pattern.length();
      }
      
      pattern = "$label_event";
      idx = html.indexOf(pattern);
      if(idx > -1){
        append(html.substring(start, idx));
        append(resources.getSectionEventLabel());
        start = idx + pattern.length();
      }
      
      pattern = "$filter";
      idx = html.indexOf(pattern);
      if(idx > -1){
        append(html.substring(start, idx));
        if(username != null) generateFilter(username, referer, viewer);
        start = idx + pattern.length();
      }
      
      pattern = "$login";
      idx = html.indexOf(pattern);
      if(idx > -1){
        append(html.substring(start, idx));
        if(username != null) {
          append("<li class=\"lv0\">");
          append("<span>");append(username);append("</span>");
          append("</li>");
          append("<li class=\"lv3\">");
          append("<a href=\"/site/LOGIN/\">");
          append(resources.getLogout()); append("</a>");
          append("</li>");
        } else {
          append("<li class=\"lv0\">");
          append("<a href=\"/site/FILE/Login.html\">");
          append(resources.getLogin()); append("</a>");
          append("</li>");
          append("<li class=\"lv3\"></li>");
        }
        start = idx + pattern.length();
      }
      
      if(start < html.length()) append(html.substring(start));
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }
  
  
  private void generateMenu(StringBuilder href, List<Track> categories, String [] items) throws Exception {
//    boolean single = Application.isSingleData();
    for(int i = 0; i < categories.size(); i++){
      Track category = categories.get(i);
//      if(category.getIds().size() < 1) continue;
      String encCategory =
        URLEncoder.encode(category.getParent().getName()  
            +  "." + category.getName(), "utf-8");
      if(i == 0) {
        append("<li class='lv0'>\n");
      } else if(i == categories.size() - 1) {
        append("<li class='lv3'>\n");
      } else {
        append("<li class='lv1'>\n");
      }
      append("  <a href=\"");append(href.toString());
      append(items[0]); append("/");append(encCategory);append("\">");
//      if(single) {
//        String label = category.getCategory();
//        append(label.substring(label.indexOf('.')+1));
//      } else {
      append(category.getName());
//      }
      append("  </a>\n");
      append("</li>\n");
      if(items.length > 1 && category.getName().equals(items[1])){
        generateSubMenu(href, encCategory, category.getChildren(), items);
      }
    }
  }
  
  private void generateSubMenu(
      StringBuilder href, String category, List<Track> sources, String [] items) throws Exception {
    if(items.length < 1) return;
    for(Track track : sources){    
//      if(track.getIds().size() < 1) continue;
      append("<li class='lv2'>\n");
      String encName = URLEncoder.encode(track.getName(), "utf-8");
      append("  <a href=\"");append(href.toString());append(items[0].toString());
              append("/");append(category);append("/");append(encName);append("\"");
      if(items.length > 2 && track.equals(items[2])){
        append(" class=\"SubMenuSelected\" >");
      } else {
        append("class=\"SubMenu\" >");
      }
      append(track.getName());append("</a>\n");
      append("</li>\n");
    }
  }
  
  private void generateDate(String [] dates, StringBuilder href, String date) throws Exception{    
    append("<select ONCHANGE=\"window.location = this.options[this.selectedIndex].value;\"");
    append("  id=\"boxmenu\" class=\"boxmenu\" name=\"select\">\n");
    for(String ele : dates){
      if(date.equals(ele)){
        append("<option selected value=\"");
      }else{
        append("<option value=\"");
      }
      append(href.toString());append(ele.replace('/', '.'));append("\">");append(ele);append("</option>\n");
    }
    append("\n</select>\n\n");
  } 
  
  private void generateFilter(String username, String referer, String viewer) throws Exception {
    CommonResources resources = CommonResources.INSTANCE;
    append("<ul class=\"leftmenu\" width=\"100%\">");
    
    String [] filters = UserFilterService.getInstance().listFilters(username);
    String filter = getFilter(referer);
    
    for(int i = 0; i < filters.length; i++) {
      String name = URLEncoder.encode(filters[i], "utf-8");
      if(i == 0) {
        append("<li class='lv0'>\n");
      } else {
        append("<li class='lv1'>\n");
      }
      append("<a href=\"/site/FILTER/?type=delete&name=");append(name);append("\">");
      append("<img src=\"/site/FILE/delete.gif\" border=\"0\" width=\"12\"> ");
      append("</a>");
      append("<a href=\"/");append(viewer);append("/FSEARCH/1/?text=");append(name);append("\">");
      boolean single = Application.isSingleData();
      if(single && filters[i].indexOf('.') > -1) {
        append(filters[i].substring(filters[i].indexOf('.')+1));
      } else {
        append(filters[i]);
      }
      append("</a>");
      append("</li>");
      if(filters[i].equals(filter)) generateSubFilter(viewer, username, filter);
    }
    
    if(filters.length > 0) {
      append("<li class=\"lv1\">");
    } else {
      append("<li class=\"lv0\">");
    }
    append("<a href=\"/site/FILE/Filter.html\">");
    append(resources.getCreateFilter());
    append("</a>");
    append("</li>");
    
    append("<li class=\"lv3\">");
    append("<a href=\"/site/PMENU/\">");
    append(resources.getCreatePMenu());
    append("</a>");
    append("</li>");
    append("</ul>");
  }
  
  private void generateSubFilter(String viewer, String username, String filterName) throws Exception {
    Filter filter = UserFilterService.getInstance().loadFilter(username, filterName);
    if(filter == null) return;
    if(filter.getType() == Filter.CONTENT || filter.getFilter() == null) return;
    String [] elements = filter.getFilter().split("\n");
    String encName = URLEncoder.encode(filterName, "utf-8");
    for(String element : elements){    
      append("<li class='lv2'>\n");
      String encSubName = URLEncoder.encode(element, "utf-8");
      
      append("<a href=\"/site/FILTER/?type=delete&name=");
      append(encName);append("&item=");append(encSubName);append("\">");
      append("<img src=\"/site/FILE/delete.gif\" border=\"0\" width=\"12\"> ");
      append("</a>");
      
      append("<a href=\"/");append(viewer);
        append("/FSEARCH/1/?text=");append(encName);append("&item=");append(encSubName);
      append("\">");
      int lastIndex = element.lastIndexOf('.');
      if(lastIndex > 0) {
        append(element.substring(lastIndex+1));
      } else {
        append(element);
      }
      append("</li>\n");
    }
  }

  private String getFilter(String referer) {
    if(referer == null || referer.trim().isEmpty())  return null;
    String separator = "/FSEARCH/";
    int index = referer.indexOf(separator);
    if(index < 0) return null;

    index = referer.indexOf('/', index + separator.length());
    if(index < 0) return null;
    separator = "text=";
    index = referer.indexOf(separator);
    if(index < 0) return null;
    try {
      int end = referer.indexOf('&', index);
      if(end < 0) end = referer.length();
      String value = referer.substring(index + separator.length(), end);
      return URLDecoder.decode(value, "utf-8");
    } catch (Exception e) {
    }
    return null;
  }
  
  
}
