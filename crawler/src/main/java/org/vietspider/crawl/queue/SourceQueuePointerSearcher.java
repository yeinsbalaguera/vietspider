/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.queue;

import static org.vietspider.common.Application.LAST_DOWNLOAD_SOURCE;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RandomAccess;
import org.vietspider.common.io.UtilFile;
import org.vietspider.db.CrawlerConfig;
import org.vietspider.db.SystemProperties;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 25, 2008  
 */
final class SourceQueuePointerSearcher extends RandomAccess {
  
  long search() {
    SystemProperties system = SystemProperties.getInstance();
    RandomAccessFile random = null;
    try {
      int  totalExecutor = CrawlerConfig.TOTAL_EXECUTOR;
      long pointer = Long.parseLong(system.getValue(LAST_DOWNLOAD_SOURCE));

      File file = UtilFile.getFile("system", "load");
      random = new RandomAccessFile(file, "r");
      if(pointer > random.length()) pointer = random.length();

      long value = search(random, pointer, totalExecutor, true);
      random.close();
      return value;
    } catch (IOException e) {
      LogService.getInstance().setThrowable(e);
    } finally {
      try {
        if(random != null) random.close();
      } catch (Exception e2) {
        LogService.getInstance().setThrowable(e2);
      }
    }
    return 0;
  }
  
  protected long search(RandomAccessFile random, long start, int counter, boolean next) throws IOException {
    start--;
    while (start >= 0) {
      random.seek(start);
      switch (random.read()) {
      case -1:
      case '\n':
        counter--;
        break;
      case '\r':
        counter--;
        break;
      default:        
        break;
      }
      if(counter <= 0) break;
      start--;
    }
    if(start < 0 && counter > 0 && next) {
      return search(random, random.length(), counter, false);
    }
    return start;
  }
  
}
