/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.users;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

import org.vietspider.common.io.UtilFile;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 11, 2008  
 */
public class Organization1 {
  
  private String url = null;

  public Organization1() throws Exception  {
    File f = UtilFile.getFolder("system/users");
    boolean create = !f.exists() || f.list() == null || f.list().length < 1;
    
    Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
    url = "jdbc:derby:"+f.getAbsolutePath()+"/vsdb;create=" + String.valueOf(create);
  }
  
  @SuppressWarnings("unused")
  private Connection createConnection() throws Exception {
    return DriverManager.getConnection(url, "vietspider", "vietspiderxbdf");
  }

  @SuppressWarnings("unused")
  private void createTable() {
    String users = "CREATE TABLE USER ("
        + "USERNAME        VARCHAR(100) PRIMARY KEY," 
        + "EMAIL           VARCHAR(200)," 
        + "PASSWORD        VARCHAR(100)," 
        + "DESC            VARCHAR(1000)"
      + ")";
    
    String groups = "CREATE TABLE GROUP ("
      + "GROUPNAME        VARCHAR(100) PRIMARY KEY," 
      + "           VARCHAR(200)," 
      + "password        VARCHAR(100)," 
      + "des             VARCHAR(1000)"
    + ")";
    
  }
  
}
