/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db.log.user;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.vietspider.common.io.LogService;
import org.vietspider.common.io.UtilFile;
import org.vietspider.common.text.CalendarUtils;
import org.vietspider.db.VDatabases;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 5, 2009  
 */
public class UserLogDatabases extends VDatabases<UserLogDatabase> {

  private static UserLogDatabases INSTANCE;

  public static UserLogDatabases getService() {
    if(INSTANCE == null) {
      INSTANCE  = new UserLogDatabases();
    }
    return INSTANCE;    
  }

  protected volatile Queue<DataLog> waitData = new ConcurrentLinkedQueue<DataLog>();

  protected boolean execute = true;

  UserLogDatabases() {
    super(5*60*1000, 5*1000);
  }
  
  public synchronized void write(String articleId, String user, int action) {
    waitData.add(new DataLog(articleId, user, action));
  }

  @SuppressWarnings("unused")
  public void commit(boolean exit) {
    if(waitData == null) return;
    while(!waitData.isEmpty()) {
      DataLog dataLog = waitData.poll();
      UserLogDatabase db = getUserLogDb(dataLog.articleId, true);
      try {
        if(db != null) db.write(dataLog.articleId, dataLog.user, dataLog.action);
      } catch (Throwable e) {
        LogService.getInstance().setThrowable(e);
      }
    }
  }

  public synchronized UserLogDatabase getUserLogDb(String id, boolean create) {
    if(id.length() < 10) return null;
    id = id.substring(0, 8);

    UserLogDatabase db = null;
    String dateValue =  null;
    File folder = null;
    try {
      Date date = new SimpleDateFormat("yyyyMMdd").parse(id);
      dateValue = CalendarUtils.getFolderFormat().format(date);
      if(create) {
        folder = UtilFile.getFolder("track/user/"+dateValue+"/");
      } else {
        folder = new File(UtilFile.getFolder("track/user/"), dateValue+"/");
        if(!folder.exists()) return null;
      }

    } catch (Exception e) {
      LogService.getInstance().setThrowable("USER_LOG_TRACKER", e);
      return null;
    }

    String location = folder.getAbsolutePath();
    db = holder.get(location);
    if(db != null && !db.isClose()) return db;

    try {
      db = new UserLogDatabase(folder);
    } catch (Throwable e) {
      LogService.getInstance().setThrowable(e);
      return null;
    }
    holder.put(location, db);
    return db;
  }

  private class DataLog {

    private String articleId;
    private String user;
    private int action;

    private DataLog(String articleId, String user, int action) {
      this.articleId = articleId;
      this.user = user;
      this.action = action;
    }
  }
}
