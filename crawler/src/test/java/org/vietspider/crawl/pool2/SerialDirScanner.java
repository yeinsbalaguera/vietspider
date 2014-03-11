/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.pool2;

import java.io.File;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 17, 2009  
 */
public class SerialDirScanner {
  
  private final ConcurrentMap<File, Boolean> files = new ConcurrentHashMap<File, Boolean>();

  public SerialDirScanner() {
    super();
  }

  public Set<File> scan(File dir) throws InterruptedException, ExecutionException {
    scan0(dir);
    return files.keySet();
  }

  private void scan0(File dir) {
    for (final File file : dir.listFiles()) {
      files.putIfAbsent(file, Boolean.TRUE);
      if (file.isDirectory()) {
        scan0(file);
      }
    }
  } 
  public static Set<File> listAllContentsUnder(File dir) throws InterruptedException, ExecutionException {
    SerialDirScanner scanner = new SerialDirScanner();
    return scanner.scan(dir);
  }
}