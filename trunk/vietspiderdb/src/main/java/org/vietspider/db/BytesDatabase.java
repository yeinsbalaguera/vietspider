/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db;

import java.io.File;
import java.util.SortedMap;

import com.sleepycat.bind.ByteArrayBinding;
import com.sleepycat.bind.tuple.LongBinding;
import com.sleepycat.collections.StoredSortedMap;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 18, 2009  
 */
public class BytesDatabase extends JeDatabase {
  
  protected SortedMap<Long, byte[]> map;
  
  public BytesDatabase(File folder, String name, long cachedSize, boolean readOnly) throws Exception {
//    Properties props = new Properties();
//    props.setProperty("java.util.logging.FileHandler.limit", "536870912");
//    props.setProperty("java.util.logging.FileHandler.on", "false");

    EnvironmentConfig envConfig = new EnvironmentConfig();
    envConfig.setTransactional(false);
    envConfig.setAllowCreate(!readOnly);
    envConfig.setReadOnly(readOnly);
    envConfig.setCacheSize(cachedSize); 
//    envConfig.setConfigParam("java.util.logging.FileHandler.limit", "536870912");


    this.env = new Environment(folder, envConfig);
    DatabaseConfig dbConfig = new DatabaseConfig();
    dbConfig.setTransactional(false);
    dbConfig.setAllowCreate(!readOnly);
    dbConfig.setDeferredWrite(!readOnly);
    dbConfig.setReadOnly(readOnly);
    this.db = env.openDatabase(null, name, dbConfig);
    ByteArrayBinding dataBinding = new ByteArrayBinding();
    LongBinding keyBinding = new LongBinding();
    this.map = new StoredSortedMap<Long, byte[]> (db, keyBinding, dataBinding, true);
  }
  
}
