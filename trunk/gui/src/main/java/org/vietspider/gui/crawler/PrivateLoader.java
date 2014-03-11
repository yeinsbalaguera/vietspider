/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.crawler;

import org.eclipse.swt.widgets.Control;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.common.util.Worker;
import org.vietspider.gui.browser.StatusBar;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.widget.waiter.WaitLoading;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 17, 2008  
 */
public abstract class PrivateLoader extends Worker {
  
  protected  String error;
  private StatusBar statusBar;
  private Control widget;
  
  public PrivateLoader(Control control, StatusBar statusBar) {
    this.widget = control;
    this.statusBar = statusBar;
    
    WaitLoading loading = new WaitLoading(control, this);
    loading.open();
  }
  
  public void abort() {
    ClientConnector2.currentInstance().abort();
  }

  public void before() {
  }

  public void execute() {
    try {
      load();
    }catch (Exception e) {
      error = e.getMessage();
      if(error == null || error.trim().isEmpty()) error = e.toString();
    }
  }

  public void after() {
    if(error != null) {
      if(statusBar != null) {
        statusBar.setError(error);
      } else {
        ClientLog.getInstance().setMessage(widget.getShell(), new Exception(error));
      }
      return;
    }
    
    if(statusBar != null) statusBar.setMessage("");
    finish();
  }
  
  public abstract void finish();
  
  public abstract void load() throws Exception ;
}
