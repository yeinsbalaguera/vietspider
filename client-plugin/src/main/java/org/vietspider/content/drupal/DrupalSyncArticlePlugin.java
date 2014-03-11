package org.vietspider.content.drupal;

import java.util.ListResourceBundle;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Control;
import org.vietspider.content.cms.ContentSyncPlugin;
import org.vietspider.ui.services.ClientRM;

/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/

/**
 * Author : Nhu Dinh Thuan nhudinhthuan@yahoo.com Aug 13, 2008
 */

public class DrupalSyncArticlePlugin extends ContentSyncPlugin {

	private String label;
//	private String confirm;
	private int type = CONTENT;
	
	private SinglePostSelector selector;
	private MultiPostSelector multiPostSelector;
	
	public DrupalSyncArticlePlugin() {
		ClientRM resources = getResources();
		label = resources.getLabel(DrupalSyncArticlePlugin.class.getName() + ".itemSendContent");
//		confirm = resources.getLabel(getClass().getName() + ".msgAlertSend");
		enable = true;
	}

	public String getConfirmMessage() { return null; }

  public String getLabel() { return label; }

  @Override
  public boolean isValidType(int type_) { 
    this.type = type_;
    if(type == CONTENT) return true;
    return type == DOMAIN;  
  }

	@Override
	public void invoke(Object... objects) {
	  if(type == CONTENT) {
      invokeContent(objects);
    } else {
      invokeDomain(objects);
    }
	}
	
	public void invokeDomain(Object... objects) {
    if (!enable || values == null || values.length < 1) return;
    browser = (Browser) objects[1];
    searchArticles();
  }
  
	
  @Override
  public void setArticles(String[] ids, String[] titles, String[] cates) {
    if(multiPostSelector == null) {
      multiPostSelector = new MultiPostSelector(browser.getShell());
      multiPostSelector.setPlugin(this);
    }
    multiPostSelector.setData(ids, titles, cates);
  }
	
	private void invokeContent(Object... objects) {
    if (!enable || values == null || values.length < 1) return;
    final Control control = (Control) objects[0];
    if(selector == null || selector.isDestroy()) {
      if(selector != null) selector.dispose();
      selector = new SinglePostSelector(control.getShell());
      selector.setMetaId(values[0]);
    } else {
      selector.setMetaId(values[0]);
      selector.show();
    }
  }
	
	public boolean isSetup() { return true; }
	
	public void invokeSetup(Object...objects) {
    if(objects == null || objects.length < 1) return;
    
    final Control link = (Control) objects[0];
    new DrupalSetup(link.getShell());
  }
	
	private static ClientRM clientRM;
  
  static synchronized ClientRM getResources() {
    if(clientRM != null) return clientRM;

    final String _package = "org.vietspider.content.drupal.";


    clientRM = new ClientRM(new ListResourceBundle() {
      
      protected Object[][] getContents() {

        String setupModule = _package + "DrupalSetup.";
        String singleModule = _package + "SinglePostSelector.";
        String multiModule = _package + "MultiPostSelector.";
        return new String[][] {
            
            {_package + "DrupalSyncArticlePlugin.itemSendContent", "Post to Drupal"},
            
            {setupModule + "title", "Config the Drupal Plugin"},

            {setupModule + "lblHomepage", "Home Page:"},
            {setupModule + "lblLogin", "Login Page:"},
            {setupModule + "lblCharset", "Encoding:"},
            {setupModule + "lblUploadImage", "Post Image(s)?"},
//            {setupModule + "butUploadImage", ""},
//            {setupModule + "lblUploadImageURL", "Upload Image URL:"},
            {setupModule + "lblCateURL", "Category Manager Page:"},
//            {setupModule + "lblImageFolder", "Image Folder:"},
//            {setupModule + "lblImagePosition", "Image Position:"},
            {setupModule + "lblBoundary", "Boundary:"},
            {setupModule + "lblPostAddress", "Add New Article Page:"},
            
            {setupModule + "lblImageWidth", "Image Width:"},
            {setupModule + "lblTextStyle", "Text Style:"},

//            {setupModule + "butPublished", "Publish?"},
//            {setupModule + "butFeatured", "Featured?"},
            {setupModule + "butAutoSync", "Auto Synchronized the Content?"},
            {setupModule + "butSourceLink", "Source"},
            {setupModule + "butAlertWhenComplete", "Alert when VietSpider add new article successful?"},

            {setupModule + "lblUsername", "Username:"},
            {setupModule + "lblPassword", "Password:"},
            
            {setupModule + "tableDataColumns","Category ID, Category Name"},
            {setupModule + "tableDataColumnWidths","120,250"},

            {setupModule + "lblLinkToSource","Link to Source"},

            {setupModule + "msgErrorEmptyHomepage", "Please input the Homepage!"},
            {setupModule + "msgErrorEmptyLogin", "Please input the Login Page!"},
            {setupModule + "msgErrorEmptyPostAddress", "Please input the Post Address!"},
//            {setupModule + "msgErrorEmptyUploadImage", "Please input the Upload Image URL!"},
            {setupModule + "msgErrorEmptyImageFolder", "Please input the Image Folder!"},
            {setupModule + "msgErrorEmptyCharset", "Please input the Encoding!"},
            {setupModule + "msgErrorEmptyPassword", "Please input password !"},
            {setupModule + "msgErrorEmptyUsername", "Please input username!"},

            {setupModule + "butClear","Reset"},
            {setupModule + "butClearTip",""},
            {setupModule + "butOk","Save"},
            {setupModule + "butOkTip",""},
            {setupModule + "butClose","Close"},
            {setupModule + "butCloseTip",""},

            {setupModule + "butCategoriesDetector","Load Categories?"},
            {setupModule + "butCategoriesDetectorTip",""},
            
            {singleModule + "butConfig", "Configure"},
            {singleModule + "butConfigTip", ""},

            {singleModule + "butPublish", "Publish?"},
            {singleModule + "butPublishTip", ""},
            {singleModule + "butFeatured", "Featured?"},
            {singleModule + "butFeaturedTip", ""},

            {singleModule + "lblLinkToSource", "Link to Source"},

            {singleModule + "butOk", "Ok"},
            {singleModule + "butOkTip", ""},
            {singleModule + "butClose", "Close"},
            {singleModule + "butCloseTip", ""},

            {multiModule + "butNextPage", "Next Page"},
            {multiModule + "butNextPageTip", ""},
            {multiModule + "butOk", "Ok"},
            {multiModule + "butOkTip", ""},
            {multiModule + "butClose", "Close"},
            {multiModule + "butCloseTip", ""},

            {"published", "Publish?"},
            {"featured", "Featured?"}
            
        };
      }
    });

    return clientRM;
  }
}