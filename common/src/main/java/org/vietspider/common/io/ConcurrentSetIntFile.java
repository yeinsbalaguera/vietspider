/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.common.io;

import java.io.File;
import java.nio.channels.FileChannel;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 9, 2009  
 */
public class ConcurrentSetIntFile implements ConcurrentSetInt {
  
  private volatile ConcurrentSkipListSet<Integer> data;
  private volatile int size = 0;

  public ConcurrentSetIntFile() {
    data = new ConcurrentSkipListSet<Integer>();
  }

  public void add(int value) {
    if(data.add(value)) size++;
  }

  public boolean contains(int value) {
    return data.contains(value);
  }

  public boolean isEmpty() {
    return  data.isEmpty();
  }

  public int size() {
    return size;
  }

  public void clear() {
    data.clear();
  }

  @SuppressWarnings("unused")
  public void write(FileChannel channel)  throws Exception {
    throw new UnsupportedOperationException("unsupport");
  }
  
  @SuppressWarnings("unused")
  public void loadFile(File file) {
    throw new UnsupportedOperationException("unsupport");
  }
  
  public Iterator<Integer> iterator() { return data.iterator(); }

  public void addToList(List<Integer> list){
    list.addAll(data);
  }
}
