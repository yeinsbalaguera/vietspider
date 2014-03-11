/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.search;

import org.eclipse.swt.widgets.Control;
import org.vietspider.client.ClientPlugin;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 10, 2010  
 */
public class SEOMobilePlugin extends ClientPlugin {
  
  private String label;

  public SEOMobilePlugin() {
    label = "SEO Mobile Plugin";
    enable = true;
  }

  public String getConfirmMessage() {
    return null; 
  }

  public String getLabel() { return label; }

  @Override
  @SuppressWarnings("unused")
  public boolean isValidType(int type_) {
    return false; 
  }

  @Override
  @SuppressWarnings("unused")
  public void invoke(Object... objects) {

  }

  public boolean isSetup() { return true; }

  public void invokeSetup(Object...objects) {
    if(objects == null || objects.length < 1) return;

    final Control link = (Control) objects[0];
    new SEOMobileSetup(link.getShell());
  }
}
