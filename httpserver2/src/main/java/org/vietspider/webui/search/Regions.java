/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.webui.search;

import java.io.File;
import java.util.HashMap;

import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.locale.vn.VietnameseConverter;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 29, 2011  
 */
public class Regions {
  
  private volatile static Regions regions;
  
  public static final Regions getInstance() {
    if(regions  != null) return regions;
    regions = new Regions();
    return regions;
  }
  
  private HashMap<String, String> map = new HashMap<String, String>();
  
  private String regionMenu;
  
  private Regions() {
    File file = UtilFile.getFile("system/cms/search", "regions.txt");
    try {
      String text = new String(RWData.getInstance().load(file), Application.CHARSET);
      String [] elements = text.split("\n");
      for(int i = 0; i < elements.length; i++) {
        String name = elements[i].trim();
        int idx = name.indexOf('/');
        if(idx > 0) name = name.substring(0, idx);
        map.put(VietnameseConverter.toAlias(name), name);
      }
      generate(elements.clone());
      
      regionMenu = generateMenu(elements.clone());
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }
  
  String getMenu() { return regionMenu; }
  
  private void generate(String [] elements) {
    File formFile = UtilFile.getFile("system/cms/search", "form.html");
//    if(formFile.exists() && formFile.length() > 10) return;
    StringBuilder builder = new StringBuilder();
    int i = 1;
    for(; i < elements.length; i++) {
      if(elements[i].startsWith("--")) break;
      String name = elements[i].trim();
      int idx = name.indexOf('/');
      String label = name;
      if(idx > 0) {
        label = name.substring(idx+1);
        name = name.substring(0, idx);
      }
      
      builder.append("<a href=\"javascript:setRegion('");
      builder.append(VietnameseConverter.toAlias(name));
      builder.append("', '").append(name).append("');\" class=\"menu\">");
      builder.append(label);
      builder.append("</a>&nbsp;&nbsp;\n");
    }
    
    boolean group = false;
    builder.append("<select onChange=\"selectRegion(this)\">");
    
    elements[i] = elements[i].substring(2);
    builder.append("<option value=\"#");
    builder.append("\" label=\"Toàn quốc\">");
    builder.append(elements[i]);
    builder.append("</option>\n");
    i++;
    
    for(; i < elements.length; i++) {
      if(elements[i].startsWith("--")) {
        if(group) builder.append("</OPTGROUP>");
        elements[i] = elements[i].substring(2);
        builder.append("<OPTGROUP label=\"").append(elements[i]).append("\">");
        group = true;
        continue;
      } 
      String name = elements[i].trim();
      int idx = name.indexOf('/');
      String label = name;
      if(idx > 0) {
        label = name.substring(idx+1);
        name = name.substring(0, idx);
      }
      builder.append("<option value=\"");
      builder.append(VietnameseConverter.toAlias(name));
      builder.append("\" label=\"").append(name);
      builder.append("\">");
      builder.append(label);
      builder.append("</option>\n");
    }
    if(group) builder.append("</OPTGROUP>");
    builder.append("</select>");
    
    
    try {
      File file = UtilFile.getFile("system/cms/search", "template.form.html");
      String html = new String(RWData.getInstance().load(file), Application.CHARSET);
      int index = html.indexOf("$region$");
      String region = builder.toString();
      builder.setLength(0);
      builder.append(html.substring(0, index));
      builder.append(region);
      builder.append(html.substring(index+"$region$".length()));
      RWData.getInstance().save(formFile, builder.toString().getBytes(Application.CHARSET));
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }
  
  private String generateMenu(String [] elements) {
    StringBuilder builder = new StringBuilder();
    builder.append("<div class=\"menu\"> <strong> <dfn>&nbsp;›&nbsp; </dfn>");
    builder.append("Tìm tại</strong> </div>");
    
    builder.append("<div class=\"menu\">&nbsp;&nbsp;&nbsp;<a href=\"javascript:clearRegion();\" class=\"menu\">");
    builder.append("Toàn quốc</a></div>\n");
    int i = 1;
    for(; i < elements.length; i++) {
      if(elements[i].startsWith("--")) break;
      String name = elements[i].trim();
      int idx = name.indexOf('/');
      String label = name;
      if(idx > 0) {
        label = name.substring(idx+1);
        name = name.substring(0, idx);
      }
      builder.append("<div class=\"menu\">&nbsp;&nbsp;&nbsp;<a href=\"javascript:setRegion('");
      builder.append(VietnameseConverter.toAlias(name));
      builder.append("', '").append(name).append("');\" class=\"menu\">");
      builder.append(label);
      builder.append("</a></div>\n");
    }
    
    boolean group = false;
    builder.append("<ul id=\"nav\">");
    i++;
    
    for(; i < elements.length; i++) {
      if(elements[i].startsWith("--")) {
        if(group) builder.append("</ul></li>");
        elements[i] = elements[i].substring(2);
        builder.append("<li>&nbsp;&nbsp;&nbsp;<u>").append(elements[i]).append("</u><ul>");
        group = true;
        continue;
      } 
      String name = elements[i].trim();
      int idx = name.indexOf('/');
      String label = name;
      if(idx > 0) {
        label = name.substring(idx+1);
        name = name.substring(0, idx);
      }
      builder.append("<li>&nbsp;&nbsp;&nbsp;");
      builder.append("<a href=\"javascript:setRegion('");
      builder.append(VietnameseConverter.toAlias(name));
      builder.append("', '").append(name).append("');\" class=\"menu\">");
      builder.append(label);
      builder.append("</a></li>\n");
    }
    
    if(group) builder.append("</ul></li>");
    builder.append("</ul>");
    return builder.toString();
  }
  
  public String getName(String key) {
    return map.get(key);
  }
  
  
 
}
