/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.export;

import org.eclipse.swt.widgets.Control;
import org.vietspider.content.AdminDataPlugin;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 14, 2008  
 */
public class ExcelExportDataPlugin  extends AdminDataPlugin {

  private String label;

  public ExcelExportDataPlugin() {
    label = "Export to Excel";
  }

  @Override
  public boolean isValidType(int type) { return type == DOMAIN; }

  public String getLabel() { return label; }

  public void invoke(Object...objects) {
    if(!enable || values == null || values.length < 1) return;

    final Control link = (Control) objects[0];
    new ExcelExportDataDialog(link.getShell(), values[0]);
  }
}