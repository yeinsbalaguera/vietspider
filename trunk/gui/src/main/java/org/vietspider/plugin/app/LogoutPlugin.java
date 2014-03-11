/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.plugin.app;

import org.vietspider.client.ClientPlugin;
import org.vietspider.gui.workspace.VietSpiderClient;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 23, 2008  
 */
public class LogoutPlugin extends ClientPlugin {

  protected String label;

  public LogoutPlugin() {
    label = "Logout";
  }

  public String getConfirmMessage() { return null; }

  public String getLabel() { return label; }

  @Override
  public boolean isValidType(int type) {
    return type == CONTENT || type == DOMAIN || type  == APPLICATION;
  }

  @SuppressWarnings("unused")
  public void invoke(Object... objects) {
    VietSpiderClient.connect(true);
  }

}
