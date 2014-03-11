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
public class ImportDataPlugin  extends AdminDataPlugin {

  private String label;

  public ImportDataPlugin() {
    ClientRM resources = new ClientRM("Plugin");
    label = resources.getLabel(getClass().getName()+".itemImportData");
  }

  @Override
  public boolean isValidType(int type) { return type == DOMAIN; }
  
  public String getLabel() { return label; }

  public void invoke(Object...objects) {
    if(!enable) return;

    final Control control = (Control) objects[0];
    new ImportDataDialog(control.getShell());
  }

}