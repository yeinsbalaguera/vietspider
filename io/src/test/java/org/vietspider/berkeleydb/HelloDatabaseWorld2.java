/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.berkeleydb;

import java.io.File;
import java.util.SortedMap;

import com.sleepycat.bind.serial.ClassCatalog;
import com.sleepycat.bind.serial.SerialBinding;
import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.bind.tuple.IntegerBinding;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.collections.StoredSortedMap;
import com.sleepycat.collections.TransactionRunner;
import com.sleepycat.collections.TransactionWorker;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.Transaction;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 30, 2009  
 */
public class HelloDatabaseWorld2 implements TransactionWorker {

  private static final String[] INT_NAMES = {
    "Hello", "Database", "World",
  };
  private static boolean create = true;

  private Environment env;
  private ClassCatalog catalog;
  private Database db;
  private SortedMap<Integer, Integer> map;

  /** Creates the environment and runs a transaction */
  public static void main(String[] argv) throws Exception {

    // environment is transactional
    EnvironmentConfig envConfig = new EnvironmentConfig();
    envConfig.setTransactional(true);
    if (create) {
      envConfig.setAllowCreate(true);
    }
    Environment env = new Environment(new File("D:\\Temp\\db"), envConfig);

    // create the application and run a transaction
    HelloDatabaseWorld2 worker = new HelloDatabaseWorld2(env);
    TransactionRunner runner = new TransactionRunner(env);
    try {
      // open and access the database within a transaction
      runner.run(worker);
    } finally {
      // close the database outside the transaction
      worker.close();
    }
  }

  /** Creates the database for this application */
  private HelloDatabaseWorld2(Environment env) throws Exception {

    this.env = env;
    open();
  }

  /** Performs work within a transaction. */
  public void doWork()
  throws Exception {

    writeAndRead();
  }

  /** Opens the database and creates the Map. */
  private void open() throws Exception {

    // use a generic database configuration
    DatabaseConfig dbConfig = new DatabaseConfig();
    dbConfig.setTransactional(true);
    if (create) {
      dbConfig.setAllowCreate(true);
    }

    // catalog is needed for serial bindings (java serialization)
    Database catalogDb = env.openDatabase(null, "catalog", dbConfig);
    catalog = new StoredClassCatalog(catalogDb);

    // use Integer tuple binding for key entries
    TupleBinding<Integer> keyBinding =
      TupleBinding.getPrimitiveBinding(Integer.class);

    // use String serial binding for data entries
    SerialBinding<Integer> dataBinding =
      new SerialBinding<Integer>(catalog, Integer.class);

    this.db = env.openDatabase(null, "helloworld", dbConfig);

    // create a map view of the database
    this.map = new StoredSortedMap<Integer, Integer>
    (db, keyBinding, dataBinding, true);
  }

  /** Closes the database. */
  private void close() throws Exception {

    if (catalog != null) {
      catalog.close();
      catalog = null;
    }
    if (db != null) {
      db.close();
      db = null;
    }
    
    if (env != null) {
      env.close();
      env = null;
    }
  }

  /** Writes and reads the database via the Map. */
  private void writeAndRead() throws DatabaseException {

    // check for existing data
//  Integer key = new Integer(0);
//  Integer val = map.get(key);
    int max = 1000000;
    Transaction txn = env.beginTransaction(null, null);
    
    DatabaseEntry keyEntry = new DatabaseEntry();
    DatabaseEntry dataEntry = new DatabaseEntry();
    
    for(int i = 0 ; i < max; i++) {
      int key = (int)(Math.random()*max);
      int value  = (int)(Math.random()*max);
      IntegerBinding.intToEntry(key, keyEntry);
      IntegerBinding.intToEntry(value, dataEntry);
      db.put(txn, keyEntry, dataEntry);
      if(i%1000 == 0) {
        txn.commit();
        txn = env.beginTransaction(null, null);
        System.out.println("=== > start " +  i);
      }
      
      //map.put(key, value);
    }
    txn.commit();
    
//    txn = env.beginTransaction(null, null);
    for(int i = 0 ; i < 100; i++) {
      int key = (int)(Math.random()*max);
      IntegerBinding.intToEntry(key, keyEntry);
      
      db.get(null, keyEntry, dataEntry, LockMode.DEFAULT);
      Integer value = IntegerBinding.entryToInt(dataEntry);
      if(value == null) continue;
//      if(value == null) {
//        value  = (int)(Math.random()*max);
//        map.put(key, value);
//      }

//    Map.Entry<Integer, String> entry = iter.next();
      System.out.println(key + " ==== " + value);
    }
  }
  
  
}

