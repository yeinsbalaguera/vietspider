/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content;

import org.eclipse.swt.widgets.Control;
import org.vietspider.ui.services.ClientRM;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 14, 2008  
 */
public class ExportDataPlugin  extends AdminDataPlugin {

  private String label;

  public ExportDataPlugin() {
    ClientRM resources = new ClientRM("Plugin");
    label = resources.getLabel(getClass().getName()+".itemExportData");
  }

  @Override
  public boolean isValidType(int type) { return type == DOMAIN; }

  public String getLabel() { return label; }

  public void invoke(Object...objects) {
    if(!enable || values == null || values.length < 1) return;

    final Control link = (Control) objects[0];
    new ExportDataDialog(link.getShell(), values[0]);
  }
}