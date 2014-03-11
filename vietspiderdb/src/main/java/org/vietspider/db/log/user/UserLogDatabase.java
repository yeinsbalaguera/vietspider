/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db.log.user;

import java.io.File;
import java.util.List;
import java.util.SortedMap;

import org.vietspider.bean.Article;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.MD5Hash;
import org.vietspider.db.JeDatabase;
import org.vietspider.db.Md5Binding;
import org.vietspider.db.VDatabase;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.XML2Object;

import com.sleepycat.bind.tuple.StringBinding;
import com.sleepycat.collections.StoredSortedMap;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 5, 2009  
 */
public class UserLogDatabase extends JeDatabase implements  VDatabase {

  protected volatile long lastAccess = System.currentTimeMillis();

  protected SortedMap<byte[], String> map;

  public UserLogDatabase(File folder) throws Exception {
    EnvironmentConfig envConfig = new EnvironmentConfig();
    envConfig.setTransactional(false);
    envConfig.setAllowCreate(true);

    envConfig.setCacheSize(1024*100); 

    this.env = new Environment(folder, envConfig);
    DatabaseConfig dbConfig = new DatabaseConfig();
    dbConfig.setTransactional(false);
    dbConfig.setAllowCreate(true);
    dbConfig.setDeferredWrite(true);
    this.db = env.openDatabase(null, "user_log", dbConfig);
    Md5Binding keyBinding = new Md5Binding();
    StringBinding dataBinding = new StringBinding();
    this.map = new StoredSortedMap<byte[], String> (db, keyBinding, dataBinding, true);
  }

  public synchronized UserLogs read(String articleId) throws Exception {
    if(isClose) return null;
    return read(MD5Hash.digest(articleId).getDigest());
  }
  
  public synchronized UserLog readTopAction(String articleId) throws Exception  {
    if(isClose) return null;
    UserLogs userLogs = read(articleId);
    if(userLogs == null) return null;
    List<UserLog> list = userLogs.getActions();

    for(int i = list.size()-1;  i > -1; i--) {
      UserLog userLog = list.get(i);
      if(userLog.getAction() == Article.SYNCHRONIZED) return userLog;
    }

    for(int i = list.size()-1;  i > -1; i--) {
      UserLog userLog = list.get(i);
      if(userLog.getAction() == Article.EDITED) return userLog;
    }

    for(int i = list.size()-1;  i > -1; i--) {
      UserLog userLog = list.get(i);
      if(userLog.getAction() == Article.READ) return userLog;
    }
    return null;
  }
  

  private UserLogs read(byte [] bytes) throws Exception {
    lastAccess = System.currentTimeMillis();
    String xml = map.get(bytes);
    if(xml == null) return null; 
    UserLogs userLogs = null;
    try {
      userLogs = XML2Object.getInstance().toObject(UserLogs.class, xml);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    return userLogs;
  }

  public synchronized void write(String articleId, String user, int action)  throws Throwable {
    if(isClose) return;
    lastAccess = System.currentTimeMillis();
    byte [] key = MD5Hash.digest(articleId).getDigest();
    UserLogs userLogs = read(key);
    if(userLogs == null) {
      userLogs = new UserLogs(articleId);
      userLogs.addAction(user, action);
      Object2XML bean2XML = Object2XML.getInstance();
      String xml = bean2XML.toXMLDocument(userLogs).getTextValue();
      map.put(key, xml);
      return;
    }

    userLogs.addAction(user, action);
    Object2XML bean2XML = Object2XML.getInstance();
    String xml = bean2XML.toXMLDocument(userLogs).getTextValue();
    map.put(key, xml);
  }

  public long lastAccess() { return lastAccess; }
}
