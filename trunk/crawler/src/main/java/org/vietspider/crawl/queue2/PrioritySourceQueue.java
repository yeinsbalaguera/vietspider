/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.queue2;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.LinkedBlockingDeque;

import org.vietspider.crawl.CrawlSessionEntry;
import org.vietspider.crawl.queue.SourceQueueEntryReader;
import org.vietspider.model.Source;
import org.vietspider.pool.SourceQueue;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 3, 2011  
 */
public class PrioritySourceQueue implements SourceQueue {

  protected volatile LinkedBlockingDeque<Source> queue;
  
  public PrioritySourceQueue() {
    queue = new LinkedBlockingDeque<Source>();
  }

  public CrawlSessionEntry[] next(String line) {
    if(line == null) return null;
    SourceQueueEntryReader reader = new SourceQueueEntryReader();
    return reader.createSessionEntry(line);
  }

  public long getPointer() { return 0; }

  public long length() { return 1; }

  public int getSourceCounter() { return queue.size(); }

  public long getCycleCounter() { return 1; }

  public boolean next() { return true; }

  public String readLine(RandomAccessFile random) throws IOException {
    return null;
  }
  
  
}
