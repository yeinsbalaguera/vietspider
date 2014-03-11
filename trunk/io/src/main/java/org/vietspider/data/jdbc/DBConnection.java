/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.data.jdbc;

import java.sql.Connection;

import org.vietspider.common.io.LogService;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 4, 2008  
 */
public class DBConnection {
  
  private volatile Connection connection;
  private volatile long start = -1;
  
  public DBConnection (Connection connection) {
    this.connection = connection;
    this.start = -1;
  }

  public void startSession() { this.start = System.currentTimeMillis(); }
  
  public void endSession() { this.start = -1; }
  
  public boolean isExpire() {
    return  System.currentTimeMillis() - start >= 1*60*60*1000;
  }

  public boolean isClosed() {
    try {
      return connection.isClosed();
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
      return true;
    }
  }

  public boolean isFree() { return this.start == -1; }
  
  public Connection getConnection() { return connection; }

}
