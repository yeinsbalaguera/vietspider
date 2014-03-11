/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.filter;

import java.util.ListResourceBundle;

import org.eclipse.swt.widgets.Control;
import org.vietspider.content.AdminDataPlugin;
import org.vietspider.ui.services.ClientRM;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 14, 2008  
 */
public class FilterDataPlugin  extends AdminDataPlugin {

  private String label;

  public FilterDataPlugin() {
    label = "Bộ lọc tin";
  }

  @Override
  public boolean isValidType(int type) {  return true; }

  public String getLabel() { return label; }

  public void invoke(Object...objects) {
    if(!enable) return;

    final Control link = (Control) objects[0];
    new FilterDataDialog(link.getShell());
  }
  
  private static ClientRM clientRM;
  
  static synchronized ClientRM getResources() {
    if(clientRM != null) return clientRM;

    final String _package = "org.vietspider.content.filter.";


    clientRM = new ClientRM(new ListResourceBundle() {
      protected Object[][] getContents() {

        String dialog = _package + "FilterDataDialog.";
        String rename = _package + "RenameFilterDialog.";
        return new String[][] {
            {dialog + "butAddRegionNameImage", "add.png"},
            {dialog + "menuRenameFilter", "Đổi tên"},
            {dialog + "menuRemoveFilter", "Xóa"},
            {dialog + "butClear", "Nhập mới"},
            {dialog + "butClearTip", "Nhập mới từ khóa cho bọ lọc đang chọn"},
            {dialog + "butSave", "Lưu"},
            {dialog + "butSaveTip", "Lưu từ khóa vào bộ lọc đang chọn"},
            
            {rename + "name", "Tên mới:"}
//            {dialog + "", "Trang gửi ảnh:"},
//            {dialog + "lblImagePosition", "Trình bày hình:"},
//            {dialog + "lblBoundary", "Boundary:"},
//            {dialog + "lblPostAddress", "Trang gửi tin:"}
            
        };
      }
    });

    return clientRM;
  }
}