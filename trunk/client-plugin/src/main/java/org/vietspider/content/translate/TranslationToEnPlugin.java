/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.translate;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Control;
import org.vietspider.client.ClientPlugin;
import org.vietspider.ui.services.ClientRM;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 19, 2011  
 */
public class TranslationToEnPlugin extends ClientPlugin {


  private String label;
  //private String confirm;
  private int type = CONTENT;

  public TranslationToEnPlugin() {
    ClientRM resources = TranslationPlugin.getResources();
    label = resources.getLabel(TranslationPlugin.class.getName() + ".itemTranslationToEn");
    //  confirm = resources.getLabel(getClass().getName() + ".msgAlertSend");
    enable = true;
  }

  public String getConfirmMessage() { return null; }

  public String getLabel() { return label; }

  @Override
  public boolean isValidType(int type_) {
    this.type = type_;
    if(type == CONTENT) return true;
    return false; 
  }

  @Override
  public void invoke(Object... objects) {
    if (!enable || values == null || values.length < 1) return;
    final Browser control = (Browser) objects[1];
    String url = control.getUrl();
    int idx = url.indexOf("/DETAIL/");
    if(idx < 0) return;
    url = url.substring(0, idx) + "/TRANSLATOR/" + values[0]+"/?to=en";
    control.setUrl(url);
  }

  public boolean isSetup() { return true; }

  public void invokeSetup(Object...objects) {
    if(objects == null || objects.length < 1) return;

    final Control link = (Control) objects[0];
    new BingTranslatorSetup(link.getShell());
  }

}
