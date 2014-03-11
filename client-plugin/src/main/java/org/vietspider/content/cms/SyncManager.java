/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.cms;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.eclipse.swt.widgets.Shell;
import org.vietspider.ui.widget.waiter.ThreadExecutor;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 24, 2009  
 */
public class SyncManager extends Thread {
  
  static SyncManager manager;
  
  final static public SyncManager getInstance(Shell shell) {
    if(manager == null) manager = new SyncManager(shell);
    return manager;
  }
  
  private ConcurrentHashMap<SyncData, Integer> waitQueue = new ConcurrentHashMap<SyncData, Integer>();
  private Queue<SyncData> syncQueue = new LinkedBlockingQueue<SyncData>();
  private Shell shell;
  
  public SyncManager(Shell shell) {
    this.shell = shell;
    start();
  }
  
  public void run() {
    while(true) {
      if(!syncQueue.isEmpty()) {
        SyncData value  = syncQueue.poll();
        waitQueue.put(value, 0);
        SyncWorker worker = new SyncWorker(shell, value);
        new ThreadExecutor(worker, shell).start();
      }
      
      Iterator<SyncData> iterator = waitQueue.keySet().iterator();
      while(iterator.hasNext()) {
        SyncData value  = iterator.next();;
        StatusViewer worker = new StatusViewer(shell, waitQueue, value);
        new ThreadExecutor(worker, shell).start();
      }
      
      try {
        Thread.sleep(5*1000);
      } catch (Exception e) {
      } 
    }
  }
  
  public void sync(SyncData value) {
    syncQueue.add(value);
  }
  
  
}
