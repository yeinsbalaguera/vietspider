/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db.content;

import org.vietspider.db.SystemProperties;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 18, 2011  
 */
public class BackupDatabase {
  
  private static BackupDatabase instance;
  
  public synchronized static final BackupDatabase getInstance() {
    if(instance != null) return instance;
    instance = new BackupDatabase();
    return instance;
  }
  
  private ArticleDatabases backup;

  private BackupDatabase() {
    SystemProperties properties = SystemProperties.getInstance();
    if("true".equals(properties.getValue("database.backup"))) {
      backup = new ArticleDatabases(true);
    }
  }
  
  public boolean isBackup() { return backup != null; }

  public ArticleDatabases getDatabase() { return backup; }
  
}
