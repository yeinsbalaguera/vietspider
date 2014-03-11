/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.data.jdbc;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.vietspider.common.Application;
import org.vietspider.common.Install;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.db.CrawlerConfig;
import org.vietspider.db.database.DBScripts;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 4, 2008  
 */
public class DBConnections {
  
  public final static short ORACLE = 0;
  public final static short SQLSERVER = 1;
  public final static short MYSQL = 2;
  public final static short POSTGRES = 3;
  public final static short HSQL = 4;
  public final static short H2 = 5;
  public final static short APACHEDB = 6;
  
  
  private String url;
  private String user;
  private String password;
  
  private int maxConnection  = 10;
  
  private short type = 0;
  
  private List<DBConnection> pools ;
  
  public DBConnections() throws Exception {    
    File file = UtilFile.getFile("system", "database.xml");
    DBScripts initScripts = new DBScripts(file);
    String driver = initScripts.get("driver"); 
    Class.forName(driver);
    url = initScripts.get("connection");
    
    String ldriver = driver.toLowerCase() + " " + url.toLowerCase(); 
    
    if(ldriver.indexOf("oracledriver") > -1) {
      type = ORACLE;
    } else if (ldriver.indexOf("sqlserver") > -1) {
      type = SQLSERVER;
    } else if (ldriver.indexOf("mysql") > -1) {
      type = MYSQL;
    }  else if (ldriver.indexOf("postgresql") > -1) {
      type = POSTGRES;
    } else if (ldriver.indexOf("derby") > -1) {
      type = APACHEDB;
    } else if (ldriver.indexOf("hsqldb") > -1) {
      type = HSQL;
    }  else {
      type = H2;
    }
    
    if(url.indexOf("$vietspider$") > -1){
      File f = UtilFile.getFolder("content"); 
      url = url.replace("$vietspider$", f.getAbsolutePath());
    }
    user = initScripts.get("user");
    if(user != null) user = user.trim();
    password = initScripts.get("password");
    if(password == null) password = "";
    
    int counter = 0;
    while(true){
      if(counter >= 3) {
        LogService.getInstance().setMessage(null, "Cann't connect to database!");
        System.exit(1);      
      }
      try {
        Connection connection = createConnection().getConnection();
        connection.close();
        break;
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
      Thread.sleep(2000);
      counter++;      
    }    
 
    initDatabase(initScripts, file);
    
    createConnections();
    
    Application.addShutdown(new Application.IShutdown() {
      public void execute() {
        try {
          closeConnections();
        } catch (Exception e) {
          LogService.getInstance().setThrowable(e);
        }
      }
    });
  }
  
  void createConnections() {
    try {
      maxConnection = CrawlerConfig.EXCUTOR_SIZE;
      if(Application.LICENSE == Install.PERSONAL) {
        maxConnection += 3;
      } else if(Application.LICENSE == Install.PROFESSIONAL) {
        maxConnection += 5;
      } else if(Application.LICENSE == Install.ENTERPRISE) {
        maxConnection += 10;
      } else if(Application.LICENSE == Install.SEARCH_SYSTEM) {
        maxConnection += 20;
      }
    } catch (Exception e) {
      maxConnection = 10;
    }
    
    if(maxConnection > 50) maxConnection = 50;
    
    pools = new ArrayList<DBConnection>(maxConnection);
    
    for(int i = 0; i < 5; i++) {
      try {
        pools.add(createConnection());
      } catch (Exception e) {
        LogService.getInstance().setThrowable("APPLICATION", e);
      }
    }
  }
  
  void closeConnections () {
    for(int i = 0; i < pools.size(); i++) {
      try {
        pools.get(i).getConnection().close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
  }
  
  private void initDatabase(DBScripts initScripts, File file) throws Exception {
    File content  = UtilFile.getFolder("content");
    if(!content.exists()) initScripts.setInited(false);
    
    if(initScripts.getInited()) return;
    Connection connection = null;
    try {
      connection = createConnection().getConnection();
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
      System.exit(-1);
    }
    
    Statement statement = connection.createStatement();
    String [] scripts = initScripts.getInitDB();
    for(String sql : scripts){
      if(sql == null || sql.trim().length() < 1) continue;          
      try{
        statement.executeUpdate(sql);
      }catch (Exception e) {
        LogService.getInstance().setMessage(e, sql);          
      }
    } 
    statement.close();
    connection.close();
    initScripts.setInited(true);
    RWData.getInstance().save(file, initScripts.getBytes());
  }
  
  private DBConnection createConnection() throws Exception {
    if(user != null && user.length() > 0){
      return new DBConnection(DriverManager.getConnection(url, user, password));
    }
    return new DBConnection(DriverManager.getConnection(url));
  }
  
  Connection getConnection() throws Exception {
    for(int i = 0; i < pools.size(); i++) {
      DBConnection dbConnection = pools.get(i);
      if(dbConnection.isClosed()) {
        pools.set(i, createConnection());
      }
      
      if(dbConnection.isFree()) {
        dbConnection.startSession();
        return dbConnection.getConnection();
      }
    }
    
    for(int i = 0; i < pools.size(); i++) {
      DBConnection dbConnection = pools.get(i);
      if(dbConnection.isExpire()) {
        dbConnection.getConnection().close();
        dbConnection = createConnection();
        pools.set(i, dbConnection);
        return dbConnection.getConnection();
      }
    }
    
    if(pools.size() >= maxConnection) {
      throw new OutOfRangeConnection(pools.size(), maxConnection);
    }
    
    for(int i = 0; i < Math.min((maxConnection - pools.size()), 5); i++) {
      try {
        pools.add(createConnection());
      } catch (Exception e) {
        LogService.getInstance().setThrowable("APPLICATION", e);
      }
    }
    
    return getConnection();
  }
  
  

  void releaseConnection(Connection connection) {
    for(int i = 0; i < pools.size(); i++) {
      DBConnection dbConnection = pools.get(i);
      if(dbConnection.getConnection() != connection) continue;
      dbConnection.endSession();
    }
  }
  
  public String getPassword() { return password; }
  public String getUrl() { return url; }
  public String getUser() { return user; }

  public boolean isType(short t) { return type == t; }
  
  @SuppressWarnings("serial")
  public static class OutOfRangeConnection extends Exception {
    
    private int size = 0;
    private int max = 0;
    
    public OutOfRangeConnection() {
    }
    
    public OutOfRangeConnection(int size, int maxSize) {
      this.size = size;
      this.max = maxSize;
    }
    
    public String getMessage() {
      if(size > 0) {
        return "The number of connection is reached the maximum(size: " + size + ", maxSize: " + max + ")";
      }
      return "The number of connection is reached the maximum";
    }
  }
}
