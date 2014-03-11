/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.drupal;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 24, 2009  
 */
class SyncManagerBak extends Thread {
  
  /*static SyncManagerBak manager;
  
  final static SyncManagerBak getInstance(Shell shell) {
    if(manager == null) manager = new SyncManagerBak(shell);
    return manager;
  }
  
  private ConcurrentHashMap<String, Integer> waitQueue = new ConcurrentHashMap<String, Integer>();
  private Queue<String> syncQueue = new LinkedBlockingQueue<String>();
  private Shell shell;
  
  public SyncManagerBak(Shell shell) {
    this.shell = shell;
    start();
  }
  
  public void run() {
    while(true) {
      if(!syncQueue.isEmpty()) {
        String value  = syncQueue.poll();
        waitQueue.put(value, 0);
        SyncWorker worker = new SyncWorker(shell, value);
        new ThreadExecutor(worker, shell).start();
      }
      Iterator<String> iterator = waitQueue.keySet().iterator();
      while(iterator.hasNext()) {
        String value  = iterator.next();
        StatusViewer worker = new StatusViewer(shell, waitQueue, value);
        new ThreadExecutor(worker, shell).start();
      }
      
      try {
        Thread.sleep(5*1000);
      } catch (Exception e) {
      } 
    }
  }
  
  public void sync(String value) {
    syncQueue.add(value);
  }*/
  
  
}
