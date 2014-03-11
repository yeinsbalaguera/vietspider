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
import java.util.ArrayList;

import org.vietspider.bean.DatabaseConfig;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.db.database.DBScripts;
import org.vietspider.parser.xml.XMLNode;
import org.vietspider.token.TypeToken;

public class DatabaseSetting {

  public DatabaseSetting(){
  }

  public void set(DatabaseConfig config) throws Exception {
    StringBuilder connection = new StringBuilder();

    File folder = UtilFile.getFolder("system/database/");
    String folderName = null;
    switch (config.getType()) {
    case DatabaseConfig.H2:
      folderName = "h2/";

      connection.append("jdbc:h2:");
      if(config.getHost() == null || config.getHost().trim().isEmpty()) {
        connection.append("$vietspider$");
      } else {
        connection.append(config.getHost()).append(':').append(config.getPort());
      }
      connection.append('/').append(config.getDatabase()).append('/');
      connection.append(config.getDatabase()).append(";create=true;recover=1");

      break;
    case DatabaseConfig.APACHE_DERBY:
      folderName = "apachedb";

      connection.append("jdbc:derby:");
      if(config.getHost() == null || config.getHost().trim().isEmpty()) {
        connection.append("$vietspider$");
      } else {
        connection.append(config.getHost()).append(':').append(config.getPort());
      }
      connection.append('/').append(config.getDatabase()).append(";create=true");

      break;
    case DatabaseConfig.HSQL:
      folderName = "hsql";

      connection.append("jdbc:hsqldb:file:/");
      if(config.getHost() == null || config.getHost().trim().isEmpty()) {
        connection.append("$vietspider$");
      } else {
        connection.append(config.getHost()).append(':').append(config.getPort());
      }
      connection.append('/').append(config.getDatabase());

      break;
    case DatabaseConfig.MY_SQL:
      folderName = "mysql";

      connection.append("jdbc:mysql://").append(config.getHost());
      connection.append(':').append(config.getPort()).append('/');
      connection.append(config.getDatabase()).append("?useUnicode=true&characterEncoding=utf-8");

      break;
    case DatabaseConfig.MS_SQL_SERVER:
      folderName = "mssql";

      connection.append("jdbc:jtds:sqlserver://").append(config.getHost());
      connection.append(':').append(config.getPort()).append('/');
      connection.append(config.getDatabase()).append(";integratedSecurity=true;");
      break;
    case DatabaseConfig.MS_SQL_SERVER_EXPRESS:
      folderName = "mssql";

      connection.append("jdbc:jtds:sqlserver://").append(config.getHost());
      connection.append(':').append(config.getPort()).append('/');
      connection.append(config.getDatabase()).append(";integratedSecurity=true;");
      break;
      //    case DatabaseConfig.MS_SQL_SERVER_2005:
      //      folderName = "mssql2005";
      //      
      //      connection.append("jdbc:jtds:sqlserver://").append(config.getHost());
      //      connection.append(':').append(config.getPort()).append('/');
      //      connection.append(config.getDatabase()).append(";integratedSecurity=true;");
      //      break;
    case DatabaseConfig.ORACLE:
      folderName = "oracle";

      connection.append("jdbc:oracle:thin:@").append(config.getHost());
      connection.append(':').append(config.getPort()).append(':').append(config.getDatabase());
      break;
    case DatabaseConfig.POST_GRES:
      folderName = "postgres";

      connection.append("jdbc:postgresql://").append(config.getHost());
      if(config.getPort() == null || config.getPort().trim().isEmpty()) {
      } else {
        connection.append(':').append(config.getPort());
      }
      connection.append('/').append(config.getDatabase());
      break;

    default:
      //      System.out.println(config.getHost() + " : " + config.getPort());
      if(config.getHost() != null 
          && !config.getHost().trim().isEmpty()
          && config.getPort() != null
          && !config.getPort().trim().isEmpty()) {
        connection.append(config.getHost()).append(':').append(config.getPort());
      }
      break;
    }

    File file = null;
    if(folderName == null) {
      file = new File(folder, "database.xml");
      folder = null;
    } else {
      folder = new File(folder, folderName);
      file = new File(folder, "database.xml");
    }

    if(file == null || !file.exists()) return;
    DBScripts initScripts = new DBScripts(file);
    initScripts.setInited(false);
    try {
      setCommonConfig(initScripts, config, connection.toString());
    } catch (Throwable e) {
      LogService.getInstance().setThrowable(e);
    }
    write(folder, initScripts);
  }

