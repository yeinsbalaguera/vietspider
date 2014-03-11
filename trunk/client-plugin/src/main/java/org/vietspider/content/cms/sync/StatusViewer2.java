/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.cms.sync;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.model.plugin.SyncContent;
import org.vietspider.net.server.URLPath;
import org.vietspider.ui.services.ClientLog;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 24, 2009  
 */
class StatusViewer2 extends Thread  {

  private SyncContent sync;
  private boolean execute = true;
  private long start = System.currentTimeMillis();

  StatusViewer2(SyncContent sync) {
    this.sync = sync;
    this.execute = true;
    this.start();
  }

  public void abort() {
    ClientConnector2.currentInstance().abort();
    execute = false;
  }

  public void run() {
//    System.out.println(" chuan bi execute roi na` "+ sync.getId() + " : "+
//       sync.getStart()+ " : " +
//       ( System.currentTimeMillis() - sync.getStart()) + " : "+ sync.isTimeout());
    if(sync == null) {
      execute = false;
      return;
    }
    try {
      Header [] headers = new Header[] {
          new BasicHeader("action", "view.status"),
          new BasicHeader("plugin.name", sync.getPlugin())
      };
      ClientConnector2 connector = ClientConnector2.currentInstance();
      byte [] bytes = String.valueOf(sync.getId()).getBytes() ;
      bytes = connector.post(URLPath.DATA_PLUGIN_HANDLER, bytes, headers);
      String value = new String(bytes);
//      System.out.println(" da duoc status "+ value);
      if(value.equals("-2") && sync.isTimeout()) {
        sync.setStatus(SyncContent.ERROR);
      } else if(value.trim().length() < 1) {
        sync.setStatus(SyncContent.ERROR);
      } else {
        sync.setStatus(Integer.parseInt(value));
      }
    } catch (Exception e) {
      ClientLog.getInstance().setException(null, e);
    }
    execute = false;
  }
  
  public boolean isExecute() {
    if(System.currentTimeMillis() - start >= 30*1000) {
      abort();
      return false;
    }
    return execute; 
  }

  public SyncContent getSync() { return sync; }

}