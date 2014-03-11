/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.bean.sync;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.bean.DBInfo;
import org.vietspider.serialize.NodeMap;
import org.vietspider.serialize.NodesMap;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 5, 2008  
 */
@NodeMap("syncDatabaseConfig")
public class SyncDatabaseConfig  implements DBInfo {
  
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
  
  @NodeMap("auto")
  private boolean auto;
  
  @NodeMap("driver")
  private String driver;
  
  @NodeMap("connection")
  private String connection;
  
  @NodeMap("load-categories-script")
  private String loadCategoriesScript;
  
  @NodeMap("load-article-by-title")
  private String loadArticleByTitle;
  
  @NodesMap(value="scripts", item="script")
  private List<String> scripts = new ArrayList<String>();
  
  public int getType() { return type; }
  public void setType(int type) { this.type = type; }
  
  public String getHost() { return host; }
  public void setHost(String host) { this.host = host; }
  
  public String getPort() { return port; }
  public void setPort(String port) { this.port = port; }
  
  public String getDatabase() { return database; }
  public void setDatabase(String database) { this.database = database; }
  
  public String getUsername() { return username; }
  public void setUsername(String username) { this.username = username; }
  
  public String getPassword() { return password; }
  public void setPassword(String password) { this.password = password; }
  
  public boolean isAuto() { return auto; }
  public void setAuto(boolean auto) { this.auto = auto; }
  
  public List<String> getScripts() { return scripts; }
  public List<String> cloneScripts() {
    List<String> newScripts = new ArrayList<String>(scripts.size());
    for(int i = 0; i < scripts.size(); i++) {
      newScripts.add(scripts.get(i));
    }
    return newScripts; 
  }
  public void setScripts(List<String> scripts) { this.scripts = scripts; }
  
  public String getDriver() { return driver; }
  public void setDriver(String driver) { this.driver = driver; }
  
  public String getConnection() { return connection; }
  public void setConnection(String connection) { this.connection = connection; }
  
  public String getLoadCategoriesScript() { return loadCategoriesScript; }
  public void setLoadCategoriesScript(String loadCategoriesScript) {
    this.loadCategoriesScript = loadCategoriesScript;
  }
  
  public String getLoadArticleByTitle() { return loadArticleByTitle; }
  public void setLoadArticleByTitle(String loadArticleByTitle) {
    this.loadArticleByTitle = loadArticleByTitle;
  }
  
  
}