  private void setCommonConfig(DBScripts initScripts, 
      DatabaseConfig config, String connection) throws Exception {
    XMLNode xmlNode = initScripts.getNode("connection"); 
    xmlNode.getChildren().clear();
    XMLNode value = new XMLNode(connection.toCharArray(), null, TypeToken.CONTENT);
    xmlNode.getChildren().add(value); 

    xmlNode = initScripts.getNode("user"); 
    xmlNode.getChildren().clear();
    if(config.getUsername() != null) {
      value = new XMLNode(config.getUsername().toCharArray(), null, TypeToken.CONTENT);
      xmlNode.getChildren().add(value);
    }

    xmlNode = initScripts.getNode("password"); 
    xmlNode.getChildren().clear();
    if(config.getPassword() != null) {
      value = new XMLNode(config.getPassword().toCharArray(), null, TypeToken.CONTENT);
      xmlNode.getChildren().add(value);
    }

    String [] scripts = initScripts.getInitDB();
    ArrayList<String> newScripts = new ArrayList<String>();

    xmlNode = initScripts.getNode("scripts");
    if(xmlNode == null) return;
    xmlNode.getChildren().clear();

    if(config.isDeleteOldTable())  {
      newScripts.add("DROP TABLE CONTENT");
      newScripts.add("DROP TABLE RELATION");
      newScripts.add("DROP TABLE IMAGE");
      newScripts.add("DROP TABLE META");
      newScripts.add("DROP TABLE DOMAIN");
    }  

    for(String script : scripts) {
      script = script.trim();
      if(script.toUpperCase().trim().startsWith("DROP TABLE")) continue;
      newScripts.add(script);
    }
    for(String script : newScripts) {
      XMLNode scriptValue = new XMLNode(script.toCharArray(), null, TypeToken.CONTENT);
      XMLNode node = new XMLNode("script".toCharArray(), "script", TypeToken.TAG);
      node.addChild(scriptValue);
      xmlNode.getChildren().add(node);
    }
    initScripts.setInited(!config.isCreateNewTable());
  }

  private void write(File folder, DBScripts dbScripts) throws Exception {
    try {
      File systemFolder = UtilFile.getFolder("system");
      if(folder != null) {
        RWData.getInstance().copy(new File(folder, "dbload.xml"), new File(systemFolder, "dbload.xml"));
        RWData.getInstance().copy(new File(folder, "dbsave.xml"), new File(systemFolder, "dbsave.xml"));
        RWData.getInstance().copy(new File(folder, "dbdelete.xml"), new File(systemFolder, "dbdelete.xml"));
      } else {
        new File(systemFolder, "dbload.xml").delete();
        new File(systemFolder, "dbsave.xml").delete();
        new File(systemFolder, "dbdelete.xml").delete();
      }
      RWData.getInstance().save(new File(systemFolder, "database.xml"), dbScripts.getBytes());
    } catch (Throwable e) {
      LogService.getInstance().setThrowable(e);
    }
  }

