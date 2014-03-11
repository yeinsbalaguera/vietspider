/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.je.codes;

import java.io.File;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import org.vietspider.common.io.MD5Hash;
/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 30, 2009  
 */
public class Md5UrlCached implements IUrlDatabase {
  
  static int CACHED_SIZE = 10000; 

  private File folder;

  private String name;
  private String type;
  
  private boolean isClose = false;
  
  private ConcurrentHashMap<MD5Hash, Integer> codes = new ConcurrentHashMap<MD5Hash, Integer>();
  private ConcurrentHashMap<MD5Hash, Integer> temp = new ConcurrentHashMap<MD5Hash, Integer>();
  private int tempSize = 0;
  
  public Md5UrlCached(Md5UrlDatabase database,
      File folder, String name, String type) throws Throwable {
    this.name = name;
    this.folder = folder;
    this.type = type;

    database.load(codes);
    database.close();
  }
  
  File getFolder() { return folder; }
  
  void reopen() { isClose = false; }

  public void close() throws Throwable {
    write();
    isClose = true;
  }
  
  public boolean isClose() { 
    return isClose; 
  }
  
  public void save(MD5Hash key, Integer value) throws Throwable {
    if(isClose) return;
    temp.put(key, value);
    tempSize++;
  }
  
  public int search(MD5Hash key) throws Throwable {
    if (isClose) return 1;
    Integer value  = temp.get(key);
    if(value != null) return value.intValue();
//    System.out.println(" truoc khi search "+ map.size());
    value  = codes.get(key);
////    System.out.println(" get ra duoc "+ new String(code) + " : "+ value);
    if(value == null) return -1;
    return value.intValue();
  }
  
  public void remove(MD5Hash key) throws Throwable {
    if (isClose) return ;
    temp.remove(key);
    codes.remove(key);
    tempSize++;
  }

  public String getName() { return name; }
  
  public long size() {
    if (isClose) return 0;
    return codes.size(); 
  }

  public void commit(boolean check) throws Throwable {
    if(check && tempSize < 500) return;
    tempSize = 0;
    if(codes.size() >= CACHED_SIZE) isClose = true;
    write();
  }
  
  private void write() throws Throwable{
    if(temp.isEmpty()) return;
//    System.out.println(" thay modified va "+ modifiedCounter + " : "+ temp.size() );
    Md5UrlDatabase database = new Md5UrlDatabase(folder, name, type, 1024*1024, true);
    Iterator<MD5Hash> iterator = temp.keySet().iterator();
    while(iterator.hasNext()) {
      MD5Hash key = iterator.next();
      Integer value = temp.get(key);
      database.save(key, value);
      codes.put(key, value);
      iterator.remove();
    }
    database.commit(false);
    database.close();
  }
  
}

