package org.vietspider.webui.cms.vietspider;

import java.io.OutputStream;
import java.util.Calendar;

import org.vietspider.common.Application;
import org.vietspider.common.Install;
import org.vietspider.common.io.LogService;
import org.vietspider.common.text.CalendarUtils;
import org.vietspider.db.database.MetaList;
import org.vietspider.webui.cms.BufferWriter;
import org.vietspider.webui.cms.CMSService;
import org.vietspider.webui.cms.render.MetaListRenderer;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Dec 25, 2006
 */
public class MetaListRendererImpl extends BufferWriter implements MetaListRenderer {
  
  public void write(OutputStream out, String viewer, MetaList data, String referer, String [] cookies, String...params) throws Exception {
    output = out;
    boolean site = viewer.equals(CMSService.INSTANCE.getSiteViewer());
    try {
      String html = CommonResources.INSTANCE.getPageTemplate();
      
      if(!site) html = html.replaceFirst("width=\"761\"", ""); 
      
      String application = CommonResources.INSTANCE.getApplicationTemplate();
      String breadcumbs = CommonResources.INSTANCE.getBreadcumbdTemplate();
      String bottom = CommonResources.INSTANCE.getBottomTemplate();
      
      String pattern = "$header";
      int idx = html.indexOf(pattern);
      int start = 0;
      if(idx > -1){
        append(html.substring(start, idx));
        new HeaderRenderer().write(output, data.getTitle());    
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
      if(idx > -1) {
        append(html.substring(start, idx));
        if(site) {     
          items = new  String[params.length >= 2 ? params.length-1 : 2];
          if(params.length < 2) {
            Calendar cal = Calendar.getInstance();    
            items[0] = CalendarUtils.getParamFormat().format(cal.getTime());
            items[1] = params[0];
          } else {
            System.arraycopy(params, 1, items, 0, items.length);
          }          
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
      
      pattern = "$layout";
      idx = html.indexOf(pattern);
      if(idx > -1) {
        append(html.substring(start, idx));
        new LayoutRenderer().write(output, viewer, data);
        start = idx + pattern.length();
      } 
      
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
        if(site) generateBottom( bottom);
        start = idx + pattern.length();
      }
      if(start < html.length()) append(html.substring(start));
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
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
      if(Application.LICENSE != Install.SEARCH_SYSTEM && Application.GROUPS.length == 1) {
        if(items.length > 1) items[1] = items[1].substring(items[1].indexOf('.')+1);
      }
         
      for(int i = 0; i < items.length; i++) {
        append("/");append(items[i]);
      }
      start = idx + pattern.length();
    }
    if(start < html.length()) append(html.substring(start));   
  }
  
  private void generateApplication(String html) throws Exception{
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
}
