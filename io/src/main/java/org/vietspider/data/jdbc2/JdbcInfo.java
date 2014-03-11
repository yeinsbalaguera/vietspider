/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.data.jdbc2;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.db.database.DBScripts;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jun 30, 2011  
 */
class JdbcInfo {

  final static short ORACLE = 0;
  final static short SQLSERVER = 1;
  final static short MYSQL = 2;
  final static short POSTGRES = 3;
  final static short HSQL = 4;
  final static short H2 = 5;
  final static short APACHEDB = 6;

  short type = 0;

  Conn main;

  private List<Conn> conns = new ArrayList<Conn>();

  private String url;
  private String user;
  private String password;

  public JdbcInfo() throws Exception {
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

    main = new Conn(create());

    initDatabase(initScripts, file);
  }

  Connection create() {
    int counter = 0;
    while(true){
      if(counter >= 3) {
        LogService.getInstance().setMessage(null, "Cann't connect to database!");
        System.exit(1);      
      }

      try {
        if(user != null && user.length() > 0){
          return DriverManager.getConnection(url, user, password);
        }
        return DriverManager.getConnection(url);
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }

      try {
        Thread.sleep(2000);
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
      counter++;      
    }    
  }

  void initDatabase(DBScripts initScripts, File file) throws Exception {
    File content  = UtilFile.getFolder("content");
    if(!content.exists()) initScripts.setInited(false);

    if(initScripts.getInited()) return;

    main.create();
    try {
      main.batch(initScripts.getInitDB());
    } finally {
      main.realse();
    }
    initScripts.setInited(true);
    RWData.getInstance().save(file, initScripts.getBytes());
  }

  synchronized Conn generateConn() {
//    System.out.println(" ===========  > total connection "+ conns.size());
//    if(conns.size() == 1) {
//      new Exception().printStackTrace();
//    }
    if(conns.size() >= 5) {
      LogService.getInstance().setMessage(null, "JDBC connection not available. Out of size!");
      return null;
    }

    for (int i = 0; i < conns.size(); i++) {
      if(!conns.get(i).busy()) {
        conns.get(i).setLastAccess();
//        System.out.println(" use conn" + conns.get(i).hashCode());
        return conns.get(i); 
      }
    }

    if(conns.size() < 5) {
      Conn conn = new Conn(create());
//      System.out.println(" create new connection " + conn);
      conns.add(conn);
//      System.out.println(" use conn"+ conn.hashCode());
      return conn;
    }

    LogService.getInstance().setMessage(null, "JDBC connection not available. Out of size!");
    return null;
  }
  
  void killTimeout() {
    Iterator<Conn> iterator = conns.iterator();
    while(iterator.hasNext()) {
      Conn conn = iterator.next();
      if(conn.isTimeout()) {
        conn.close();
        iterator.remove();
      }
    }
  }

  class Conn {

    private Connection connection;
    private long lastAccess = System.currentTimeMillis();

    private Statement statement;
    private PreparedStatement prepared = null;

    Conn(Connection con) {
      this.connection = con;
    }
    
    void setLastAccess() {
      lastAccess = System.currentTimeMillis();
    }

    oracle.sql.CLOB oracleClob () throws Exception {
      return oracle.sql.CLOB.createTemporary(
          connection, true, oracle.sql.CLOB.DURATION_SESSION);
    }

    String setMaxRows(String sql) throws Exception {
      String [] eles = sql.split("MAXROWS");
      int max = Integer.parseInt(eles[1].trim());
      statement.setMaxRows(max);
      return eles[0];
    }

    boolean busy() {
      return statement != null || prepared != null; 
    }


    boolean isTimeout() {
      return System.currentTimeMillis() - lastAccess >= 30*1000l;
    }

    void create() throws Exception {
      if(statement != null) statement.close();
      statement = connection.createStatement();
    }

    void create(int _type, int mode) throws Exception {
      if(statement != null) statement.close();
      statement = connection.createStatement(_type, mode);
    }

    ResultSet executeQuery(String sql) throws Exception {
      return statement.executeQuery(sql);
    }

    void setMaxRows(int max) throws Exception {
      statement.setMaxRows(max);
    }

    public void execute(String...sqls) throws Exception  {
      try {
        connection.setAutoCommit(false);
        statement = connection.createStatement();
        for(String sql : sqls) {
          statement.execute(sql);
        }
        connection.commit();
        connection.setAutoCommit(true);
      } finally {
        if(statement != null) statement.close();
      }
    }

    void batch(String...sqls) throws Exception {
      statement.clearBatch();
      for(int i = 0; i < sqls.length; i++) {
        if(sqls[i] == null 
            || sqls[i].trim().isEmpty()) continue;
        if(i > 0 && i%10 == 0) {
          statement.executeBatch();
          statement.clearBatch();
        }
        statement.addBatch(sqls[i]);
      }
      statement.executeBatch();
    }

    void batch(Collection<String> collection) throws Exception {
      boolean _defaultAutoCommint = connection.getAutoCommit();
      connection.setAutoCommit(false);
      statement.clearBatch();
      int i = 0;
      Iterator<String> iterator = collection.iterator();
      while(iterator.hasNext()) {
        String sql = iterator.next();
        if(sql == null 
            || sql.trim().isEmpty()) continue;

        if(i > 0 && i%10 == 0) {
          statement.executeBatch();
          statement.clearBatch();
        }
        statement.addBatch(sql);
        i++;
      }
      statement.executeBatch();
      connection.commit();
      connection.setAutoCommit(_defaultAutoCommint);
    }

    void realse() {
      try {
        statement.close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      } finally {
        statement = null;
      }
    }

    void releasePrepared() throws Exception {
      try {
        prepared.close();
      } finally {
        prepared = null;
      }
    }

    PreparedStatement prepared(String sql) throws Exception {
      prepared = connection.prepareStatement(sql);
      return prepared;
    }

    void close() {
      try {
        connection.close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
  }
}
