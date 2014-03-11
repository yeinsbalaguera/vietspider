/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.je.page;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentHashMap;

import org.vietspider.common.io.LogService;
import org.vietspider.common.io.MD5Hash;
import org.vietspider.db.Md5Binding;

import com.sleepycat.bind.tuple.LongBinding;
import com.sleepycat.collections.StoredSortedMap;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.util.RuntimeExceptionWrapper;
/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 30, 2009  
 */
public class PageSizeDatabase implements IPageSizeDatabase {
  
  private Environment env;
  private Database db;
  
  protected String name;
  private SortedMap<byte[], Long> map;
  
  private volatile int counter = 0;
  
  private  volatile boolean isClose = false;

  private ConcurrentHashMap<MD5Hash, Long> temp = new ConcurrentHashMap<MD5Hash, Long>();

  public PageSizeDatabase(File folder, String name,
      String type, long cachedSize, boolean writeAllowed) throws Exception {
    this.name = name;
    
    EnvironmentConfig envConfig = new EnvironmentConfig();
    envConfig.setTransactional(false);
    envConfig.setAllowCreate(true);
    if(cachedSize > 0) {
      envConfig.setCacheSize(cachedSize);
    } else {
      envConfig.setCacheSize(1*1024*1024);
    }
    
    this.env = new Environment(folder, envConfig);
    
    DatabaseConfig dbConfig = new DatabaseConfig();
    dbConfig.setTransactional(false);
    dbConfig.setAllowCreate(true);
    dbConfig.setDeferredWrite(true);
    this.db = env.openDatabase(null, type, dbConfig);
    
    Md5Binding keyBinding = new Md5Binding();
    LongBinding dataBinding = new LongBinding();
    this.map = new StoredSortedMap<byte[], Long> (db, keyBinding, dataBinding, writeAllowed);
  }

  public void close() throws Throwable {
    isClose = true;
    internalClose();
  }
  
  public boolean isClose() { return isClose; }
 
  private void internalClose() throws Throwable {
    if (db != null) {
      try {
        db.close();
      } catch (Throwable e) {
        LogService.getInstance().setMessage("TRACKER" , null, e.toString());
      }
      db = null;
    }
    
    if (env != null) {
      try {
        env.close();
      } catch (Throwable e) {
//        e.printStackTrace();
        LogService.getInstance().setMessage("TRACKER" , null, e.toString());
      }
      env = null;
    }
  }
  
  public void update(MD5Hash key, Long value) throws Throwable {
    if(isClose 
        || key == null 
        || value == null) return;
    temp.put(key, value);
    counter++;
  }
  
  public void commit(boolean check) throws Throwable {
    if(check && counter < 500) return;
//    System.out.println(" ==== > tong luong "+ db.getDatabaseName()+ "  / "+ temp.size());
    Iterator<Map.Entry<MD5Hash, Long>> iterator = temp.entrySet().iterator();
    while(iterator.hasNext()) {
      Map.Entry<MD5Hash, Long> entry = iterator.next();
      map.put(entry.getKey().getDigest(), entry.getValue());
      iterator.remove();
    }
    db.sync();
    counter = 0;  
  }
  
  public long search(MD5Hash key) throws Throwable {
    if (isClose) return 1;
    Long value  = temp.get(key);
    if(value != null) return value.longValue();
      
//    System.out.println(" truoc khi search "+ map.size());
    try {
      value = map.get(key.getDigest());
    } catch (RuntimeExceptionWrapper e) {
      return -1;
    }
//    System.out.println(" get ra duoc "+ new String(code) + " : "+ value);
    if(value == null) return -1;
    return value.longValue();
  }

  public String getName() { return name; }
  
  public long size() {
    if (isClose) return 0;
    return map.size(); 
  }
  
  void load(Map<MD5Hash, Long> cachedTemp) throws Throwable {
    Iterator<byte[]> iterator = map.keySet().iterator();
    while(iterator.hasNext()) {
      byte [] bytes = iterator.next();
      if(bytes == null) continue;
      MD5Hash key = new MD5Hash(bytes);
      Long value  = map.get(bytes);
      if(value == null) continue;
      cachedTemp.put(key, value);
    }
    
  }
  
}

