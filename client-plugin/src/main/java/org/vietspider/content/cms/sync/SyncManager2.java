/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.cms.sync;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.eclipse.swt.widgets.Shell;
import org.vietspider.model.plugin.SyncContent;
import org.vietspider.ui.widget.waiter.ThreadExecutor;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 24, 2009  
 */
public class SyncManager2 extends Thread {
  
  static SyncManager2 manager;
  
  final static public SyncManager2 getInstance(Shell shell) {
    if(manager == null) manager = new SyncManager2(shell);
    return manager;
  }
  
  private Queue<SyncContent> syncQueue = new LinkedBlockingQueue<SyncContent>();
  private Queue<SyncContent> messageQueue = new LinkedBlockingQueue<SyncContent>();
  private SyncContent sync;
  private StatusViewer2 statusViewer2;
  private Shell shell;
  
  private MessageDialog dialog;
  
  public SyncManager2(Shell shell) {
    this.shell = shell;
    dialog = new MessageDialog(shell);
    start();
  }
  
  public void run() {
    while(true) {
      if(sync != null) {
        if(sync.isTimeout()) {
          dialog.addMessage(sync.isShowMessage(), sync.getId()+ " is timeout!");
          messageQueue.add(sync);
          sync = null;
          if(statusViewer2.getSync() == sync) statusViewer2 = null;
          continue;
        } 
        
        if(sync.getStatus() == SyncContent.ERROR 
            || sync.getStatus() ==  SyncContent.SUCCESSFULL) {
          messageQueue.add(sync);
          sync = null;
          if(statusViewer2.getSync() == sync) statusViewer2 = null;
          continue;
        } 
//        System.out.println(" chuan bi execute de "+ sync + " : " + statusViewer2 + " : ");
        if(statusViewer2 == null || !statusViewer2.isExecute())  {
          statusViewer2 = new StatusViewer2(sync);
        }
      } else {
        if(!syncQueue.isEmpty()) {
          sync = syncQueue.poll();
          sync.setStart(System.currentTimeMillis());
          SyncWorker2 worker = new SyncWorker2(shell, sync);
          new ThreadExecutor(worker, shell).start();
        }
      }
      
      if(!messageQueue.isEmpty()) {
        MessageViewer2 viewer2 = new MessageViewer2(dialog, messageQueue.poll());
        new ThreadExecutor(viewer2, shell).start();
      }
      
      try {
        Thread.sleep(3*1000);
      } catch (Exception e) {
      } 
    }
  }
  
  public void sync(SyncContent value) { syncQueue.add(value); }
  
}
