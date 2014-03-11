/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db.source.monitor;

import java.io.File;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.vietspider.common.io.LogService;
import org.vietspider.common.io.UtilFile;
import org.vietspider.db.VDatabases;

//import com.sleepycat.je.recovery.RecoveryException;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 12, 2008  
 */
final class SourceMonitorDatabases extends VDatabases<SourceMonitorDatabase> {
  
  protected volatile java.util.Queue<SourceLog> waitData = new ConcurrentLinkedQueue<SourceLog>();
  
  public SourceMonitorDatabases() {
    super(5*60*1000L, 15*1000);
  }
  
  @SuppressWarnings("unused")
  public void commit(boolean exit) {
    if(waitData == null) return;
    
    while(!waitData.isEmpty()) {
      SourceLog dataLog = waitData.poll();

      SourceMonitorDatabase database = getDatabase(dataLog.getDate());
      if(database == null) continue;

      try {
        SourceLog savedLog = database.search(dataLog.getName());
        if(savedLog == null) {
          savedLog = dataLog;
        } else {
          savedLog.update(dataLog);
        }
        database.save(savedLog);
      } catch (Throwable e) {
        LogService.getInstance().setThrowable("SOURCE MONITOR", e);
        if(waitData.size() > 10000) waitData.clear();
        break;        
      }
    }
  }

  
  public synchronized SourceMonitorDatabase getDatabase(String date) {
    SourceMonitorDatabase tracker = holder.get(date);
    if(tracker != null && !tracker.isClose()) return tracker;
    try {
      File folder = UtilFile.getFolder("content/summary/"+date+"_msdb/");
      tracker = new SourceMonitorDatabase(folder, date, 3*1024*1024);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(date, e);
    }
    if(tracker != null) holder.put(date, tracker);
    return tracker;
  }

  java.util.Queue<SourceLog> getWaitData() {
    return waitData;
  }

}
