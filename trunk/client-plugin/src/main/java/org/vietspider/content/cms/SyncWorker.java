/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.cms;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.common.Application;
import org.vietspider.common.util.Worker;
import org.vietspider.net.server.URLPath;
import org.vietspider.ui.services.ClientLog;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 12, 2008  
 */
class SyncWorker extends Worker  {

  private String error = null;
  private SyncData value;
  private Control control;
  private Shell shell;
  
//  SyncWorker(Control control, String values) {
//    this.control = control;
//    this.values = values;
//  }
  
  SyncWorker(Control control, SyncData value) {
    this.control = control;
    this.value= value;
  }

  public void abort() {
    ClientConnector2.currentInstance().abort();
  }

  public void before() {
  }

  public void execute() {
    if(value == null) return;
    try {
      Header [] headers = new Header[] {
          new BasicHeader("thread.executing", "true"),
          new BasicHeader("action", "send.article"),
          new BasicHeader("plugin.name", value.getPlugin())
      };
      ClientConnector2 connector = ClientConnector2.currentInstance();
      byte [] bytes = value.toXML().getBytes(Application.CHARSET) ;
      bytes = connector.post(URLPath.DATA_PLUGIN_HANDLER, bytes, headers);
      if(bytes == null || bytes.length < 1) {
        error = "";
        return ;
      }
      error = new String(bytes, Application.CHARSET);
//      PluginClientHandler handler = new PluginClientHandler();
//      error = handler.send("joomla.sync.article.plugin", "send.article", values.remove(0));
    } catch (Exception e) {
      error = e.toString();
    }
  }

  public void after() {
    if (error != null && !error.isEmpty()) {
      ClientLog.getInstance().setMessage(control.getShell(), new Exception(error));
    }
    if(shell != null) shell.dispose();
//      control.getShell().dispose();
  }

  void setShell(Shell shell) {
    this.shell = shell;
  }

}