/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.queue;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RandomAccess;
import org.vietspider.common.io.UtilFile;
import org.vietspider.crawl.CrawlingPool;
import org.vietspider.crawl.CrawlService;
import org.vietspider.crawl.CrawlSessionEntry;
import org.vietspider.crawl.CrawlingSources;
import org.vietspider.model.Source;
import org.vietspider.pool.QueueEntry;
import org.vietspider.pool.SourceQueue;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 19, 2007  
 */
final class SourceQueueFileImpl extends RandomAccess implements SourceQueue {
  
  protected long pointer = 0;
  
  private int cycleCounter = 0;
  
  public SourceQueueFileImpl() {
    pointer = new SourceQueuePointerSearcher().search();
    
    File file = UtilFile.getFile("system", "load");
    if(pointer >= file.length()-1) pointer = 0;
    
//    System.out.println(" thay co "+ pointer+ " : "+ file.length());
  }
  
  public synchronized CrawlSessionEntry [] next(String line) {
    if(line == null) return null;
    SourceQueueEntryReader reader = new SourceQueueEntryReader();
    return reader.createSessionEntry(line);
  }
  
  public boolean next() {
    CrawlingPool pool = CrawlService.getInstance().getThreadPool();
    if(pool == null || pool.getExecutors() == null) return false;
//    int size = pool.getExecutors().size() + 10;
    QueueEntry<String> queue = pool.getQueueEntry();
    if(queue == null  || queue.isFull()) return false;
    
    RandomAccessFile random = null;
    long length = 0;
    try {
      File file = UtilFile.getFile("system", "load");
      random = new RandomAccessFile(file, "r");
      length = random.length();
      
      if(pointer < 0 || pointer >= length)  pointer = 0;
      
      random.seek(pointer);
      SourceQueueEntryReader reader = new SourceQueueEntryReader();
      
      while(!queue.isFull()) {
        if(pointer >= length) break;
        
        try {
          validateLicense(random);
        } catch (Exception e) {
          LogService.getInstance().setThrowable(e);
          break;
        }
        
        pointer = random.getFilePointer();
        
        if(pointer < 1) cycleCounter++;
        
        String line = readLine(random);
        if(line == null) continue;
        
//        if(pool.isExecuting(line)) continue;
        CrawlSessionEntry [] entries = reader.createSessionEntry(line);
        if(entries == null) continue;
        
        for(CrawlSessionEntry entry : entries) {
          if(entry == null || entry.getSourceFullName() == null) continue;
          Source source = CrawlingSources.getInstance().getSource(entry.getSourceFullName());
          if(source == null) continue;
          String [] addresses = source.getHome();
          if(addresses == null || addresses.length < 1) continue;
          entry.setPointer(pointer);
          queue.add(entry);
        }
      }
      
      pointer = random.getFilePointer();
      random.close();
    } catch (IOException e) {
      LogService.getInstance().setThrowable(e);
    } finally {
      try {
        if(random != null) random.close();
      } catch (Exception e2) {
        LogService.getInstance().setThrowable(e2);
      }
    }
    
    return pointer >= length;
  }
  
  public long getPointer() { return pointer; }
  
  public long length() {
    File file = UtilFile.getFile("system", "load");
    return file.length(); 
  }

  public int getSourceCounter() { return sourceCounter; }

  public long getCycleCounter() { return cycleCounter; }
  
  @Override()
  public String readLine(RandomAccessFile random) throws IOException {
    String line = super.readLine(random);
    if(line == null) return null;
    StringBuilder builder = new StringBuilder();
    String [] elements = line.split("\\.");
    if(elements.length < 3) return null;
    builder.append(elements[1]).append('.').append(elements[2]).append('.').append(elements[0]);
    return builder.toString();
  }
  
  private volatile int sourceCounter = 0;
  
  private void validateLicense(RandomAccessFile random) throws Exception {
    switch (Application.LICENSE) {
    case PERSONAL:
      sourceCounter++;
      if(sourceCounter < 50) return;
      sourceCounter = 0;
      pointer = 0;
      random.seek(0);
      return;
    case PROFESSIONAL:
      sourceCounter++;
      if(sourceCounter < 100) return;
      sourceCounter = 0;
      pointer = 0;
      random.seek(pointer);
      return;
    case ENTERPRISE:
      sourceCounter++;
      if(sourceCounter < 1000) return;
      sourceCounter = 0;
      pointer = 0;
      random.seek(pointer);
      return;
    default:
      break;
    }
  }

}
