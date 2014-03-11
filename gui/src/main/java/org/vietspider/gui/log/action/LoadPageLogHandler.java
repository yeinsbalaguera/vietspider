/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.log.action;

import java.io.File;

import org.eclipse.swt.widgets.Spinner;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.client.common.ZipRatioWorker;
import org.vietspider.common.io.UtilFile;
import org.vietspider.gui.log.ITextViewer;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.services.ClientRM;
import org.vietspider.ui.widget.waiter.WaitLoading;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 17, 2009  
 */
public class LoadPageLogHandler {
  
  private ITextViewer viewer;
  
  public LoadPageLogHandler(ITextViewer viewer) {
    this.viewer = viewer;
  }
  
  public void load(final boolean update) {
    String folder = viewer.getFolder();
    String logName = viewer.getFileName();
    
    load(folder, logName, update);
  }
  
  public void load(final String folder, final String logName, final boolean update) {
    if(logName == null) return;
    ClientRM resources = new ClientRM("Monitor");
    final String labelPage = " "+ resources.getLabel("page");
    
    final File file  = ClientConnector2.getCacheFolder("server/" + folder);
    if(update) {
      UtilFile.deleteFolder(file);
    } else {
      File [] files = file.listFiles();
      for(int i = 0; i < files.length; i++) {
        if(files[i].getName().endsWith(".temp")) continue;
        files[i].delete();
      }
    }
    
//    System.out.println("============ >"+ logName);
    final LoadLogHandler loadHandler = new LoadLogHandler(folder, logName, viewer.isHTML());

    ZipRatioWorker worker = new ZipRatioWorker() {

      private String pattern = "";
      private boolean finished = false;

      @Override
      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      @Override
      public void before() {
        pattern = viewer.getCboFilter().getText().trim();
        if(!pattern.equals("USER")) return;
        
        viewer.getListDate().setEnabled(false);
        viewer.getButGo().setEnabled(false);
        
        final Spinner spinPage = viewer.getSpinPage();
        spinPage.setMaximum(0);
        spinPage.setMinimum(0);
        spinPage.setSelection(0);

        final int time = 1000;
        Runnable timer = new Runnable () {
          public void run () {
            if(finished || spinPage.isDisposed()) return;
            viewer.setPages(file, labelPage);
            viewer.getShell().getDisplay().timerExec(time, this);
          }
        };
        viewer.getShell().getDisplay().timerExec(time, timer);
      }

      @Override
      public void execute() {
        try {
          loadHandler.execute(this, pattern, update);
        } catch (Exception e) {
          ClientLog.getInstance().setException(null, e);
        }
      }

      @Override
      public void after() {
        if(viewer.getButGo().isDisposed()) return;
        viewer.getButGo().setEnabled(true);
        viewer.getListDate().setEnabled(true);
        finished = true;
        viewer.setPages(file, labelPage);
        Spinner spinPage = viewer.getSpinPage();
        if(spinPage.getSelection() > 1) return;
        spinPage.setSelection(1);
        new ViewLogHandler(viewer, spinPage.getSelection());
      }
    };

    new  WaitLoading(viewer.getShell(), worker).open();
//    statusBar.showProgressBar();
//    ThreadExecutor executor = 
//    ProgressExecutor loading = new ProgressExecutor(statusBar.getProgressBar(), worker);
//    loading.open();
  }
  
  /*final File file  = ClientConnector2.getCacheFolder("logs/server");
  if(update) {
    UtilFile.deleteFolder(file);
  } else {
    File [] files = file.listFiles();
    for(int i = 0; i < files.length; i++) {
      if(files[i].getName().endsWith(".temp")) continue;
      files[i].delete();
    }
  }

  final String date = viewer.getSelectedDate();
  if(date  == null || date.trim().length() < 1) return ;*/
}
