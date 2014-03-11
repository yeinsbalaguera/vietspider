/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.queue;

import org.vietspider.crawl.CrawlSessionEntry;
import org.vietspider.crawl.io.access.SourceTrackerService;
import org.vietspider.model.Source;
import org.vietspider.pool.CrawlQueue;
import org.vietspider.pool.QueueEntry;
import org.vietspider.pool.SourceQueue;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 2, 2011  
 */
public class NormalCrawlQueue extends CrawlQueue<String> {
  
  protected QueueEntry<String> queue;
  
  public NormalCrawlQueue() {
    queue = new CrawlFullQueueEntry();
  }

  @Override
  public boolean validate(Source source) {
    SourceTrackerService tracker = SourceTrackerService.getInstance();
    long accessCode = tracker.search(source);
    SourceQueueValidator validator = SourceQueueValidator.getInstance();
    return validator.validate(source, accessCode);
  }
  
  public void removeElement(String key) {
    queue.removeElement(key);
  }
  
  public void setCapacity(int capacity) {
    queue.setCapacity(capacity);
  }
  
  public QueueEntry<String> getQueueEntry() { return queue; }
  
  public SourceQueue getNormalQueue() {
    return SourceQueueManager.getInstance().getNormalQueue();
  }
  
  public CrawlSessionEntry nextEntry(int id) { return queue.nextEntry(id); }
  
  public boolean isEmpty() { return queue.isEmpty(); }
  
  public void appendFirst(CrawlSessionEntry [] entries) { queue.appendFirst(entries); }
  
  public CrawlSessionEntry [] next(String key) {
    return SourceQueueManager.getInstance().getNormalQueue().next(key);
  }

  public boolean isSleep() { return SourceQueueManager.getInstance().isSleep(); }

  public void wake() { SourceQueueManager.getInstance().wake(); }

  public boolean isInterrupted() {
    return SourceQueueManager.getInstance().isInterrupted();
  }

  public boolean isAlive() {
    return SourceQueueManager.getInstance().isAlive();
  }
  
  
  
}
