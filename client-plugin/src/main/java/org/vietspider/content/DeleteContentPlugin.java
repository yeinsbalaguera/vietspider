package org.vietspider.content;

import org.eclipse.swt.browser.Browser;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.client.common.PluginClientHandler;
import org.vietspider.common.util.Worker;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.services.ClientRM;
import org.vietspider.ui.widget.waiter.ThreadExecutor;

/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 13, 2008  
 */
public class DeleteContentPlugin extends AdminDataPlugin {
  
  private String label;
  private String confirm;
  
  public DeleteContentPlugin() {
    ClientRM resources = new ClientRM("Plugin");
    label = resources.getLabel(getClass().getName()+".itemDeleteContent");
    confirm = resources.getLabel(getClass().getName()+".msgAlertDeleteMeta");
  }
  
  public boolean isValidType(int type) { return type == CONTENT; }

  public String getConfirmMessage() { return confirm; }

  public String getLabel() { return label; }

  public void invoke(Object...objects) {
    if(!enable || values == null || values.length < 1) return;
    final Browser browser = (Browser) objects[1];
    
    delete(browser, values[0]);
  }
  
  static void delete(final Browser browser, final String value) {
    Worker excutor = new Worker() {

      private String error = null;

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
      }

      public void execute() {
        try {
          PluginClientHandler handler = new PluginClientHandler();
          handler.send("delete.data.plugin", "delete.article", value);
        } catch (Exception e) {
          error = e.toString();
        }
      }

      public void after() {
        if(error != null && !error.isEmpty()) { 
          ClientLog.getInstance().setMessage(browser.getShell(), new Exception(error));
          return;
        }        
        if(browser == null) return;
        browser.back();
        browser.refresh();
      }
    };
    new ThreadExecutor(excutor, browser).start();
  }
  
}
