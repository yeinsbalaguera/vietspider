/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.je.forum2;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;

import com.sleepycat.bind.serial.ClassCatalog;
import com.sleepycat.bind.serial.SerialBinding;
import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.collections.StoredSortedMap;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 30, 2009  
 */
public class CodeDatabase {
  
  private Environment env;
  private Database db;
  
  private ClassCatalog tpCatalog;
  
  protected String name;
  private SortedMap<Integer, Integer> map;

//  public CodeDatabase(File folder, String type) throws Exception {
//    this(folder, folder.getName(), type); 
//  }

  public CodeDatabase(File folder, String name, String type) throws Exception {
    this.name = name;
    
    EnvironmentConfig envConfig = new EnvironmentConfig();
    envConfig.setTransactional(false);
    envConfig.setAllowCreate(true);
    envConfig.setCacheSize(5*1024*1024);
    
    this.env = new Environment(folder, envConfig);
    
//    Transaction txn = env.beginTransaction(null, null);
    DatabaseConfig dbConfig = new DatabaseConfig();
    dbConfig.setTransactional(false);
    dbConfig.setAllowCreate(true);
    this.db = env.openDatabase(null, /*"post_forum"*/type, dbConfig);
    
    tpCatalog = new StoredClassCatalog(db);
    
    // use Integer tuple binding for key entries
    TupleBinding<Integer> keyBinding = TupleBinding.getPrimitiveBinding(Integer.class);
    SerialBinding<Integer> dataBinding = new SerialBinding<Integer>(tpCatalog, Integer.class);
    this.map = new StoredSortedMap<Integer, Integer> (db, keyBinding, dataBinding, true);
//    System.out.println("================= >"+ map.size());
  }

  public void close() throws Exception {
    if (tpCatalog != null) {
      tpCatalog.close();
      tpCatalog = null;
    }
    
    if (db != null) {
      try {
        db.close();
      } catch (Exception e) {
//        LogService.getInstance().setMessage(e, e.toString());
      }
      db = null;
    }
    
    if (env != null) {
      env.close();
      env = null;
    }
  }
  
  public synchronized int commit(Map<Integer, Integer> codes) {
    int total = 0;
    Iterator<Integer> iterator = codes.keySet().iterator();
    while(iterator.hasNext()) {
      Integer key = iterator.next();
      Integer value = codes.get(key);
      map.put(key, value);
//      CachedCodes.getInstance().put(key, value);
      total++;
      iterator.remove();
    }
    return total;
  }
  
  public int search(int code) {
    Integer value  = map.get(code);
    if(value == null) return -1;
    return value.intValue();
  }

  public String getName() { return name; }
  
  public long size() { return map.size(); }
  
  /*private boolean isCreate(File folder) {
    if(!folder.exists()) return true;
    if(folder.isFile()) folder.delete();
    File [] files = folder.listFiles();
    return files == null || files.length < 1;
  }*/
  
}

