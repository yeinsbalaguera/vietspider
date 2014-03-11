/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.webui.cms.vietspider;

import java.io.File;

import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.server.WebRM;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 6, 2007  
 */
public class CommonResources {
  
  private String detailTemplate;  
  private String applicationTemplate;
  private String breadcumbdTemplate;
  private String bottomTemplate;
  private String eventTemplate;
  private String headerTemplate;
  private String layoutTemplate;
  private String metaTemplate;
  private String imageTemplate;
  private String relationTemplate;
  private String menuTemplate;
  private String pageTemplate;
  
  private String siteAppLabel;
  private String viewSourceLabel;
  private String siteCopyrightLabel;
  private String dateLabel;
  private String viewContentOnDateLabel;
  private String sectionEventLabel;
  
  private String login;
  private String logout;
  
  private String readContent;
  private String editedContent;
  private String editContent;
  private String synchronizedContent;
  
  private String createFilter;
  private String createPMenu; 
  private String emptyData;
  
  final static CommonResources INSTANCE = new CommonResources();
  
  public CommonResources() {
    try {
      File file = UtilFile.getFile("system/cms/vietspider", "Detail.html");
      detailTemplate = new String(RWData.getInstance().load(file));
      
      file = UtilFile.getFile("system/cms/vietspider", "Application.html");
      applicationTemplate = new String(RWData.getInstance().load(file));
      
      file = UtilFile.getFile("system/cms/vietspider", "Breadcumbs.html");
      breadcumbdTemplate = new String(RWData.getInstance().load(file));
      
      file = UtilFile.getFile("system/cms/vietspider", "Bottom.html");
      bottomTemplate = new String(RWData.getInstance().load(file));
      
      file = UtilFile.getFile("system/cms/vietspider", "Event.html");
      eventTemplate = new String(RWData.getInstance().load(file));
      
      file = UtilFile.getFile("system/cms/vietspider", "Header.html");
      headerTemplate = new String(RWData.getInstance().load(file));
      
      file = UtilFile.getFile("system/cms/vietspider", "Layout.html");
      layoutTemplate = new String(RWData.getInstance().load(file));
      
      file = UtilFile.getFile("system/cms/vietspider", "Meta.html");
      metaTemplate = new String(RWData.getInstance().load(file));
      
      file = UtilFile.getFile("system/cms/vietspider", "Image.html");
      imageTemplate = new String(RWData.getInstance().load(file));
      
      file = UtilFile.getFile("system/cms/vietspider", "Relation.html");
      relationTemplate = new String(RWData.getInstance().load(file));
      
      file = UtilFile.getFile("system/cms/vietspider", "Menu.html");
      menuTemplate = new String(RWData.getInstance().load(file));
      
      file = UtilFile.getFile("system/cms/vietspider", "Page.html");
      pageTemplate = new String(RWData.getInstance().load(file));
      
      WebRM resources = new  WebRM();
      siteAppLabel = resources.getLabel("siteApplication");
      viewSourceLabel = resources.getLabel("viewSource");
      siteCopyrightLabel = resources.getLabel("siteCopyright");
      dateLabel = resources.getLabel("date");
      viewContentOnDateLabel = resources.getLabel("viewContentOnDate");
      sectionEventLabel = resources.getLabel("sectionEvent");
      
      login = resources.getLabel("login");
      logout = resources.getLabel("logout");
      
      createFilter = resources.getLabel("createFilter");
      createPMenu = resources.getLabel("createPMenu");
      
      readContent = resources.getLabel("readContent");
      editedContent = resources.getLabel("editedContent");
      editContent = resources.getLabel("editContent");
      synchronizedContent = resources.getLabel("synchronizedContent");
      
      
      emptyData = resources.getLabel("emptyData");
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }
  
  public String getDetailTemplate() { return detailTemplate;  }
  
  public String getApplicationTemplate() { return applicationTemplate; }

  public String getBreadcumbdTemplate() { return breadcumbdTemplate; }
  
  public String getBottomTemplate() { return bottomTemplate; }

  public String getEventTemplate() { return eventTemplate; }

  public String getHeaderTemplate() { return headerTemplate;  }

  public String getLayoutTemplate() { return layoutTemplate; }

  public String getMetaTemplate() { return metaTemplate; }

  public String getImageTemplate() { return imageTemplate; }

  public String getRelationTemplate() { return relationTemplate; }

  public String getMenuTemplate() { return menuTemplate; }

  public String getPageTemplate() { return pageTemplate; }

  public String getSiteAppLabel() { return siteAppLabel; }
  public String getViewSourceLabel() { return viewSourceLabel; }

  public String getSiteCopyrightLabel() { return siteCopyrightLabel; }

  public String getDateLabel() { return dateLabel; }
  public String getViewContentOnDateLabel() { return viewContentOnDateLabel; }
  public String getSectionEventLabel() { return sectionEventLabel; }

  public String getLogin() { return login; }
  public String getLogout() { return logout; }

  public String getCreateFilter() { return createFilter; }
  public String getCreatePMenu() { return createPMenu; }

  public String getEmptyData() { return emptyData; }

  public String getReadContent() { return readContent; }
  public String getEditedContent() { return editedContent; }
  public String getEditContent() { return editContent; }
  public String getSynchronizedContent() { return synchronizedContent; }
  
}
