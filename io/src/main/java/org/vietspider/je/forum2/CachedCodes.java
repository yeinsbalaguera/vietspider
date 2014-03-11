/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.je.forum2;

import gnu.trove.TIntObjectHashMap;
import gnu.trove.TIntObjectProcedure;

import org.vietspider.db.SystemProperties;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 9, 2009  
 */
public final class CachedCodes {
  
  private final static CachedCodes instance = new CachedCodes();
  
  public final static CachedCodes getInstance() { return instance; }
  
  private TIntObjectHashMap<Entry> map;

  private int max = 100000;
  private long late = -1;
  
  private CachedCodes() {
    String value  = SystemProperties.getInstance().getValue("max.cached.code.post");
    if(value != null) {
      try {
        max = Integer.parseInt(value);
      } catch (Exception e) {
      }
    }
    
    map = new TIntObjectHashMap<Entry>(max/4);
    late = System.currentTimeMillis(); 
  }
  
  public void put(int code, int value) {
    Entry entry = map.get(code);
    if(entry == null) {
      entry = new Entry(value);
      map.put(code, entry);
      return;
    }
    
    entry.value = value;
    entry.access = System.currentTimeMillis();
  }
  
  public int size() { return map.size(); }
  
  public int get(int code) {
    Entry entry = map.get(code);
    if(entry == null) return -1;
    entry.access = System.currentTimeMillis();
    return entry.value;
  }
  
  public void clean() {
    final long current = System.currentTimeMillis();
    if(map.size() <= max) return;
    int time  = 24;
    int unit = 60*60*1000;
    while(time > 1) {
      if(map.size() <= max) break;
      clean(current, time*unit);
      time = time/2;
    }
    
    if(map.size() <= max) return;
    
    time  = 60;
    unit = 60*1000;
    while(time > 1) {
      if(map.size() <= max) break;
      clean(current, time*unit);
      time = time/2;
    }
    
    if(map.size() <= max) return;
    
    time  = 60;
    unit = 1000;
    while(time > 1) {
      if(map.size() <= max) break;
      clean(current, time*unit);
      time = time/2;
    }
  }
  
  private final void clean(final long current, final long timeout) {
//    long timeout = expire*60*60*1000;
    if(current - late < timeout) return;
    /*{
      Calendar calendar = Calendar.getInstance();
      calendar.setTimeInMillis(late);
      System.err.println(" bi late roi roi " + calendar.getTime());
      return;
    }*/

    map.forEachEntry(new TIntObjectProcedure<Entry>() {
      public boolean execute(int key, Entry entry) {
        if(current - entry.access < timeout) return true;
        if(entry.access > late) late = entry.access;
        map.remove(key);
        return true;
      }
    });
  }
  
  public void setMax(int max) { this.max = max; }
  
  private static class Entry {
    
    private int value;
    private long access;
    
    Entry(int value) {
      this.value = value;
      access = System.currentTimeMillis();
    }
    
  }


}
