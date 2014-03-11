/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.common.io;

import java.io.File;
import java.nio.channels.FileChannel;
import java.util.List;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 7, 2009  
 */
public interface ConcurrentSetInt  {
  
  public void add(int value) ;
  
  public boolean contains(int value);
  
  public boolean isEmpty() ;

  public int size() ;

  public void clear();

  public void write(FileChannel channel)  throws Exception ;
  
  public void loadFile(File file) ;
  
  public void addToList(List<Integer> list);
}
