/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.je.forum2;

import java.util.concurrent.ConcurrentHashMap;

import org.vietspider.common.io.LogService;

import com.sleepycat.je.DeadlockException;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 25, 2007  
 */
final class ForumTrackerDatabase extends CodeDatabases {

  private final static int MAX_TEMP_SIZE = 1000;//10000;
//private final static int MAX_CACHED_COLLECTION = 1000000/MAX_TEMP_SIZE; 

  protected ConcurrentHashMap<Integer, Integer> codes;
  protected volatile int tempSize = 0;
  
  protected volatile long lastAccess = System.currentTimeMillis();
  
  protected String source;
  
  ForumTrackerDatabase(String sourceName) throws Exception {
    super("track/private/forum/post_db/" + sourceName, "post_forum", 5*24*60*60*1000);
    this.source = sourceName;
    codes = new ConcurrentHashMap<Integer, Integer>();
  }

  public void writeTemp() {
    try {
      if(tempSize >= MAX_TEMP_SIZE) {
        current.commit(codes);
      }
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }

    try {
      nextDatabase();      
    } catch (DeadlockException e) {
      LogService.getInstance().setMessage(e, e.getMessage());
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }
  
  public void close() {
    isClose = true;
    
//    System.out.println(" chuan bi "+ codes.size());
    current.commit(codes);
    
    try {
      current.close();
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    
    for(int i = 0; i < databases.size(); i++) {
      try {
        databases.get(i).close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
  }

  public void write(int addressCode, int post) {
//    System.out.println("===== > write " + source + " : " + addressCode+ " post ");
    codes.put(addressCode, post);
    tempSize++;
  }

  public int read(int addressCode) {
//    System.out.println("===== > read " + source + " : " + addressCode);
    Integer cacheValue = codes.get(addressCode);
    if(cacheValue != null) return cacheValue.intValue();

    int value = current.search(addressCode);
    if(value > -1) return value;

//    System.out.println("===== > "+databases.size());
    for(int i = 0; i < databases.size(); i++) {
//      System.out.println(" doc den database thu i "+ databases.get(i).getName() + " : "+ i);
      if(databases.get(i) == current) continue;
      value = databases.get(i).search(addressCode);
      if(value < 0) continue;
      codes.put(addressCode, value);
      tempSize++;
      return value;
    }

    return -1;
  }

  public long getLastAccess() { return lastAccess; }

  public void setLastAccess(long lastAccess) { this.lastAccess = lastAccess; }

  public String getSource() { return source; }
  
}
