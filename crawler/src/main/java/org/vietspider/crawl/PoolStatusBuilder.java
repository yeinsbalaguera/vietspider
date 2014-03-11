/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl;

import org.vietspider.pool.CrawlQueue;
import org.vietspider.pool.SourceQueue;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jun 23, 2008  
 */
public final class PoolStatusBuilder {
  
  public String get(CrawlingPool pool) {
    StringBuilder builder = new StringBuilder();
    
    builder.append("Crawler pool status:\n");
//    builder.append("Thread is ");
//    if(pool == null) {
//      builder.append(" null\n");
//    } else {
//      builder.append(pool.isInterrupted() ? " interrupted, " : " not interrupted, ");
//      builder.append(pool.isAlive() ? "alive" : "dead" ).append("\n");
//    }
    
    builder.append("Crawler is ").append(pool.isPause() ? "pause" : "executing").append('\n');
    builder.append("Total of source queue: ").append(pool.getQueueEntry().size()).append('\n');
    builder.append("----------------------------------------------------------------------\n");
    
    builder.append("Source queue status:\n");
    CrawlQueue<?> crawlQueue = CrawlService.getInstance().getThreadPool().getCrawlQueue();
    SourceQueue sourceQueue = crawlQueue.getNormalQueue();
    builder.append("Reader point: ").append(sourceQueue.getPointer());
    builder.append(", load length: ").append(sourceQueue.length()).append('\n');
    builder.append("class : "+sourceQueue.getClass().getName()).append('\n');
    builder.append("Crawl source counter: ").append(sourceQueue.getSourceCounter()).append('\n');
    builder.append("Total of reading cycle: ").append(sourceQueue.getCycleCounter()).append('\n');
    
    builder.append("----------------------------------------------------------------------\n");
    
    builder.append("Working source saver status:\n");
    builder.append("Saver is ").append(crawlQueue.isInterrupted() ? " interrupted, " : " not interrupted, ");
    builder.append(crawlQueue.isAlive() ? "alive" : "dead" ).append(", ").append('\n');
//    builder.append(sourceSaver.isSave() ? "not wait to save" : "wait to save");
//    builder.append(", save point ").append(sourceSaver.getPointer()).append('\n');
    
    builder.append("----------------------------------------------------------------------\n");
    builder.append("Source queue:\n");
    pool.getQueueEntry().buildList(builder);
    /*CrawlSessionEntry [] entries = pool.getQueueEntry().toArray();
    int size = Math.min(entries.length, pool.getExecutors().size() + 10);
    for(int i = 0 ; i < size; i++) {
      CrawlSessionEntry entry = entries[i];
      builder.append(entry.getSource().getFullName());
      builder.append(' ').append('[').append(entry.getPointer()).append(']').append('\n');
    }*/
    
    return builder.toString(); 
  }
}
