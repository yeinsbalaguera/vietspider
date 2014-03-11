/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.pool;

import java.io.IOException;
import java.io.RandomAccessFile;

import org.vietspider.crawl.CrawlSessionEntry;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 19, 2007  
 */
public interface SourceQueue  {
  
  public CrawlSessionEntry [] next(String line) ;
  
  long getPointer() ;
  
  long length();

  int getSourceCounter() ;

  long getCycleCounter() ;
  
  boolean next() ;
  
  public String readLine(RandomAccessFile random) throws IOException ;
  
}
