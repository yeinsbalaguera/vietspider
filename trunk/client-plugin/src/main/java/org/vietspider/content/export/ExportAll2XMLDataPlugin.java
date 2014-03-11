/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.export;

import java.util.ListResourceBundle;

import org.eclipse.swt.widgets.Control;
import org.vietspider.content.AdminDataPlugin;
import org.vietspider.ui.services.ClientRM;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 14, 2008  
 */
public class ExportAll2XMLDataPlugin extends AdminDataPlugin {

  private String label;
  
  private static ClientRM clientRM;

  public ExportAll2XMLDataPlugin() {
    ClientRM resources = getResources();
    label = resources.getLabel(getClass().getName()+".itemExportData");
  }

  @Override
  public boolean isValidType(int type) { return type == DOMAIN; }

  public String getLabel() { return label; }

  public void invoke(Object...objects) {
    if(!enable || values == null || values.length < 1) return;

    final Control link = (Control) objects[0];
    new ExportAll2XMLDataDialog(link.getShell(), values[0]);
  }
  
  static synchronized ClientRM getResources() {
    if(clientRM != null) return clientRM;

    final String _package = "org.vietspider.content.export.";

    clientRM = new ClientRM(new ListResourceBundle() {
      protected Object[][] getContents() {
        
        String dialogName = _package + "ExportAll2XMLDataDialog.";

        return new String[][] {
            
            {_package + "ExportAll2XMLDataPlugin.itemExportData", "Export to XML file"},
            
            {dialogName + "title", "Export Data to XML"},
            {dialogName + "butExportFile", "Save as..."},
            {dialogName + "butCancelExportFile", "Cancel"},
//            {dialogName + "butExportFile", "Export Data to XML"},
//            {dialogName + "butExportFile", "Export Data to XML"},
//            {dialogName + "butExportFile", "Export Data to XML"},
//            {"frontpage", "Trang chủ?"}
            
        };
      }
    });

    return clientRM;
  }
}