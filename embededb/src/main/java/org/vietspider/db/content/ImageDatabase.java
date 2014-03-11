/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db.content;

import java.io.File;
import java.util.SortedMap;

import org.vietspider.db.JeDatabase;

import com.sleepycat.bind.ByteArrayBinding;
import com.sleepycat.bind.tuple.StringBinding;
import com.sleepycat.collections.StoredSortedMap;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 18, 2009  
 */
public class ImageDatabase extends JeDatabase {
  
  protected SortedMap<String, byte[]> map;
  
  public ImageDatabase(File folder, String name, long cachedSize) throws Exception {
    EnvironmentConfig envConfig = new EnvironmentConfig();
    envConfig.setTransactional(false);
    envConfig.setAllowCreate(true);
    envConfig.setCacheSize(cachedSize); 
//    envConfig.setConfigParam("java.util.logging.FileHandler.limit", "536870912");

    this.env = new Environment(folder, envConfig);
    DatabaseConfig dbConfig = new DatabaseConfig();
    dbConfig.setTransactional(false);
    dbConfig.setAllowCreate(true);
    dbConfig.setDeferredWrite(true);
    this.db = env.openDatabase(null, name, dbConfig);
    ByteArrayBinding dataBinding = new ByteArrayBinding();
    StringBinding keyBinding = new StringBinding();
    this.map = new StoredSortedMap<String, byte[]> (db, keyBinding, dataBinding, true);
  }
  
  public void save(String id, byte[] bytes) throws Throwable {
    if(isClose) return;
    map.put(id, bytes);
  }
  
  public void sync() throws Throwable {
    db.sync();
  }
  
  public byte[] load(String id) throws Throwable {
    if(isClose) return null;
    return map.get(id);
  }
}
