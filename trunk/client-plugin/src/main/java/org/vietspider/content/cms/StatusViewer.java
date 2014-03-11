/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.cms;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.eclipse.swt.widgets.Control;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.common.Application;
import org.vietspider.common.util.Worker;
import org.vietspider.net.server.URLPath;
import org.vietspider.ui.services.ClientLog;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 24, 2009  
 */
class StatusViewer extends Worker  {

  private String error = null;
  private SyncData value;
  private Control control;
  private ConcurrentHashMap<SyncData, Integer> queue;

  StatusViewer(Control control, ConcurrentHashMap<SyncData, Integer> queue, SyncData syncData) {
    this.control = control;
    this.value= syncData;
    this.queue = queue;
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
          new BasicHeader("action", "view.status"),
          new BasicHeader("plugin.name", value.getPlugin())
      };
      ClientConnector2 connector = ClientConnector2.currentInstance();
      byte [] bytes = value.toXML().getBytes(Application.CHARSET) ;
      bytes = connector.post(URLPath.DATA_PLUGIN_HANDLER, bytes, headers);
      error = new String(bytes, Application.CHARSET);
    } catch (Exception e) {
      e.printStackTrace();
      queue.remove(value);
      error = e.toString();
    }
  }
  
  public void after() {
    if("0".equals(error) || error.trim().isEmpty()) {
      Integer time = queue.get(value);
      if(time != null && time < 20) {
        queue.put(value, time+1);
        return;
      }
    } else {
      if(value.isAlertMessage()) {
        ClientLog.getInstance().setMessage(control.getShell(), new Exception(error));
      }
    }
    queue.remove(value);  
  }

}