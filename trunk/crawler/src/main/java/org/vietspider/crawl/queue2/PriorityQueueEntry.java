/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.queue2;

import java.util.Iterator;
import java.util.concurrent.LinkedBlockingDeque;

import org.vietspider.crawl.CrawlSessionEntry;
import org.vietspider.pool.QueueEntry;
import org.vietspider.pool.SessionEntry;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 3, 2011  
 */
public class PriorityQueueEntry extends QueueEntry<String> {

  public PriorityQueueEntry() {
    queue = new LinkedBlockingDeque<SessionEntry<String>>();
  }

  @SuppressWarnings("all")
  public CrawlSessionEntry nextEntry(int id) {
    Iterator<SessionEntry<String>> iterator = queue.iterator();
    while(iterator.hasNext()) {
      CrawlSessionEntry tempEntry = (CrawlSessionEntry)iterator.next();
      iterator.remove();
      return tempEntry;
    }
    return null ;
  }

  @SuppressWarnings("unchecked")
  public CrawlSessionEntry[] toArray() {
    CrawlSessionEntry [] entries = new CrawlSessionEntry[queue.size()];
    int index = 0;
    Iterator<SessionEntry<String>> iterator = queue.iterator();
    while(iterator.hasNext()) {
      SessionEntry<String> entry = iterator.next();
      entries[index] = (CrawlSessionEntry)entry;
      index++;
    }
    return entries;
  }

  @Override
  public void buildList(StringBuilder builder) {
    Iterator<SessionEntry<String>> iterator = queue.iterator();
    while(iterator.hasNext()) {
      CrawlSessionEntry entry = (CrawlSessionEntry)iterator.next();
      builder.append(entry.getSourceFullName()).append(' ').append('\n');
    }
  }
  
  
}
