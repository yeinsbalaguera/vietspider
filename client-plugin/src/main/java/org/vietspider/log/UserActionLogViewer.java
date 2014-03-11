/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.log;

import java.io.File;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.vietspider.client.ClientPlugin;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.common.util.Worker;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.services.ClientRM;
import org.vietspider.ui.widget.waiter.ThreadExecutor;

/**
 * Author : Nhu Dinh Thuan nhudinhthuan@yahoo.com Sep 8, 2008
 */
public class UserActionLogViewer extends ClientPlugin {

  private String label;

  public UserActionLogViewer() {
    try {
      ClientRM resources = new ClientRM("LogPlugin");
      label = resources.getLabel(getClass().getName() + ".viewUserAction");
    } catch (Exception e) {
      label = "User Action Viewer";
      ClientLog.getInstance().setException(null, e);
    }
  }

  @Override
  public boolean isValidType(int type) { return type == LOG; }

  public String getLabel() { return label; }

  @Override
  public void invoke(Object... objects) {
    final Control link = (Control) objects[0];
    final Composite composite = (Composite) objects[1];
    if(composite == null) return;

    Worker excutor = new Worker() {

      private String error = null;
      private File rfile;

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
      }

      public void execute() {
        try {
          File tfile = ClientConnector2.getCacheFolder("server/track/logs");
          rfile =  new UserLogFilter().filter(new File(tfile, "log.temp"));
        } catch (Exception e) {
          error = e.toString();
        }
      }

      public void after() {
        if (error != null && !error.isEmpty()) {
          ClientLog.getInstance().setMessage(link.getShell(), new Exception(error));
          return;
        }
        new UserActionViewer(composite, rfile);
      }
    };
    new ThreadExecutor(excutor, link).start();
  }
  
}