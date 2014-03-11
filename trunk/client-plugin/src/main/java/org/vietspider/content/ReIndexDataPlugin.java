/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content;

import org.eclipse.swt.widgets.Composite;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.client.common.PluginClientHandler;
import org.vietspider.common.util.Worker;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.widget.waiter.ThreadExecutor;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 14, 2008  
 */
public class ReIndexDataPlugin extends AdminDataPlugin {

  private String label;
  private String confirm;

  public ReIndexDataPlugin() {
    label = "Reindex";
    confirm = "Do you want reindex data of {1}?";
  }

  @Override
  public boolean isValidType(int type) { return type == DOMAIN; }

  public String getConfirmMessage() {
    if(values == null || values.length < 1) return null;
    String elements [] = values[0].split("/");
    elements[0] = elements[0].replace('.', '/');
    return confirm.replaceAll("\\{1\\}", elements[0]); 
  }

  public String getLabel() { return label; }

  public void invoke(Object...objects) {
    if(!enable || values == null || values.length < 1) return;

    final Composite composite = (Composite) objects[0];
    final String elements [] = values[0].split("/");
    if(elements.length < 1) return;

    Worker excutor = new Worker() {

      private String error = null;

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
        elements[0] = elements[0].replace('.', '/');
      }

      public void execute() {
        try {
          PluginClientHandler handler = new PluginClientHandler();
          handler.send("reindex.data.plugin", "reindex.data", elements[0]);
        } catch (Exception e) {
          error = e.toString();
        }
      }

      public void after() {
        if(error != null && !error.isEmpty()) {
          ClientLog.getInstance().setMessage(composite.getShell(), new Exception(error));
          return;
        }
      }
    };
    new ThreadExecutor(excutor, composite).start();
  }

}