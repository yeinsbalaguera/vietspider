/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db;

import java.io.File;
import java.util.SortedMap;

import org.vietspider.common.io.MD5Hash;
import org.vietspider.paging.PageIO;
import org.vietspider.paging.PageIOs;

import com.sleepycat.bind.tuple.StringBinding;
import com.sleepycat.collections.StoredSortedMap;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 31, 2009  
 */
public class HashStringDatabase extends JeDatabase {
  
  protected volatile long lastAccess = System.currentTimeMillis();

  protected SortedMap<byte[], String> map;

  protected volatile int counter = 0;

  protected File fileIndex;

  public HashStringDatabase(File folder, String name, long cachedSize) throws Exception {
    EnvironmentConfig envConfig = new EnvironmentConfig();
    envConfig.setTransactional(false);
    envConfig.setAllowCreate(true);

    envConfig.setCacheSize(cachedSize); 
    
    this.env = new Environment(folder, envConfig);
    DatabaseConfig dbConfig = new DatabaseConfig();
    dbConfig.setTransactional(false);
    dbConfig.setAllowCreate(true);
    dbConfig.setDeferredWrite(true);
    this.db = env.openDatabase(null, name, dbConfig);
    Md5Binding keyBinding = new Md5Binding();
    StringBinding dataBinding = new StringBinding();
    this.map = new StoredSortedMap<byte[], String> (db, keyBinding, dataBinding, true);

    fileIndex = new File(folder, folder.getName() + "." + name + ".idx");
  }
  
  public File getFileIndex() { return fileIndex; }
  
  public final String search(MD5Hash key) {
    if (isClose) return null;
    lastAccess = System.currentTimeMillis();
    return map.get(key.getDigest());
  }
  
  public final void remove(MD5Hash key) throws Throwable {
    if (isClose) return;
    lastAccess = System.currentTimeMillis();
    String xml = map.remove(key.getDigest());
    if(xml == null) return;
//    System.out.println(key.toString()+  " : "+ xml);
//      System.out.println(fileIndex.getPath()+  " : "+ xml.length());
////      System.out.println(" prepare remove host "+ xml);
//    }
    Md5HashEntry entry = new Md5HashEntry(key);
    entry.setStatus(Md5HashEntry.DELETE);
    PageIOs.getInstance().write(fileIndex, entry);
    
//    counter++;
//    if(counter < 50) return;
//    db.sync();
//    counter = 0;
  }

  public long lastAccess() { return lastAccess; }
  
  public int getTotalPage(int pageSize) {
    PageIO<Md5HashEntry> pageIO = getPageIO();
    return pageIO.getTotalPage(pageSize);
  }

  @SuppressWarnings("unchecked")
  protected PageIO<Md5HashEntry>  getPageIO() {
    PageIO<Md5HashEntry> io = (PageIO<Md5HashEntry>)PageIOs.getInstance().getPageIO(fileIndex);
    if(io != null) return io;
    io = new PageIO<Md5HashEntry>() {
      public Md5HashEntry createEntry() {
        return new Md5HashEntry();
      }
    };
    io.setData(fileIndex, MD5Hash.DATA_LENGTH);
    PageIOs.getInstance().putPageIO(fileIndex, io);
    return io;
  }
}
