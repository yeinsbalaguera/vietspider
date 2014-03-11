/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.data.jdbc;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.common.util.Queue;
import org.vietspider.db.SystemProperties;
import org.vietspider.db.database.DBScripts;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Dec 25, 2006
 */
public class JdbcConnectionBak {  
  
  private static JdbcConnectionBak JDBC;
  
  public static Connection get() throws Exception { return JDBC.getConnection(); }
  
  public static void close() { JDBC.closeConnection(); }
  
  public static boolean isOracleDB() { return JDBC.isOracleDB; }
  
  public static void release(Connection connection) { JDBC.releaseConnection(connection); }
  
  static {
    try{
      JDBC = new JdbcConnectionBak();
    }catch (Exception e) {
      LogService.getInstance().setThrowable(e);
      System.exit(-2);
    }
  }

  private String url;
  private String user;
  private String password;
  
  private boolean isOracleDB = false;
  
  private Queue<Connection> pools ;
  
  public JdbcConnectionBak() throws Exception {    
    File file = UtilFile.getFile("system", "database.xml");
    DBScripts initScripts = new DBScripts(file);
    String driver = initScripts.get("driver"); 
    Class.forName(driver);
    isOracleDB = driver.indexOf("OracleDriver") > -1;
    
    url = initScripts.get("connection");
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
      if(counter >= 3) System.exit(1);      
      try{
        Connection connection = createConnection();
        connection.close();
        break;
      }catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
      Thread.sleep(2000);
      counter++;      
    }    
 
    initDatabase(initScripts, file);
    
    pools = new Queue<Connection>();
    int totalConnection = 25;
    try {
      SystemProperties system = SystemProperties.getInstance();
      int totalExecutor = Integer.parseInt(system.getValue(Application.TOTAL_EXECUTOR));
      int sizeOfExecutor = Integer.parseInt(system.getValue(Application.TOTAL_WORKER_OF_EXECUTOR));
      totalConnection = totalExecutor*sizeOfExecutor + 5;
    } catch (Exception e) {
      totalConnection = 25;
    }
    for(int i = 0; i < totalConnection; i++) {
      pools.push(createConnection());
    }
    
    Application.addShutdown(new Application.IShutdown() {
      public void execute() {
        try {
          closeConnection();
        }catch (Exception e) {
          LogService.getInstance().setThrowable(e);
        }
      }
    });
  }
  
  private void closeConnection () {
    while(pools.hasNext()) {
      Connection connection = pools.pop();
      try {
        if(connection != null && !connection.isClosed()) connection.close();
      }catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
  }
  
  private void initDatabase(DBScripts initScripts, File file) throws Exception {
    if(initScripts.getInited()) return;
    Connection connection = createConnection();
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
  
  private Connection createConnection() throws Exception {
    if(user != null && user.length() > 0){
      return DriverManager.getConnection(url, user, password);
    }
    return DriverManager.getConnection(url);
  }
  
  private Connection getConnection() throws Exception {
    if(!pools.hasNext()) {
      throw new Exception("The number of connection is reached the maximum") ;
    }     
    Connection connection = pools.pop();
    if(connection.isClosed()) connection = createConnection();
    return connection;
  }
  
  private void releaseConnection(Connection connection) { pools.push(connection); }
  
  public String getPassword() { return password; }
  public String getUrl() { return url; }
  public String getUser() { return user; }

}
