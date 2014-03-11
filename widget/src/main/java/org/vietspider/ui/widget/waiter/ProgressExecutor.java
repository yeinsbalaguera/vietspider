/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.ui.widget.waiter;

import org.eclipse.swt.widgets.ProgressBar;
import org.vietspider.common.util.RatioWorker;
import org.vietspider.common.util.Worker;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 3, 2008  
 */
public class ProgressExecutor {
  
  private ProgressBar progressBar;

  private RatioWorker worker;

  public ProgressExecutor(ProgressBar progress, RatioWorker worker) {
    this.progressBar = progress;
    this.worker = worker;
  }

  public void open() {
    Thread thread = new Thread () {
      public void run () {
        if(progressBar == null || progressBar.isDisposed()) return;
        progressBar.getDisplay().syncExec(new Runnable () {
          public void run () {
            if(progressBar != null && !progressBar.isDisposed()) {
              progressBar.setVisible(true);
              progressBar.setMinimum(0);
            }
            
            worker.executeBefore();
            
            new Thread(new Runnable() {
              public void run () {
                if(worker!= null) worker.executeTask();
              }
            }).start();
            
            while(worker.isRunning()) {
              if(progressBar == null || progressBar.isDisposed()) return;
              progressBar.setSelection(worker.getRatio());
              progressBar.setMaximum(worker.getTotal());
//              System.out.println(worker.getRatio());
              if (!progressBar.getDisplay().readAndDispatch()) {
                progressBar.getDisplay().sleep();
              }
            }
            if(progressBar == null || progressBar.isDisposed()) return;
            worker.after();
            Worker [] plugins = worker.getPlugins();
            if(plugins != null) {
              for(int i = 0; i < plugins.length; i++) {
                new ThreadExecutor(plugins[i], progressBar).start();
              }
            }
            if(!progressBar.isDisposed()) progressBar.setVisible(false);
          }
        });
      }
    };
    thread.start ();
  }


}