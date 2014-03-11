/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.je.page;

import java.io.File;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import org.vietspider.common.io.MD5Hash;
/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 30, 2009  
 */
public class PageSizeCached implements IPageSizeDatabase {
  
  final static int CACHED_SIZE = 10000; 

  private File folder;
  private String name;
  private String type;
  
  private boolean isClose = false;
  
  private ConcurrentHashMap<MD5Hash, Long> codes = new ConcurrentHashMap<MD5Hash, Long>();
  private ConcurrentHashMap<MD5Hash, Long> temp = new ConcurrentHashMap<MD5Hash, Long>();
  private int tempSize = 0;
  
  public PageSizeCached(PageSizeDatabase database,
      File folder, String name, String type) throws Throwable {
    this.name = name;
    this.folder = folder;
    this.type = type;

    database.load(codes);
    database.close();
  }

  public void close() throws Throwable {
    isClose = true;
    write();
  }
  
  public boolean isClose() { 
    return isClose; 
  }
  
  public void update(MD5Hash key, Long value) throws Throwable {
    if(isClose) return;
    temp.put(key, value);
    tempSize++;
  }
  
  public long search(MD5Hash key) throws Throwable {
    if (isClose) return 1;
    Long value  = temp.get(key);
    if(value != null) return value.longValue();
//    System.out.println(" truoc khi search "+ map.size());
    value  = codes.get(key);
////    System.out.println(" get ra duoc "+ new String(code) + " : "+ value);
    if(value == null) return -1;
    return value.longValue();
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
    PageSizeDatabase database = new PageSizeDatabase(folder, name, type, 1024*1024, true);
    Iterator<MD5Hash> iterator = temp.keySet().iterator();
    while(iterator.hasNext()) {
      MD5Hash key = iterator.next();
      Long value = temp.get(key);
      database.update(key, value);
      codes.put(key, value);
      iterator.remove();
    }
    database.commit(false);
    database.close();
  }
  
}

