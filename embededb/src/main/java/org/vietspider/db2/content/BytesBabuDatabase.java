/***************************************************************************
 * Copyright 2001-2012 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db2.content;

import java.io.File;

import org.vietspider.common.io.LogService;
import org.xtreemfs.babudb.BabuDBFactory;
import org.xtreemfs.babudb.api.BabuDB;
import org.xtreemfs.babudb.api.DatabaseManager;
import org.xtreemfs.babudb.api.database.Database;
import org.xtreemfs.babudb.config.BabuDBConfig;
import org.xtreemfs.babudb.log.DiskLogger.SyncMode;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 23, 2012  
 */
public class BytesBabuDatabase {
  
//  protected SortedMap<Long, byte[]> map;
  
  protected Database db;
  protected BabuDB databaseSystem = null;
  
  protected  volatile boolean isClose = false;

  public BytesBabuDatabase(File folder, String name) throws Exception {
    databaseSystem = BabuDBFactory.createBabuDB(
        new BabuDBConfig(folder.getAbsolutePath(), 
            folder.getAbsolutePath(), 2, 
            1024*1024, 5*60, SyncMode.FDATASYNC, 
            0, 0, false, 16, 50*1024*1024));
    DatabaseManager dbm = databaseSystem.getDatabaseManager();

    //create a new database called myDB
    File file = new File(folder, name);
    if(!file.exists()) dbm.createDatabase(name, 2);
    db = dbm.getDatabase(name);
  }
  
  public void close() {
    isClose = true;
    internalClose();
  }
  
  public boolean isClose() { return isClose; }
  
  private void internalClose()  {
    try {
      databaseSystem.getCheckpointer().checkpoint();
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    if (db != null) {
      try {
        db.shutdown();
      } catch (Throwable e) {
        LogService.getInstance().setMessage("BABU DATABASE" , null, e.toString());
      }
      db = null;
    }

    if (databaseSystem != null) {
      try {
        databaseSystem.shutdown();
      } catch (Throwable e) {
        LogService.getInstance().setMessage("BABU DATABASE" , null, e.toString());
      }
      databaseSystem = null;
    }
  }

}
