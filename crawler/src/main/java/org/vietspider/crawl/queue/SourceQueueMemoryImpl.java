/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.queue;

import static org.vietspider.common.Application.LAST_DOWNLOAD_SOURCE;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.crawl.CrawlingPool;
import org.vietspider.crawl.CrawlService;
import org.vietspider.crawl.CrawlSessionEntry;
import org.vietspider.crawl.CrawlingSources;
import org.vietspider.db.SystemProperties;
import org.vietspider.model.Source;
import org.vietspider.pool.CrawlQueue;
import org.vietspider.pool.QueueEntry;
import org.vietspider.pool.SourceQueue;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 5, 2009  
 */
class SourceQueueMemoryImpl implements SourceQueue {

  protected volatile int index = 0;

  private int cycleCounter = 0;

  private String [] lines;

  private File file;
  private long lastModified = -1;

  public SourceQueueMemoryImpl() {
    SystemProperties system = SystemProperties.getInstance();
    index = Integer.parseInt(system.getValue(LAST_DOWNLOAD_SOURCE));

    file = UtilFile.getFile("system", "load");
    lastModified = file.lastModified();

    try {
      String text = new String(RWData.getInstance().load(file), Application.CHARSET);
      lines = CrawlQueue.split(text);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
      lines = new String[0];
    }

    if(index > lines.length) index = 0;

    //    System.out.println(" thay co "+ pointer+ " : "+ file.length());
  }

  public synchronized CrawlSessionEntry [] next(String line) {
    if(line == null) return null;
    SourceQueueEntryReader reader = new SourceQueueEntryReader();
    return reader.createSessionEntry(line);
  }

  public boolean next() {
    if(file.lastModified() != lastModified) {
      //      System.out.println(" da thay thay doi "+ file.lastModified() + " / "+ lastModified);
      lastModified = file.lastModified();
      try {
        String text = new String(RWData.getInstance().load(file), Application.CHARSET);
        lines = CrawlQueue.split(text);
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
        lines = new String[0];
      }
    }
    CrawlingPool pool = CrawlService.getInstance().getThreadPool();
    if(pool == null || pool.getExecutors() == null) return false;
    //    int size = pool.getExecutors().size() + 10;
    QueueEntry<String> queue = pool.getQueueEntry();
    if(queue == null  || queue.isFull()) return false;

    if(index < 0 || index >= lines.length)  index = 0;

    SourceQueueEntryReader reader = new SourceQueueEntryReader();

    while(!queue.isFull()) {
      if(index >= lines.length) break;

      try {
        validateLicense();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
        break;
      }

      if(index < 1) cycleCounter++;

      //        String line = readLine(null);
      SystemProperties.getInstance().putValue(
          LAST_DOWNLOAD_SOURCE, String.valueOf(index), false);
      //        if(line == null) continue;

      //        if(pool.isExecuting(line)) continue;
      CrawlSessionEntry [] entries = reader.createSessionEntry(lines[index]);
      index++;
      if(entries == null) continue;

      for(CrawlSessionEntry entry : entries) {
        if(entry == null || entry.getSourceFullName() == null) continue;
        Source source = CrawlingSources.getInstance().getSource(entry.getSourceFullName());
        String [] addresses = source.getHome();
        if(addresses == null || addresses.length < 1) continue;
        entry.setPointer(index*16);
        queue.add(entry);
      }
    }

    return index >= lines.length;
  }

  public long getPointer() { return index; }

  public long length() { return lines.length; }

  public int getSourceCounter() { return sourceCounter; }

  public long getCycleCounter() { return cycleCounter; }

  @Override()
  @SuppressWarnings("unused")
  public synchronized String readLine(RandomAccessFile random) throws IOException {
    return null;
  }
  
  /*  if(index >= lines.length) return null;
    int start = text.indexOf('\n', index);
    if(start < -1) {
      index = text.length();
      return null;
    }
    int end = text.indexOf('\n', start+2);
    if(end < -1) {
      index = text.length();
      return null;
    }

    if(start + 1 >= end) {
      index = start+1;
      return null;
    }
//    String line = lines[index]// text.substring(start+1, end).trim();
    //    LogService.getInstance().setMessage(null, line);
//    index = end;
    //    System.out.println("thay co "+ line + " / "+ index);
//    if(line == null) return null;
    StringBuilder builder = new StringBuilder();
    String [] elements = lines[index].split("\\.");
    if(elements.length < 3) return null;
    builder.append(elements[1]).append('.').append(elements[2]).append('.').append(elements[0]);
    return builder.toString();
  }*/

  private volatile int sourceCounter = 0;

  private void validateLicense() throws Exception {
    switch (Application.LICENSE) {
    case PERSONAL:
      sourceCounter++;
      if(sourceCounter < 50) return ;
      sourceCounter = 0;
      index = 0;
      return;
    case PROFESSIONAL:
      sourceCounter++;
      if(sourceCounter < 100) return;
      sourceCounter = 0;
      index = 0;
      return;
    case ENTERPRISE:
      sourceCounter++;
      if(sourceCounter < 1000) return;
      sourceCounter = 0;
      index = 0;
      return;
    default:
      break;
    }
  }


}