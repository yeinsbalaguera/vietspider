/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl;

import java.net.URL;

import org.vietspider.model.Source;
import org.vietspider.pool.SessionEntry;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 6, 2008  
 */
final public class CrawlSessionEntry extends SessionEntry<String> {

  private volatile long pointer = -1;
  private volatile boolean validated = false;
  private volatile long loadTime = -1;
  
  private String sourceFullName;
  
  public CrawlSessionEntry(String sourceFullName, boolean validate) {
    this.sourceFullName = sourceFullName;
    this.validated = validate;
    loadTime = System.currentTimeMillis();
  }

  public long getPointer() { return pointer; }
  
  public void setPointer(long pointer) { this.pointer = pointer; }
  
  boolean isValidated() { return validated; }

  public long getLoadTime() { return loadTime; }

  public String getSourceFullName() { return sourceFullName; }
  
  public boolean equalsKey(String key) { return sourceFullName.equals(key); }
  
  public boolean equalsValue(String name) {
    if(name == null) return false;
    Source value = CrawlingSources.getInstance().getSource(name);
    if(value == null) return true;
    CrawlingPool pool = CrawlService.getInstance().getThreadPool();
    if(pool == null || pool.getExecutors() == null) return true;
    if(pool.getExecutors().size() < 10) {
      try {
        Source source = CrawlingSources.getInstance().getSource(sourceFullName);
        String [] home = source.getHome();
        if(home ==  null || home.length < 1) return true;
        
        String [] otherHome = value.getHome();
        if(otherHome ==  null || otherHome.length < 1) return false;
        
        URL url = new URL(home[0]);
        URL otherUrl = new URL(otherHome[0]);
        
        return url.getHost().equals(otherUrl.getHost());
      } catch (Exception e) {
        return false;
      }
    }
    
    if(!sourceFullName.equals(value.getFullName())) return false;
    Source source = CrawlingSources.getInstance().getSource(sourceFullName);
    String [] addresses = source.getHome();
    String [] addresses1 = value.getHome();
    if(addresses.length != addresses1.length) return false;
    if(compare(addresses, addresses1)) return true;
    return false;
  }

  private boolean compare(String [] addresses1, String [] addresses2) {
    for(int i = 0; i < addresses1.length; i++) {
      if(addresses1[i] == null) {
        if(addresses2[i] == null) continue;
        if(addresses2[i] != null) return false; 
      }
      if(!addresses1[i].equals(addresses2[i])) return false;
    }  
    return true;
  }
  
}
