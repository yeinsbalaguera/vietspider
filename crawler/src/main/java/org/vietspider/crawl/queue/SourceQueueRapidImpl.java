/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.queue;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RandomAccess;
import org.vietspider.common.io.UtilFile;
import org.vietspider.crawl.CrawlingPool;
import org.vietspider.crawl.CrawlService;
import org.vietspider.crawl.CrawlSessionEntry;
import org.vietspider.crawl.CrawlingSources;
import org.vietspider.model.Source;
import org.vietspider.pool.SourceQueue;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 19, 2007  
 */
final class SourceQueueRapidImpl extends RandomAccess implements SourceQueue {
  
  private int index = 0;
  private List<String> lines = new ArrayList<String>();
  
  private File file;
  private long lastModified = -1;
  
  public SourceQueueRapidImpl() {
    file = UtilFile.getFile("system", "load");
    if(validateLicense()) load();
//    System.out.println(" thay co "+ pointer+ " : "+ file.length());
  }
  
  public synchronized CrawlSessionEntry [] next(String line) {
    if(line == null) return null;
    SourceQueueEntryReader reader = new SourceQueueEntryReader();
    return reader.createSessionEntry(line);
  }
  
  public boolean next() {
    if(file.lastModified() != lastModified) load();
    
    CrawlingPool pool = CrawlService.getInstance().getThreadPool();
    if(pool == null || pool.getExecutors() == null) return false;
//    int size = pool.getExecutors().size() + 10;
    CrawlFullQueueEntry queue = (CrawlFullQueueEntry )pool.getQueueEntry();
    if(queue == null  || queue.isRapidFull()) return false;
    
    SourceQueueEntryReader reader = new SourceQueueEntryReader();
    while(!queue.isRapidFull()) {
      if(index >= lines.size()) {
        index = 0;
        break;
      }
      
      String line  = lines.get(index);
      index++;
      
      CrawlSessionEntry [] entries = reader.createSessionEntry(line);
      if(entries == null) continue;
      
      for(CrawlSessionEntry entry : entries) {
        if(entry == null || entry.getSourceFullName() == null) continue;
        Source source = CrawlingSources.getInstance().getSource(entry.getSourceFullName());
        String [] addresses = source.getHome();
        if(addresses == null || addresses.length < 1) continue;
        entry.setPointer(index);
        queue.addRapid(entry);
      }
    }
    return false;
  }
  
  private void load() {
    lastModified = file.lastModified();
//    int size = pool.getExecutors().size() + 10;
    RandomAccessFile random = null;
    long length = 0;
    long pointer = 0;
    try {
      random = new RandomAccessFile(file, "r");
      length = random.length();
      
      random.seek(pointer);
      SourceQueueEntryReader reader = new SourceQueueEntryReader();
      
      while(pointer < length) {
        String line = readLine(random);
        if(line == null) {
          pointer = random.getFilePointer();
          continue;
        }
        
//        if(pool.isExecuting(line)) continue;
        CrawlSessionEntry [] entries = reader.createSessionEntry(line);
        if(entries == null || entries.length < 1) {
          pointer = random.getFilePointer();
          continue;
        }
        
        Source source = CrawlingSources.getInstance().getSource(entries[0].getSourceFullName());
        if(source.getPriority() == 0 
            || source.getPriority() == 1) lines.add(line);
        pointer = random.getFilePointer();
      }
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
  }
  
  public long getPointer() { return index; }
  
  public long length() { return lines.size(); }

  public int getSourceCounter() { return 0; }

  public long getCycleCounter() { return 0; }
  
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
  
  
  private boolean validateLicense() {
    switch (Application.LICENSE) {
    case PERSONAL:
      return false;
    case PROFESSIONAL:
      return false;
    case ENTERPRISE:
      return true;
    default:
      return true;
    }
  }
  

}
