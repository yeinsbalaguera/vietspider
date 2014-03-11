package org.vietspider.content.wordpress;

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

public class WordPressSyncArticlesPlugin extends ContentSyncPlugin {

  private String label;
  //	private String confirm;
  private int type = CONTENT;

  private SinglePostSelector selector;
  private MultiPostSelector multiPostSelector;

  private static ClientRM clientRM;

  public WordPressSyncArticlesPlugin() {
    ClientRM resources = getResources();
    label = resources.getLabel(WordPressSyncArticlesPlugin.class.getName() + ".itemSendContent");
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
    new WordPressSetup(link.getShell());
  }

  static synchronized ClientRM getResources() {
    if(clientRM != null) return clientRM;

    final String _package = "org.vietspider.content.wordpress.";


    clientRM = new ClientRM(new ListResourceBundle() {
      protected Object[][] getContents() {

        String setupModule = _package + "WordPressSetup.";
        String singleModule = _package + "SinglePostSelector.";
        String multiModule = _package + "MultiPostSelector.";
        return new String[][] {
            
            {_package + "WordPressSyncArticlesPlugin.itemSendContent", "Đồng bộ tới WordPress"},
            
            {setupModule + "title", "Cấu hình WordPress Plugin"},

            {setupModule + "lblHomepage", "Trang chủ:"},
            {setupModule + "lblLogin", "Trang đăng nhập:"},
            {setupModule + "lblCharset", "Encoding:"},
            {setupModule + "lblUploadImage", "Gửi ảnh?"},
            {setupModule + "butUploadImage", ""},
            {setupModule + "lblUploadImageURL", "Trang có form gửi ảnh:"},
            {setupModule + "lblPostImageURL", "Trang gửi ảnh:"},
            {setupModule + "lblImagePosition", "Trình bày hình:"},
            {setupModule + "lblBoundary", "Boundary:"},
            {setupModule + "lblPostAddress", "Trang gửi tin:"},
            
            
            {setupModule + "lblImageStartURL", "Phần bắt đầu link ảnh:"},
            
            {setupModule + "lblMetaImageWidth", "Độ rộng ảnh:"},
            {setupModule + "lblTextStyle", "Text Style:"},

            {setupModule + "butPublished", "Đăng tin"},
            {setupModule + "butFrontpage", "Trang chủ"},
            {setupModule + "butAutoSync", "Tự động đồng bộ"},
            {setupModule + "butSourceLink", "Nguồn tin"},
            {setupModule + "butAlertWhenComplete", "Thông báo khi gửi tin thành công?"},

            {setupModule + "lblUsername", "Bí danh:"},
            {setupModule + "lblPassword", "Mật khẩu:"},
            
            {setupModule + "tableDataColumns","ID chuyên mục, Tên chuyên mục"},
            {setupModule + "tableDataColumnWidths","120,250"},

            {setupModule + "lblLinkToSource","Nguồn tin"},

            {setupModule + "msgErrorEmptyHomepage", "Không có địa chỉ trang chủ!"},
            {setupModule + "msgErrorEmptyLogin", "Không có trang đăng nhập!"},
            {setupModule + "msgErrorEmptyPostAddress", "Không có trang gửi tin!"},
            {setupModule + "msgErrorEmptyUploadImage", "Không có trang gửi ảnh!"},
            {setupModule + "msgErrorEmptyImagePath", "Hãy nhập đường dẫn ảnh!"},
            {setupModule + "msgErrorEmptyCharset", "Hãy nhập encoding!"},
            {setupModule + "msgErrorEmptyPassword", "Không có mật khẩu !"},
            {setupModule + "msgErrorEmptyUsername", "Không có bí danh!"},

            {setupModule + "butClear","Nhập lại"},
            {setupModule + "butClearTip",""},
            {setupModule + "butOk","Lưu"},
            {setupModule + "butOkTip",""},
            {setupModule + "butClose","Đóng"},
            {setupModule + "butCloseTip",""},

            {setupModule + "butCategoriesDetector","Tải chuyên mục từ trang"},
            {setupModule + "butCategoriesDetectorTip",""},
            
            {singleModule + "butConfig", "Cấu hình"},
            {singleModule + "butConfigTip", ""},

            {singleModule + "butFrontpage", "Trang chủ?"},
            {singleModule + "butFrontpageTip", ""},
            {singleModule + "butPublished", "Đăng tin?"},
            {singleModule + "butPublishedTip", ""},

            {singleModule + "lblLinkToSource", "Nguồn tin"},

            {singleModule + "butOk", "Ok"},
            {singleModule + "butOkTip", ""},
            {singleModule + "butClose", "Đóng"},
            {singleModule + "butCloseTip", ""},

            {multiModule + "butNextPage", "Trang tiếp"},
            {multiModule + "butNextPageTip", ""},
            {multiModule + "butOk", "Ok"},
            {multiModule + "butOkTip", ""},
            {multiModule + "butClose", "Đóng"},
            {multiModule + "butCloseTip", ""},


            {"published", "Đăng tin?"},
            {"frontpage", "Trang chủ?"}
            
        };
      }
    });

    return clientRM;
  }

}