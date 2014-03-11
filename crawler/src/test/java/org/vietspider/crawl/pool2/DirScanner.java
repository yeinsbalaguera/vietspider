/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.pool2;

import java.io.File;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 17, 2009  
 */
public class DirScanner {
  
  private final ConcurrentMap<File, Boolean> files = new ConcurrentHashMap<File, Boolean>();
  private final ExecutorService executor;
  private final Semaphore sem = new Semaphore(1);
  private final AtomicInteger count = new AtomicInteger();

  public DirScanner(int threads) {
    super();
    executor = Executors.newFixedThreadPool(threads);
  }

  public Set<File> scan(File dir) throws InterruptedException, ExecutionException {
    sem.acquire();
    scan0(dir);
    sem.acquire();
    return files.keySet();
  }

  private void scan0(File dir) {
    for (final File file : dir.listFiles()) {
      files.putIfAbsent(file, Boolean.TRUE);
      if (file.isDirectory()) {
        count.incrementAndGet();
        executor.submit(new Runnable() {
          public void run() {
            DirScanner.this.scan0(file);
          }
        });
      }
    }
    if (count.decrementAndGet() < 0) {
      sem.release();
    }
  }
  public void close(){
    executor.shutdown();
  }
  public static Set<File> listAllContentsUnder(File dir, int threads) throws InterruptedException, ExecutionException {
    DirScanner scanner = new DirScanner(threads);
    return scanner.scan(dir);
  } 
}
