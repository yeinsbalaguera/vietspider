/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.webui.cms.vietspider;

import java.io.File;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.vietspider.common.Application;
import org.vietspider.common.io.DataReader;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.common.text.NameConverter;
import org.vietspider.io.model.GroupIO;
import org.vietspider.model.Group;
import org.vietspider.model.Groups;
import org.vietspider.model.SourceFileFilter;
import org.vietspider.user.AccessChecker;
import org.vietspider.users.AccessCheckerService;
import org.vietspider.webui.cms.BufferWriter;
import org.vietspider.webui.cms.CMSService;
import org.vietspider.webui.cms.render.PMenuRenderer;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 18, 2008  
 */
public class PMenuRendererImpl extends BufferWriter implements PMenuRenderer {
  
  private  Groups groups = null; 
  
  public PMenuRendererImpl() {
    groups = GroupIO.getInstance().loadGroups();
  }

  @SuppressWarnings("unused")
  public void write(OutputStream output_, String viewer, String [] cookies, String [] params) throws Exception {
    this.output = output_;
    try {
      File file = UtilFile.getFile("system/cms/vietspider/", "PersonalMenu.html");
//    System.out.println(file.getAbsolutePath()+ " : "+ file.exists());
      String html = new String(RWData.getInstance().load(file), Application.CHARSET);
//    System.out.println(html);
      
      String username = CMSService.INSTANCE.getUsername(cookies);
      AccessChecker accessChecker = AccessCheckerService.getInstance().getAccessChecker(username);
      List<PCategory> categories = new ArrayList<PCategory>();
      
      String group = null;
      PCategory category = null;
      
      int start = 0;
      
      String pattern = "$groups";
      int idx = html.indexOf(pattern);
      if(idx > -1) {
        append(html.substring(start, idx));   
//        boolean single = Application.LICENSE != Install.SEARCH_SYSTEM && Application.GROUPS.length == 1;
//        if(single) {
//          group = Application.GROUPS[0];
//        } else {
        group = generateGroup(accessChecker, categories, params);
//        }
        start = idx + pattern.length();
      }
      
      pattern = "$categories";
      idx = html.indexOf(pattern);
      if(idx > -1) {
        append(html.substring(start, idx));
        category = generateCategory(group, categories, params);
        start = idx + pattern.length();
      }
      
      pattern = "$sources";
      idx = html.indexOf(pattern);
      if(idx > -1) {
        append(html.substring(start, idx));
        generateSources(group, category);
        start = idx + pattern.length();
      }

      if(start < html.length()) append(html.substring(start));
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }
  
  private String generateGroup(
      AccessChecker accessChecker, List<PCategory> categories, String [] params) throws Exception {    
    
    String selectedGroup = null;
    if(params.length > 0) selectedGroup = params[0];
    if(selectedGroup == null && groups.getGroups().length > 0) {
      selectedGroup =  groups.getGroups()[0].getType();
    }
    
    append("<SELECT name=\"group\" onchange=\"location = this.options[this.selectedIndex].value;\">");
    for(Group group : groups.getGroups()) {
      if(group.getType().equals("DUSTBIN")) continue;
      String type = group.getType();

      append("<OPTION label=\""); append(type); append("\" ");
      if(type.equals(selectedGroup)) {
        list(accessChecker, group.getType(), categories);
        append(" selected ");
      }
      append(" value=\"");append("/site/PMENU/?group="); append(URLEncoder.encode(type, "utf-8")); append("\">"); 
      append(type);
      append("</OPTION>");
      
    }
    append("</SELECT>");
    
    return selectedGroup;
  }
  
  private PCategory generateCategory (
      String group, List<PCategory> categories, String [] params) throws Exception {
    
    if(group == null) return null;
    
    String selectedCategory = null;
    if(params.length > 1) {
      selectedCategory = URLDecoder.decode(params[1], "utf-8");
    }
    if(selectedCategory == null && categories.size() > 0) {
      selectedCategory = categories.get(0).category;
    }
    
    PCategory selectedPCategory = null;
    
    String url = "/site/PMENU/?group="+URLEncoder.encode(group, "utf-8")+"&category=";
    
    append("<SELECT name=\"group\" onchange=\"location = this.options[this.selectedIndex].value;\">");
    for(int i = 0; i < categories.size(); i++) {
      String name = categories.get(i).category;
      if(selectedCategory == null) selectedCategory = name;
      
      append("<OPTION label=\""); append(name); append("\" ");
      if(name.equals(selectedCategory)) {
        append(" selected ");
        selectedPCategory = categories.get(i);
      }
          append(" value=\"");append(url); append(URLEncoder.encode(name, "utf-8")); append("\">"); 
        append(name);
      append("</OPTION>");
    }
    append("</SELECT>");
    append("<input type=\"hidden\" name=\"filterName\" value=\"");
    append(group); append("."); append(selectedCategory); append("\">");
    return selectedPCategory;
  }
  
  private void generateSources(String group, PCategory pcategory) throws Exception {
    if(group == null || pcategory == null) return;
    
    append("<SELECT multiple size=\"20\" name=\"pmenu\">");
    for(int k = 0; k < pcategory.sources.size(); k++) {
      String name = pcategory.sources.get(k);
      String value = group+"." +pcategory.category+"." + name;
      append("<OPTION label=\""); append(name); append("\""); 
      append(" value=\"");append(value); append("\">"); append(name);append("</OPTION>");
    }
    append("</SELECT>");
  }
  
  private void list(AccessChecker accessChecker, String group, List<PCategory> list) {
    if(!accessChecker.isPermitGroup(group)) return;
    
    SourceFileFilter filter = new SourceFileFilter(false);
    File groupFolder = UtilFile.getFolder("sources/sources/"+ group+"/");
    File  [] cateFolders = groupFolder.listFiles();
    
    for(File cateFolder : cateFolders) {
      if(cateFolder.isFile()) continue;
      
      File [] files = UtilFile.listFiles(cateFolder, filter);
      PCategory pcate = null;
      
      for(File file : files) {
        String name = NameConverter.decode(file.getName());
        if(!accessChecker.isPermitAccess(name, false)) continue;
        
        if(pcate == null) {
          pcate = new PCategory(NameConverter.decode(cateFolder.getName()));
          list.add(pcate);
        }
        pcate.sources.add(name.substring(name.indexOf('.') + 1));
      }
    }
    //end for
  }
  
  private class PCategory {
    
    private String category;
    private List<String> sources = new ArrayList<String>();
     
    private PCategory(String cate) { category = cate; }
  }
  

}
