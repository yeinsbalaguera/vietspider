/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content;

import org.eclipse.swt.widgets.Control;
import org.vietspider.client.ClientPlugin;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.common.util.Worker;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.widget.waiter.ThreadExecutor;
import org.vietspider.user.User;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 14, 2008  
 */
public abstract class AdminDataPlugin  extends ClientPlugin {
  
  public void syncEnable(Object... objects) {

    final Control link = (Control) objects[0];

    Worker excutor = new Worker() {

      private String error = null;

      public void abort() {}

      public void before() { }

      public void execute() {
        try {
          enable = ClientConnector2.currentInstance().getPermission() == User.ROLE_ADMIN;
        }catch (Exception e) {
          error = e.toString();
        }
      }

      public void after() {
        if(error != null && !error.isEmpty()) {
          ClientLog.getInstance().setMessage(link.getShell(), new Exception(error));
          return;
        }
        if(!enable) link.dispose();
      }
    };
    new ThreadExecutor(excutor, link).start();
  }
}
