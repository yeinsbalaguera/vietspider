/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.data.jdbc.install;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 3, 2008  
 */
import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;

import org.vietspider.bean.DBInfo;
import org.vietspider.bean.DatabaseConfig;
import org.vietspider.common.io.UtilFile;

public class DatabaseChecker {
  
  public DatabaseChecker(){
  }
  
  public String connect(DBInfo config) throws Exception {
    StringBuilder builder = new StringBuilder();
    StringBuilder connection = new StringBuilder();
    switch (config.getType()) {
    case DatabaseConfig.H2:
      Class.forName("org.h2.Driver");
      connection.append("jdbc:h2:");
      if(config.getHost() == null || config.getHost().trim().isEmpty()) {
        File f = UtilFile.getFolder("content");
        connection.append(f.getAbsolutePath());
      } else {
        connection.append(config.getHost()).append(':').append(config.getPort());
      }
      connection.append('/').append(config.getDatabase()).append('/');
      connection.append(config.getDatabase()).append(";create=true");
      break;
    case DatabaseConfig.APACHE_DERBY:
      Class.forName("net.sourceforge.jtds.jdbc.Driver");
      connection.append("jdbc:derby:");
      if(config.getHost() == null || config.getHost().trim().isEmpty()) {
        File f = UtilFile.getFolder("content");
        connection.append(f.getAbsolutePath());
      } else {
        connection.append(config.getHost()).append(':').append(config.getPort());
      }
      connection.append('/').append(config.getDatabase()).append(";create=true");
      
      break;
    case DatabaseConfig.HSQL:
      Class.forName("org.hsqldb.jdbcDriver");
      connection.append("jdbc:hsqldb:file:/");
      if(config.getHost() == null || config.getHost().trim().isEmpty()) {
        File f = UtilFile.getFolder("content");
        connection.append(f.getAbsolutePath());
      } else {
        connection.append(config.getHost()).append(':').append(config.getPort());
      }
      connection.append('/').append(config.getDatabase());
      break;
    case DatabaseConfig.MY_SQL:
      Class.forName("com.mysql.jdbc.Driver");
      connection.append("jdbc:mysql://").append(config.getHost());
      connection.append(':').append(config.getPort()).append('/');
      connection.append(config.getDatabase()).append("?useUnicode=true&characterEncoding=utf-8");
      break;
    case DatabaseConfig.MS_SQL_SERVER:
      Class.forName("net.sourceforge.jtds.jdbc.Driver");
      connection.append("jdbc:jtds:sqlserver://").append(config.getHost());
      connection.append(':').append(config.getPort()).append('/');
      connection.append(config.getDatabase()).append(";integratedSecurity=true;");
      break;
    case DatabaseConfig.MS_SQL_SERVER_EXPRESS:
      Class.forName("net.sourceforge.jtds.jdbc.Driver");
      connection.append("jdbc:jtds:sqlserver://").append(config.getHost());
      connection.append(':').append(config.getPort()).append('/');
      connection.append(config.getDatabase()).append(";integratedSecurity=true;");
      break;
//    case DatabaseConfig.MS_SQL_SERVER_2005:
//      Class.forName("net.sourceforge.jtds.jdbc.Driver");
//      connection.append("jdbc:jtds:sqlserver://").append(config.getHost());
//      connection.append(':').append(config.getPort()).append('/');
//      connection.append(config.getDatabase()).append(";integratedSecurity=true;");
//      break;
    case DatabaseConfig.ORACLE:
      Class.forName("oracle.jdbc.driver.OracleDriver");
      connection.append("jdbc:oracle:thin:@").append(config.getHost());
      connection.append(':').append(config.getPort()).append(':').append(config.getDatabase());
      break;
    case DatabaseConfig.POST_GRES:
      Class.forName("org.postgresql.Driver");
      connection.append("jdbc:postgresql://").append(config.getHost());
      if(config.getPort() == null || config.getPort().trim().isEmpty()) {
      } else {
        connection.append(':').append(config.getPort());
      }
      connection.append('/').append(config.getDatabase());
      break;
    default:
      Class.forName("org.h2.Driver");
      if(config.getHost() == null || config.getHost().trim().isEmpty()) {
        File f = UtilFile.getFolder("content");
        connection.append(f.getAbsolutePath());
        connection.append('/').append(config.getDatabase()).append('/');
        connection.append(config.getDatabase()).append(";create=true");
      }
      break;
    }
    Connection connectionValue = 
      DriverManager.getConnection(connection.toString(), config.getUsername(), config.getPassword());
    if (connectionValue!= null) {
      builder.append("Successfully connected");
      builder.append("\n");
      // Meta data
      DatabaseMetaData meta = connectionValue.getMetaData();
      builder.append("\nDriver Information");
      builder.append("Driver Name: ").append(meta.getDriverName());
      builder.append("Driver Version: ").append(meta.getDriverVersion());
      builder.append("\nDatabase Information ");
      builder.append("Database Name: ").append(meta.getDatabaseProductName());
      builder.append("Database Version: ").append(meta.getDatabaseProductVersion());
    }
    return builder.toString();
  }
  
  public static void main (String args[]) throws Exception {
  }
}