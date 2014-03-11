/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.cms.sync;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.common.Application;
import org.vietspider.common.util.Worker;
import org.vietspider.model.plugin.SyncContent;
import org.vietspider.net.server.URLPath;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 24, 2009  
 */
class MessageViewer2 extends Worker  {

  private MessageDialog dialog;
  private SyncContent sync;
  private String message;
  private int time = 1;
  private int totalTime = 5;

  MessageViewer2(MessageDialog dialog, SyncContent sync) {
    this.dialog = dialog;
    this.sync = sync;
  }

  public void abort() {
    ClientConnector2.currentInstance().abort();
  }

  public void before() {
  }

  public void execute() {
    if(sync == null) return;
    while(time < totalTime) {
      try {
        Header [] headers = new Header[] {
            new BasicHeader("action", "view.message"),
            new BasicHeader("plugin.name", sync.getPlugin())
        };
        ClientConnector2 connector = ClientConnector2.currentInstance();
        byte [] bytes = String.valueOf(sync.getId()).getBytes() ;
        bytes = connector.post(URLPath.DATA_PLUGIN_HANDLER, bytes, headers);
        message = new String(bytes, Application.CHARSET);
        if(message != null && message.trim().length() > 0) break;
        time++;
        try {
          Thread.sleep(3*1000);
        } catch (Exception e) {
        }
      } catch (Exception e) {
        if(time >= totalTime)  continue;
        message = e.toString();
      }
    }
  }

  public void after() {
    if(message == null || message.trim().length() < 5) return;
    dialog.setMessage(sync.isShowMessage(), message);
  }

}