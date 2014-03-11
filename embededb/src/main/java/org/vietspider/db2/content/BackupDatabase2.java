/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db2.content;

import org.vietspider.db.SystemProperties;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 18, 2011  
 */
public class BackupDatabase2 {
  
  private static BackupDatabase2 instance;
  
  public synchronized static final BackupDatabase2 getInstance() {
    if(instance != null) return instance;
    instance = new BackupDatabase2();
    return instance;
  }
  
  private MigratedDatabases backup;

  private BackupDatabase2() {
    SystemProperties properties = SystemProperties.getInstance();
    if("true".equals(properties.getValue("database.backup"))) {
      backup = new MigratedDatabases();
    }
  }
  
  public boolean isBackup() { return backup != null; }

  public MigratedDatabases getDatabase() { return backup; }
  
}
