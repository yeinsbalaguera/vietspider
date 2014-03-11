/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.bean;

import org.vietspider.serialize.NodeMap;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 5, 2008  
 */
@NodeMap("databaseConfig")
public class DatabaseConfig implements DBInfo {
  
  @NodeMap("type")
  private int type;
  
  @NodeMap("host")
  private String host;
  @NodeMap("port")
  private String port;
  
  @NodeMap("database")
  private String database;
  
  @NodeMap("username")
  private String username;
  @NodeMap("password")
  private String password;
  
  @NodeMap("deleteOldTable")
  private boolean deleteOldTable;
  @NodeMap("createNewTable")
  private boolean createNewTable;
  
  public int getType() { return type; }
  public void setType(int type) { this.type = type; }
  
  public String getHost() { return host; }
  public void setHost(String host) {
    this.host = host; 
  }
  
  public String getPort() { return port; }
  public void setPort(String port) { this.port = port; }
  
  public String getDatabase() { return database; }
  public void setDatabase(String database) { this.database = database; }
  
  public String getUsername() { return username; }
  public void setUsername(String username) { this.username = username; }
  
  public String getPassword() { return password; }
  public void setPassword(String password) { this.password = password; }
  
  public boolean isDeleteOldTable() { return deleteOldTable; }
  public void setDeleteOldTable(boolean deleteOldTable) { this.deleteOldTable = deleteOldTable; }
  
  public boolean isCreateNewTable() { return createNewTable; }
  public void setCreateNewTable(boolean createNewTable) { this.createNewTable = createNewTable; }
}
