/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.creator;

import java.util.prefs.Preferences;

import org.eclipse.swt.widgets.Composite;
import org.vietspider.common.util.Worker;
import org.vietspider.gui.config.RemoteClient;
import org.vietspider.gui.config.RemoteDialog;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.waiter.WaitLoading;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 16, 2007  
 */
public class SendSource extends RemoteDialog {

  private SendWorker sendWorker;

  public SendSource(Composite parent, SendWorker sendWorker) {
    super(parent);
    
    ApplicationFactory factory = new ApplicationFactory(this, "Connector", getClass().getName());
    createRemoteAddress(factory);
    createButton(factory, false);

    this.sendWorker = sendWorker;

    String username  = "vietspider";
    String password  = "vietspider";
    try {
      Preferences prefs = Preferences.userNodeForPackage(RemoteClient.class);
      username  = prefs.get("remote.username", "");
      password  = prefs.get("remote.password", "");
    } catch (Exception e) {
      username  = "vietspider";
      password  = "vietspider";
    }
    
    try {
      if(username != null) txtUsername.setText(username);
      if(password != null) txtPassword.setText(password);
    } catch (Exception e) {
      lblStatus.setText(e.toString());
    }

    loadHistory();

    if(cboHost.getText().trim().length() > 0) {
      butConnect.setFocus(); 
    } else {
      cboHost.setFocus();
    }
  }


  public void connect(final String host, final String port, final String username, final String password) {
    Worker excutor = new Worker() {

      private String message = ""; 


      public void abort() { sendWorker.abort(); }

      public void before() {
      }

      public void execute() {
        message = sendWorker.send(host, port, username, password);
      }

      public void after() {
        if(message != null && message.length() > 0) {
          lblStatus.setText(message);
          return; 
        }
        saveHistory();
        getShell().dispose();
      }
    };
    WaitLoading loading = new WaitLoading(butConnect, excutor);
    loading.open();
  }

  public void show() {
    loadHistory();
    getShell().setVisible(true);
  }

  public void close() {
    getShell().setVisible(false);
  }

}
