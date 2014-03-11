/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.creator.action;

import org.vietspider.client.common.ClientConnector2;
import org.vietspider.client.common.HomepagesClientHandler;
import org.vietspider.common.util.Worker;
import org.vietspider.gui.creator.homepage.URLHomepageDialog;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.widget.waiter.ThreadExecutor;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 23, 2008  
 */
public class LoadHomepage {
  
  private URLHomepageDialog dialog;
  
  public LoadHomepage(URLHomepageDialog dialog) {
    this.dialog = dialog;
    
    execute();
  }
  
  public void execute(){
    Worker excutor = new Worker() {

      private int page;
      private String value;
      
      private String group;
      private String category;
      private String name;
      
      
      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
        if(dialog.getShell().isDisposed()) return;
        page = dialog.getSpinnerPage().getSelection();
        
        this.group = dialog.getGroup();
        this.category = dialog.getCategory();
        this.name = dialog.getName();
      }

      public void execute() {
        HomepagesClientHandler client = new HomepagesClientHandler();
        try {
          if(page > 0) {
            value = client.loadHomepages(group, category, name, page);
          } else {
            value = "";
          }
        }catch (Exception e) {
          ClientLog.getInstance().setException(null, e);
        }
      }

      public void after() {
        if(dialog.getShell().isDisposed()) return;
        dialog.getTxtDialog().setText(value);
      }
    };
    new ThreadExecutor(excutor, dialog.getSpinnerPage()).start();
  }
  
  
}
