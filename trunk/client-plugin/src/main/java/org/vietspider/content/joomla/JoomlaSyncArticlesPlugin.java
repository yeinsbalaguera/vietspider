package org.vietspider.content.joomla;

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

public class JoomlaSyncArticlesPlugin extends ContentSyncPlugin {

	private String label;
//	private String confirm;
	private int type = CONTENT;
	
	private SinglePostSelector selector;
	private MultiPostSelector multiPostSelector;

	public JoomlaSyncArticlesPlugin() {
	  ClientRM resources = getResources();
		label = resources.getLabel(JoomlaSyncArticlesPlugin.class.getName() + ".itemSendContent");
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
    new JoomlaSetup(link.getShell());
  }
  
  private static ClientRM clientRM;
  
  static synchronized ClientRM getResources() {
    if(clientRM != null) return clientRM;

    final String _package = "org.vietspider.content.joomla.";


    clientRM = new ClientRM(new ListResourceBundle() {
      
      protected Object[][] getContents() {

        String setupModule = _package + "JoomlaSetup.";
        String singleModule = _package + "SinglePostSelector.";
        String multiModule = _package + "MultiPostSelector.";
        return new String[][] {
            
            {_package + "JoomlaSyncArticlesPlugin.itemSendContent", "Gửi tin tới Joomla"},
            
            {setupModule + "title", "Cấu hình Joomla Plugin"},

            {setupModule + "lblHomepage", "Trang chủ:"},
            {setupModule + "lblLogin", "Trang Login:"},
            {setupModule + "lblCharset", "Encoding:"},
            {setupModule + "lblUploadImage", "Gửi ảnh?"},
//            {setupModule + "butUploadImage", ""},
//            {setupModule + "lblUploadImageURL", "Upload Image URL:"},
            {setupModule + "lblCateURL", "Trang quản lý chuyên mục:"},
            {setupModule + "lblImageFolder", "Thư mục ảnh:"},
            {setupModule + "lblUriImageFolder", "Đường dẫn thư mục ảnh:"},
            {setupModule + "lblImagePosition", "Image Position:"},
            {setupModule + "lblBoundary", "Boundary:"},
            {setupModule + "lblPostAddress", "Trang gửi bài:"},
            
            {setupModule + "lblImageWidth", "Độ rộng ảnh:"},
            {setupModule + "lblTextStyle", "Text Style:"},

            {setupModule + "butPublished", "Xuất bản?"},
            {setupModule + "butFeatured", "Featured?"},
            {setupModule + "butAutoSync", "Tự động gửi tin?"},
            {setupModule + "butSourceLink", "Nguồn"},
            {setupModule + "butAlertWhenComplete", "Thông báo khi gửi tin xong?"},

            {setupModule + "lblUsername", "Username:"},
            {setupModule + "lblPassword", "Password:"},
            
            {setupModule + "tableDataColumns","Category ID, Category Name"},
            {setupModule + "tableDataColumnWidths","120,250"},

            {setupModule + "lblLinkToSource","Link tới nguồn"},

            {setupModule + "msgErrorEmptyHomepage", "Hãy nhập trang chủ!"},
            {setupModule + "msgErrorEmptyLogin", "Hãy nhập trang Login!"},
            {setupModule + "msgErrorEmptyPostAddress", "Hãy nhập trang gửi tin!"},
//            {setupModule + "msgErrorEmptyUploadImage", "Please input the Upload Image URL!"},
            {setupModule + "msgErrorEmptyImageFolder", "Hãy nhập thư mục ảnh!"},
            {setupModule + "msgErrorEmptyCharset", "Hãy nhập Encoding!"},
            {setupModule + "msgErrorEmptyPassword", "Hãy nhập password !"},
            {setupModule + "msgErrorEmptyUsername", "Hãy nhập username!"},

            {setupModule + "butClear","Làm lại"},
            {setupModule + "butClearTip",""},
            {setupModule + "butOk","Lưu"},
            {setupModule + "butOkTip",""},
            {setupModule + "butClose","Đóng"},
            {setupModule + "butCloseTip",""},

            {setupModule + "butCategoriesDetector","Tải chuyên mục?"},
            {setupModule + "butCategoriesDetectorTip",""},
            
            {singleModule + "butConfig", "Cấu hình"},
            {singleModule + "butConfigTip", ""},

            {singleModule + "butPublish", "Xuất bản?"},
            {singleModule + "butPublishTip", ""},
            {singleModule + "butFeatured", "Trang chủ?"},
            {singleModule + "butFeaturedTip", ""},

            {singleModule + "lblLinkToSource", "Link tới nguồn"},

            {singleModule + "butOk", "Ok"},
            {singleModule + "butOkTip", ""},
            {singleModule + "butClose", "Đóng"},
            {singleModule + "butCloseTip", ""},

            {multiModule + "butNextPage", "Trang sau"},
            {multiModule + "butNextPageTip", ""},
            {multiModule + "butOk", "Ok"},
            {multiModule + "butOkTip", ""},
            {multiModule + "butClose", "Đóng"},
            {multiModule + "butCloseTip", ""},

            {"published", "Xuất bản?"},
            {"featured", "Trang chủ?"}
            
        };
      }
    });

    return clientRM;
  }
	
}