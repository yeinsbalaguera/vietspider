/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl;

import static org.vietspider.common.Application.LAST_DOWNLOAD_SOURCE;

import java.io.File;

import org.vietspider.common.io.LogService;
import org.vietspider.common.io.UtilFile;
import org.vietspider.crawl.link.Link;
import org.vietspider.crawl.link.CrawlingSetup;
import org.vietspider.crawl.queue.NormalCrawlQueue;
import org.vietspider.crawl.queue2.PriorityCrawlQueue;
import org.vietspider.db.SystemProperties;
import org.vietspider.db.link.track.LinkLogStorages;
import org.vietspider.model.Source;
import org.vietspider.pool.CrawlQueue;
import org.vietspider.pool.Session;
import org.vietspider.pool.ThreadPool;
/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 2, 2008  
 */
public final class CrawlingPool extends ThreadPool<String, Link> {
  
  public CrawlingPool() {
    File file = UtilFile.getFile("system", "load");
    if(file.length() < 100*1024l) {
      crawlQueue = CrawlQueue.getInstance(PriorityCrawlQueue.class);
    } else {
      crawlQueue = CrawlQueue.getInstance(NormalCrawlQueue.class);
    }
    /* if(Application.LICENSE == Install.ENTERPRISE) {
      queue = new CrawlFullQueueEntry();
    } else {
      queue = new CrawlSimpleQueueEntry();
    }*/
    CrawlerPoolPing.createInstance();
  }
  
  CrawlQueue<?> getCrawlQueue() { return crawlQueue; }

  public void nextElement(String key, Object...params) {
    CrawlSessionEntry [] entries = crawlQueue.next(key);
    if(entries == null || entries.length < 1) {
      LinkLogStorages.getInstance().entryNull(key);
      return;
    }

    boolean redown = false;
    if(params.length > 0) redown = (Boolean)params[0];
    int index = 0;
    for(Session<String, Link> executor : executors) {
      if(executor.isEndSession()) {
        Source source = CrawlingSources.getInstance().getSource(entries[index].getSourceFullName());
        if(source == null) return;
        source.setRedown(redown);
        newSession((CrawlingSession)executor, entries[index]);
        index++;
        if(index >= entries.length) return;
      }
    }

    if(index >= entries.length) return;

    for(int i = executors.size() - 1; i > -1; i--) {
      executors.get(i).endSession(true);
      Source source = CrawlingSources.getInstance().getSource(entries[index].getSourceFullName());
      if(source == null) return;
      source.setRedown(redown);
      newSession((CrawlingSession)executors.get(i), entries[index]);
      index++;
      if(index >= entries.length) return;
    }
  }

  public void newSession(Session<String, Link> executor) {
    if(pause) return;
    CrawlSessionEntry entry = crawlQueue.nextEntry(executor.getId());
    //queue.nextEntry(executor.getId());
    if(executors == null || entry == null /*|| isExecuting(entry)*/) {
//      LogService.getInstance().setMessage(null, "Create new session fail by entry or executor is null.");
      return;
    }
    //        || isExecuting(entry) /*|| entry.getPingStatus() == CrawlSessionEntry.DEAD*/) return;

    if(!entry.isValidated()) {
//      SourceTrackerService tracker = SourceTrackerService.getInstance();
//      long accessCode = tracker.search(entry.getSource().getFullName().hashCode());
//      SourceQueueValidator validator = SourceQueueValidator.getInstance();
//      if(!validator.validate(entry.getSource(), accessCode)) {
//        LinkLogStorages.getInstance().save(entry.getSource(), "{validate.fail.no.crawl}");
//        return;
//      }
      
      Source source = CrawlingSources.getInstance().getSource(entry.getSourceFullName());
      if(source == null) return;
      if(!crawlQueue.validate(source)) {
        LinkLogStorages.getInstance().save(source, "{validate.fail.no.crawl}");
        return;
      }
    }

    newSession((CrawlingSession)executor, entry);

    SystemProperties.getInstance().putValue(
        LAST_DOWNLOAD_SOURCE, String.valueOf(entry.getPointer()), false);
  }

  public void continueExecutors() {
    if(pause) {
      super.setPause(false);
    } else if(crawlQueue.isSleep()) {
      crawlQueue.wake();
    }
  }

  public boolean isPause() {
    if(super.isPause()) return true;
    return crawlQueue.isEmpty() && crawlQueue.isSleep();
  }

  private void newSession(CrawlingSession executor, CrawlSessionEntry entry) {
    try {
      executor.newSession(entry.getSourceFullName());
    } catch (Throwable e) {
      Source source = CrawlingSources.getInstance().getSource(entry.getSourceFullName());
      if(source == null) {
        LogService.getInstance().setMessage(null, "Create new session fail by source is null");
        return;
      }
      //      if(source != null) {
      LinkLogStorages.getInstance().save(source, e);
      //        LogSource.getInstance().setThrowable(source, e);
      //      } else {
      //      LogWebsite.getInstance().setThrowable(source, e);
      //      }
    }

    CrawlSessionEntry[] entries = CrawlingSetup.duplicateEntries(executor, entry);
    if(entries != null) crawlQueue.appendFirst(entries);
  }
}