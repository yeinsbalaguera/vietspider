/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.crawler;

import org.eclipse.swt.widgets.Control;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.common.util.Worker;
import org.vietspider.gui.browser.StatusBar;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.widget.waiter.ThreadExecutor;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 17, 2008  
 */
public abstract class BackgroupLoader extends Worker {

  protected  String error;
  protected Control widget;
  private Crawler crawler;

  public BackgroupLoader(Crawler crawler, Control control) {
    this.widget = control;
    this.crawler = crawler;
    
    new ThreadExecutor(this, control).start();
  }
  
  public Crawler getCrawler() { return crawler; }
  
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
    StatusBar status = crawler.getStatusBar();
    if(error != null) {
      if(status != null && !status.isDisposed()) {
        status.setError(error);
      } else {
        if(widget.isDisposed()) {
          ClientLog.getInstance().setMessage(null, new Exception(error));
        } else {
          ClientLog.getInstance().setMessage(widget.getShell(), new Exception(error));
        }
      }
      return;
    }
    
    if(status != null && !status.isDisposed()) status.setMessage("");
    finish();
  }
  
  public abstract void finish();
  
  public abstract void load() throws Exception ;

}
