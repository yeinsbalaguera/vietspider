package org.vietspider.content.vbulletin;

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

public class VBulletinSyncArticlesPlugin extends ContentSyncPlugin {

	private String label;
//	private String confirm;
	private int type = CONTENT;
	
	private SinglePostSelector selector;
	private MultiPostSelector multiPostSelector;

	public VBulletinSyncArticlesPlugin() {
		ClientRM resources = getResources();
		label = resources.getLabel(VBulletinSyncArticlesPlugin.class.getName() + ".itemSendContent");
//		confirm = resources.getLabel(getClass().getName() + ".msgAlertSend");
		enable = true;
	}

	public String getConfirmMessage() {
	  return null; 
	}

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
		if (!enable || values == null || values.length < 1)	return;
		
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
    new VBulletinSetup(link.getShell());
  }
  
  private static ClientRM clientRM;
  
  static synchronized ClientRM getResources() {
    if(clientRM != null) return clientRM;
    
    clientRM = new ClientRM(new ListResourceBundle() {
      protected Object[][] getContents() {
        String packageName  = "org.vietspider.content.vbulletin.";
        return new String[][] {
            {packageName + "VBulletinSyncArticlesPlugin.itemSendContent",  "Sync Content to VBulletin"},
            {packageName + "VBulletinSetup.title", "Sync Content to vBulletin"},
            {packageName + "VBulletinSetup.lblHomepage", "Home Page:"},
            {packageName + "VBulletinSetup.lblLogin", "Login Page:"},
            {packageName + "VBulletinSetup.lblCharset", "Charset:"},
            {packageName + "VBulletinSetup.lblPostAddress", "New Thread Page:"},
            {packageName + "VBulletinSetup.lblUploadImage", "Upload Image Page:"},
            {packageName + "VBulletinSetup.butAutoSync", "Auto Synchonized?"},
            {packageName + "VBulletinSetup.lblTextStyle", "Text Style:"},
            {packageName + "VBulletinSetup.lblLinkToSource", "Link to Source"},
            {packageName + "VBulletinSetup.butAlertWhenComplete", "Alert when complete?"},
            {packageName + "VBulletinSetup.lblUsername", "Username:"},
            {packageName + "VBulletinSetup.lblPassword", "Password:"},
            {packageName + "VBulletinSetup.butCategoriesDetectorTip", ""},
            {packageName + "VBulletinSetup.butCategoriesDetector", "Detect Categories"},
            {packageName + "VBulletinSetup.tableDataColumns", "Name,Id"},
            {packageName + "VBulletinSetup.tableDataColumnWidths", "200,70"},
            {packageName + "VBulletinSetup.butOk", "Save"},
            {packageName + "VBulletinSetup.butOkTip", ""},
            {packageName + "VBulletinSetup.butClose", "Close"},
            {packageName + "VBulletinSetup.butCloseTip", ""},
            {packageName + "VBulletinSetup.butClear", "Reset"},
            {packageName + "VBulletinSetup.butClearTip", ""},
            {packageName + "VBulletinSetup.msgErrorEmptyHomepage", "No homepage!!!"},
            {packageName + "VBulletinSetup.msgErrorEmptyCharset", "Please input charset!"},
            {packageName + "VBulletinSetup.msgErrorEmptyLogin", "No Login page!!!"},
            {packageName + "VBulletinSetup.msgErrorEmptyPostAddress", "Not found New Thread page!!!"},
            {packageName + "VBulletinSetup.msgErrorEmptyUsername", "Please input username!"},
            {packageName + "VBulletinSetup.msgErrorEmptyPassword", "Please input password!"},
            {packageName + "VBulletinSetup.", ""},
            
            {packageName + "SinglePostSelector.lblLinkToSource", "Link to Source"},
            {packageName + "SinglePostSelector.butConfig", "Config"},
            {packageName + "SinglePostSelector.butConfigTip", ""},
            {packageName + "SinglePostSelector.butOk", "Ok"},
            {packageName + "SinglePostSelector.butOkTip", ""},
            {packageName + "SinglePostSelector.butClose", "Close"},
            {packageName + "SinglePostSelector.butCloseTip", ""},
            {packageName + "SinglePostSelector.", ""},
            {packageName + "SinglePostSelector.", ""},
            {packageName + "SinglePostSelector.", ""},
            {packageName + "SinglePostSelector.", ""},
            {packageName + "SinglePostSelector.", ""},
            {packageName + "SinglePostSelector.", ""},
            {packageName + "SinglePostSelector.", ""},
            
            
        };
      }
    });
    return clientRM;
  }
	
}