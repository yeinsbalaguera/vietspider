/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.je.codes;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentHashMap;

import org.vietspider.common.io.LogService;
import org.vietspider.common.io.MD5Hash;
import org.vietspider.db.Md5Binding;

import com.sleepycat.bind.serial.ClassCatalog;
import com.sleepycat.bind.serial.SerialBinding;
import com.sleepycat.bind.serial.StoredClassCatalog;
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
public class Md5UrlDatabase implements IUrlDatabase {
  
  private Environment env;
  private Database db;
  
  private ClassCatalog tpCatalog;
  
  protected String name;
  private SortedMap<byte[], Integer> map;
  
  private volatile int counter = 0;
  
  private  volatile boolean isClose = false;

//  public CodeDatabase(File folder, String type) throws Exception {
//    this(folder, folder.getName(), type); 
//  }
  
  private ConcurrentHashMap<MD5Hash, Integer> temp = new ConcurrentHashMap<MD5Hash, Integer>();

  public Md5UrlDatabase(File folder, String name,
      String type, long cachedSize, boolean writeAllowed) throws Exception {
    this.name = name;
    
    EnvironmentConfig envConfig = new EnvironmentConfig();
    envConfig.setTransactional(false);
    envConfig.setAllowCreate(true);
    if(cachedSize > 0) {
      envConfig.setCacheSize(cachedSize);//5*1024*1024
    } else {
      envConfig.setCacheSize(1*1024*1024);
    }
    
    this.env = new Environment(folder, envConfig);
    
//    Transaction txn = env.beginTransaction(null, null);
    DatabaseConfig dbConfig = new DatabaseConfig();
    dbConfig.setTransactional(false);
    dbConfig.setAllowCreate(true);
    dbConfig.setDeferredWrite(true);
    this.db = env.openDatabase(null, /*"post_forum"*/type, dbConfig);
    
    tpCatalog = new StoredClassCatalog(db);
    
    // use Integer tuple binding for key entries
    Md5Binding keyBinding = new Md5Binding();
    SerialBinding<Integer> dataBinding = new SerialBinding<Integer>(tpCatalog, Integer.class);
    this.map = new StoredSortedMap<byte[], Integer> (db, keyBinding, dataBinding, writeAllowed);
//    System.out.println("==== > comparator "+ map.comparator());
//    System.out.println("================= >"+ map.size());
  }

  public void close() throws Throwable {
    isClose = true;
    internalClose();
  }
  
  public boolean isClose() { return isClose; }
 
  private void internalClose() throws Throwable {
    /*if (tpCatalog != null) {
      try {
        tpCatalog.close();
      } catch (Throwable e) {
        LogService.getInstance().setMessage("TRACKER" , null, e.toString());
      }
      tpCatalog = null;
    }*/
    
    if (db != null) {
      try {
        db.close();
      } catch (Throwable e) {
//        e.printStackTrace();
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
  
  public void save(MD5Hash key, Integer value) throws Throwable {
    if(isClose 
        || key == null 
        || value == null) return;
    temp.put(key, value);
    counter++;
  }
  
  public void commit(boolean check) throws Throwable {
    if(check && counter < 500) return;
//    System.out.println(" ==== > tong luong "+ db.getDatabaseName()+ "  / "+ temp.size());
    Iterator<Map.Entry<MD5Hash, Integer>> iterator = temp.entrySet().iterator();
    while(iterator.hasNext()) {
      Map.Entry<MD5Hash, Integer> entry = iterator.next();
      map.put(entry.getKey().getDigest(), entry.getValue());
      iterator.remove();
    }
//    System.out.println(" ==== > sau do "+ db.getDatabaseName()+ "  / "+ temp.size());
    db.sync();
    counter = 0;  
  }
  
  public int search(MD5Hash key) throws Throwable {
    if (isClose) return 1;
    Integer value  = temp.get(key);
    if(value != null) return value.intValue();
      
//    System.out.println(" truoc khi search "+ map.size());
    try {
      value = map.get(key.getDigest());
    } catch (RuntimeExceptionWrapper e) {
      return -1;
    }
//    System.out.println(" get ra duoc "+ new String(code) + " : "+ value);
    if(value == null) return -1;
    return value.intValue();
  }
  
  public void remove(MD5Hash key) throws Throwable {
    if (isClose) return ;
    temp.remove(key);
    System.out.println(map.get(key));
    map.remove(key);
    System.out.println(map.get(key));
  }

  public String getName() { return name; }
  
  public long size() {
    if (isClose) return 0;
    return map.size(); 
  }
  
  void load(Map<MD5Hash, Integer> cachedTemp) throws Throwable {
    Iterator<byte[]> iterator = map.keySet().iterator();
//    System.out.println(" thay co "+ map.size());
    while(iterator.hasNext()) {
      byte [] bytes = iterator.next();
      if(bytes == null) continue;
      MD5Hash key = new MD5Hash(bytes);
//      System.out.println("==== > "+key.toString());
      Integer value  = map.get(bytes);
      if(value == null) continue;
      cachedTemp.put(key, value);
    }
    
  }
  
}

