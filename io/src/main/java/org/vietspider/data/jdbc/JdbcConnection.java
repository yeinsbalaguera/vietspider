/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.data.jdbc;

import java.sql.Connection;

import org.vietspider.common.io.LogService;
import org.vietspider.data.jdbc.DBConnections.OutOfRangeConnection;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Dec 25, 2006
 */
public class JdbcConnection {  

  private static DBConnections JDBC;

  static {
    try{
      JDBC = new DBConnections();
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
      System.exit(-2);
    }
  }

  public static Connection get() throws Exception {
    Connection connection = null;
    int counter = 0;
    while(counter < 5) {
      try {
        connection = JDBC.getConnection();
      } catch (OutOfRangeConnection e) {
        try {
          Thread.sleep(500);
        } catch (Exception e2) {
        }
        if(counter >= 5) throw e;
      } catch (Exception e2) {
        throw e2;
      }
      counter++;
      if(counter >= 5 || connection != null) break;
    }
    if(connection == null) {
      throw new OutOfRangeConnection();
    }

    return connection;
  }

  public static void close() { 
    JDBC.closeConnections(); 
  }

  public static void open() { 
    JDBC.createConnections(); 
  }

  public static boolean isType(short t) {  return JDBC.isType(t);  }

  public static void release(Connection connection) { 
    JDBC.releaseConnection(connection); 
  }

}
