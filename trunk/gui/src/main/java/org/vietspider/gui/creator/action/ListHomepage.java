/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.creator.action;

import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.client.common.HomepagesClientHandler;
import org.vietspider.common.util.Worker;
import org.vietspider.gui.creator.homepage.URLHomepageDialog;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.services.ClientRM;
import org.vietspider.ui.widget.waiter.ThreadExecutor;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 23, 2008  
 */
public class ListHomepage {
  
  private URLHomepageDialog dialog;
  
  public ListHomepage(URLHomepageDialog dialog) {
    this.dialog = dialog;
    execute();
  }
  
  public void execute(){
    Worker excutor = new Worker() {

      private int total = 1;
      
      private String group;
      private String category;
      private String name;
      
      
      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
        if(dialog.getShell().isDisposed()) return;
        this.group = dialog.getGroup();
        this.category = dialog.getCategory();
        this.name = dialog.getName();
      }

      public void execute() {
        HomepagesClientHandler client = new HomepagesClientHandler();
        try {
          total = client.loadTotalHomepages(group, category, name);
        }catch (Exception e) {
          ClientLog.getInstance().setException(null, e);
        }
      }

      public void after() {
        if(dialog.getShell().isDisposed()) return;
        Spinner spinner = dialog.getSpinnerPage();
        total = total < 1 ? 1 : total;
        spinner.setMaximum(total);
        Label lblPage  = dialog.getLabelPage();
        
        ClientRM resources = new ClientRM("Creator");
        String lblTotal = String.valueOf(total);
        lblPage.setText(resources.getLabel("homepage.page") +" ("+lblTotal+"):");
        spinner.setToolTipText(resources.getLabel("homepage.total.page") +": "+ lblTotal);
//        for(int i = 1;  i <= total; i++) {
//          label.setText(String.valueOf(total));
//        }
//        int idx = label.getItemCount() - 1;
//        if(idx < 0) return;
//        label.select(idx);
//        new LoadHomepage(dialog);
      }
    };
    new ThreadExecutor(excutor, dialog.getShell()).start();
    
  }
  
  
}
