/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.ui.widget.waiter;

import org.eclipse.swt.widgets.Control;
import org.vietspider.common.util.Worker;
import org.vietspider.ui.services.ClientLog;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 10, 2007  
 */
public class ThreadExecutor extends Thread {

  private Control control;
  private Worker worker;

  public ThreadExecutor(Worker excutor, Control control) {
    if(control == null)  throw new RuntimeException(" Control is null");
    this.control = control;
    this.worker = excutor;
  }

  public void run () {
    if(control == null || control.isDisposed()) return;
    control.getDisplay().syncExec(new Runnable () {
      public void run () {
        worker.executeBefore();

        new Thread(new Runnable() {
          public void run () {
            worker.executeTask();
          }
        }).start();

        while(worker.isRunning()) {
          if(control.isDisposed()) return;
          if(!control.getDisplay().readAndDispatch()) control.getDisplay().sleep();
        }

        try {
          worker.after();
        } catch (Exception e) {
          ClientLog.getInstance().setException(null, e);
        }
        Worker [] plugins = worker.getPlugins();
        if(plugins != null) {
          for(int i = 0; i < plugins.length; i++) {
            new ThreadExecutor(plugins[i], control).start();
          }
        }
      }
    });
  }

}
