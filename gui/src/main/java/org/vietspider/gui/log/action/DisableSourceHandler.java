/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.log.action;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.client.common.source.SourcesClientHandler;
import org.vietspider.common.util.Worker;
import org.vietspider.model.Source;
import org.vietspider.ui.widget.waiter.WaitLoading;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 7, 2010  
 */
public class DisableSourceHandler {
  public DisableSourceHandler(final Shell shell,
      final String group, final String category, final String name) {
    MessageBox msg = new MessageBox (shell, SWT.APPLICATION_MODAL | SWT.YES | SWT.NO);
    msg.setMessage("Are you sure diable this source?");
    if(msg.open() == SWT.NO) return;

    Worker excutor = new Worker() {

      private String message = null;

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {}

      public void execute() {
        try {
          SourcesClientHandler client = new SourcesClientHandler(group);
          Source source = client.loadSource(category, name);
          if(source == null) return;
          source.setPriority(-1);
          client.saveSource(source);
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
          MessageBox msg2 = new MessageBox (shell, SWT.APPLICATION_MODAL | SWT.OK);
          msg2.setMessage(message);
          msg2.open();
          return;
        }
      }
    };
    WaitLoading loading = new WaitLoading(shell, excutor);
    loading.open();
  }
}