  public DatabaseConfig loadDefault()  throws Exception {
    DatabaseConfig databaseConfig = new DatabaseConfig();
    File file = UtilFile.getFile("system", "database.xml");
    DBScripts initScripts = new DBScripts(file);

    databaseConfig.setUsername(initScripts.get("user"));
    databaseConfig.setPassword(initScripts.get("password"));
    databaseConfig.setCreateNewTable(!initScripts.getInited());

    String connection = initScripts.get("connection");
    if(connection.indexOf(":h2:") > -1) {
      databaseConfig.setType(DatabaseConfig.H2);
      databaseConfig.setDatabase(getDatabaseName(connection, ';'));
    } else if(connection.indexOf(":derby:") > -1) {
      databaseConfig.setType(DatabaseConfig.APACHE_DERBY);
      databaseConfig.setDatabase(getDatabaseName(connection, ';'));
    } else  if(connection.indexOf(":hsqldb:") > -1) {
      databaseConfig.setType(DatabaseConfig.HSQL);
      databaseConfig.setDatabase(getDatabaseName(connection, ';'));
    } else if(connection.indexOf(":mysql:") > -1) {
      databaseConfig.setType(DatabaseConfig.MY_SQL);
      getHostInfo(databaseConfig, connection);
      databaseConfig.setDatabase(getDatabaseName(connection, '?'));
    } else if(connection.indexOf(":sqlserver:") > -1) {
      databaseConfig.setType(DatabaseConfig.MS_SQL_SERVER);
      databaseConfig.setDatabase(getDatabaseName(connection, ';'));
      getHostInfo(databaseConfig, connection);
    } else if(connection.indexOf(":oracle:") > -1) {
      databaseConfig.setType(DatabaseConfig.ORACLE);
      databaseConfig.setDatabase(getOracleDatabaseName(connection));
      getOracleHostInfo(databaseConfig, connection);
    } else if(connection.indexOf(":postgresql:") > -1) {
      databaseConfig.setType(DatabaseConfig.POST_GRES);
      databaseConfig.setDatabase(getDatabaseName(connection, '?'));
      getHostInfo(databaseConfig, connection);
    } else {
      int idx = connection.indexOf(':');
      if(idx > -1) {
        databaseConfig.setHost(connection.substring(0, idx));
        databaseConfig.setPort(connection.substring(idx + 1));
      }
    }

    return databaseConfig;
  }

  private String getDatabaseName(String connection, char c) {
    try {
      int idx = connection.indexOf(c);
      if(idx < 1) idx = connection.length();
      connection = connection.substring(0, idx);
      idx = connection.lastIndexOf('/');
      return connection.substring(idx+1);
    } catch (Exception e) {
    }
    return "";
  }

  private String getOracleDatabaseName(String connection) {
    int idx = connection.lastIndexOf(':');
    if(idx < 1) return connection;
    return connection.substring(idx+1);
  }

  private void getHostInfo(DatabaseConfig config, String connection) {
    //    System.out.println(" tai day ta co " + connection);
    try {
      int idx = connection.indexOf("//");
      if(idx < 1) return;
      connection = connection.substring(idx+2);
      idx = connection.indexOf('/');
      if(idx < 1) return;
      String host  = connection.substring(0, idx);
      idx = host.indexOf(':');
      if(idx > -1) {
        config.setHost(host.substring(0, idx));
        config.setPort(host.substring(idx + 1));
      } else {
        config.setHost(host);
      }
    } catch (Exception e) {
    }
  }

  private void getOracleHostInfo(DatabaseConfig config, String connection) {
    //  System.out.println(" tai day ta co " + connection);
    try {
      int idx = connection.indexOf('@');
      if(idx < 1) return;
      connection = connection.substring(idx+1);
      idx = connection.indexOf(':');
      if(idx < 1) return;
      String host  = connection.substring(0, idx);
      config.setHost(host);

      int portIdx = connection.indexOf(':', idx+2);
      if(portIdx < 1) return;
      String port = connection.substring(idx+1, portIdx);
      config.setPort(port);
    } catch (Exception e) {
    }
  }

  //  private void loadDatabaseInfo(DBScripts initScripts, DatabaseConfig config, String connection) throws Exception {
  //    String driver = initScripts.get("driver"); 
  //    Class.forName(driver);
  //    Connection connectionValue = 
  //      DriverManager.getConnection(connection, config.getUsername(), config.getPassword());
  //    connectionValue.get
  //    DatabaseMetaData meta = connectionValue.getMetaData();
  ////    meta.getda
  //  }

}