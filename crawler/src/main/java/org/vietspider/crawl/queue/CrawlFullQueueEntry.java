/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.queue;

import java.util.Iterator;
import java.util.concurrent.LinkedBlockingDeque;

import org.vietspider.common.io.LogService;
import org.vietspider.crawl.CrawlService;
import org.vietspider.crawl.CrawlSessionEntry;
import org.vietspider.db.CrawlerConfig;
import org.vietspider.db.SystemProperties;
import org.vietspider.pool.QueueEntry;
import org.vietspider.pool.SessionEntry;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 6, 2009  
 */
class CrawlFullQueueEntry extends QueueEntry<String> {

  protected volatile LinkedBlockingDeque<SessionEntry<String>> rapidQueue;
  protected volatile int max = 3;

  CrawlFullQueueEntry() {
    SystemProperties properties = SystemProperties.getInstance();
    String value = properties.getValue("crawler.high.executor");
    if(value != null && (value = value.trim()).length() > 0) {
      try {
        max  = Integer.parseInt(value);
        rapidQueue = new LinkedBlockingDeque<SessionEntry<String>>();
        capacity = max + 10;
      } catch (Exception e) {
        LogService.getInstance().setMessage(e, "crawler.high.executor is invalid value!");
      }
    }
    if(rapidQueue != null) return;
    
    if(CrawlerConfig.TOTAL_EXECUTOR < 5) {
      max = 0;
    } else if(CrawlerConfig.TOTAL_EXECUTOR >= 5 
        && CrawlerConfig.TOTAL_EXECUTOR < 9) {
      rapidQueue = new LinkedBlockingDeque<SessionEntry<String>>();
      max = 2;
    } else if(CrawlerConfig.TOTAL_EXECUTOR >= 9
        && CrawlerConfig.TOTAL_EXECUTOR < 15) {
      rapidQueue = new LinkedBlockingDeque<SessionEntry<String>>();
      max = 3;
    } else if(CrawlerConfig.TOTAL_EXECUTOR >= 15 
        && CrawlerConfig.TOTAL_EXECUTOR < 20) {
      rapidQueue = new LinkedBlockingDeque<SessionEntry<String>>();
      max = 4;
    }  else if(CrawlerConfig.TOTAL_EXECUTOR >= 20 
        && CrawlerConfig.TOTAL_EXECUTOR < 30) {
      rapidQueue = new LinkedBlockingDeque<SessionEntry<String>>();
      max = 5;
    }  else {
      rapidQueue = new LinkedBlockingDeque<SessionEntry<String>>();
      max = 7;
    }
    capacity = max + 10;
  }

  @SuppressWarnings("unchecked")
  public CrawlSessionEntry nextEntry(int id) {
//    LogService.getInstance().setMessage(null, " ===  >" + id + "  / "+ max+ "  / "+ rapidQueue);
//        System.out.println(" ===  >" + id + "  / "+ max);
    Iterator<SessionEntry<String>> iterator =  null;
    if(id < max && rapidQueue != null) {
      iterator = rapidQueue.iterator();
      while(iterator.hasNext()) {
        CrawlSessionEntry tempEntry = (CrawlSessionEntry)iterator.next();
        
        String key = tempEntry.getSourceFullName();
        
        if(CrawlService.getInstance() == null || 
            CrawlService.getInstance().getThreadPool() == null) return null;
        if(CrawlService.getInstance().getThreadPool().countExecutor(key) > 3) continue;
//              System.out.println(" thay co "
//                  + tempEntry.getSource().getFullName() 
//                  + " === >  " + tempEntry.getSource().getPriority());
        iterator.remove();
        return tempEntry;
      }
      if(max > 10) return null;
    }

    iterator = queue.iterator();
    while(iterator.hasNext()) {
      CrawlSessionEntry tempEntry = (CrawlSessionEntry)iterator.next();
      iterator.remove();
      return tempEntry;
    }
    return null ;
  }

  @SuppressWarnings("unchecked")
  public CrawlSessionEntry [] toArray() {
    if(rapidQueue == null) {
      return queue.toArray(new CrawlSessionEntry[queue.size()]);
    }
    CrawlSessionEntry [] entries = new CrawlSessionEntry[queue.size() + rapidQueue.size()];
    int index = 0;
    Iterator<SessionEntry<String>> iterator = queue.iterator();
    while(iterator.hasNext()) {
      SessionEntry<String> entry = iterator.next();
      entries[index] = (CrawlSessionEntry)entry;
      index++;
    }
    
    iterator = rapidQueue.iterator();
    while(iterator.hasNext()) {
      SessionEntry<String> entry = iterator.next();
      entries[index] = (CrawlSessionEntry)entry;
      index++;
    }
    return entries;
  }

  public void addRapid(SessionEntry<String> entry) { 
    if(rapidQueue == null) return;
    rapidQueue.add(entry); 
  }

  public boolean isRapidFull() {
    if(rapidQueue == null) return true;
    return rapidQueue.size() >= capacity;
  }
  
  public void buildList(StringBuilder builder) {
    Iterator<SessionEntry<String>> iterator = queue.iterator();
    while(iterator.hasNext()) {
      CrawlSessionEntry entry = (CrawlSessionEntry)iterator.next();
      builder.append(entry.getSourceFullName()).append(' ').append('\n');
    }
    if(rapidQueue == null) return;
    builder.append("\n\n");
    
    iterator = rapidQueue.iterator();
    while(iterator.hasNext()) {
      CrawlSessionEntry entry = (CrawlSessionEntry)iterator.next();
      builder.append(entry.getSourceFullName()).append(' ').append('\n');
    }
  }
  
}
