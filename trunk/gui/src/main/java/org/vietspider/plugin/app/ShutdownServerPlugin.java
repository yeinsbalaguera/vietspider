/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.plugin.app;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.MessageBox;
import org.vietspider.client.ClientPlugin;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.client.common.DataClientHandler;
import org.vietspider.common.util.Worker;
import org.vietspider.ui.services.ClientRM;
import org.vietspider.ui.widget.waiter.WaitLoading;
import org.vietspider.user.User;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 24, 2008  
 */
public class ShutdownServerPlugin extends ClientPlugin {

  protected String label;
  private String confirm;

  public ShutdownServerPlugin() {
    ClientRM resource = new ClientRM("Plugin");
    label = resource.getLabel(ShutdownServerPlugin.class.getName()+".shutdown.server");
    confirm = resource.getLabel("msgAlertShutdownServer");
  }

  public String getConfirmMessage() { return confirm; }

  public String getLabel() { return label; }
  
  public boolean isInvisible() { 
    return ClientConnector2.currentInstance().getPermission() != User.ROLE_ADMIN; 
  }

  @Override
  public boolean isValidType(int type) {
    return type == CONTENT || type == DOMAIN || type  == APPLICATION;
  }

  public void invoke(Object... objects) {
    if(ClientConnector2.currentInstance().getPermission() != User.ROLE_ADMIN ) return;
    
    final Control control = (Control) objects[0];
    
    Worker excutor = new Worker() {
      
      private String message = "";

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
      }

      public void execute() {
        try {
          DataClientHandler.getInstance().exitApplication();
        } catch (Exception e) {
          if(e.getMessage() != null && e.getMessage().trim().length() > 0) {
            message = e.getMessage();
          } else {
            message = e.toString();
          }
        }
      }

      public void after() {
        if(message != null && !message.trim().isEmpty()) {
          MessageBox msg = new MessageBox (control.getShell(), SWT.APPLICATION_MODAL | SWT.OK);
          msg.setMessage(message);
          msg.open();
        }
      }
    };
    WaitLoading loading = new WaitLoading(control.getShell(), excutor);
    loading.open();
  }

}
