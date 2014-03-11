/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.common.io;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListSet;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 9, 2009  
 */
public class ConcurrentSetIntCacherBak implements ConcurrentSetInt {
  
  private volatile ConcurrentSkipListSet<Integer> data;
  private volatile TreeSet<Integer> cacher;
  private volatile int size = 0;

  public ConcurrentSetIntCacherBak() {
    cacher = new TreeSet<Integer>();
    data = new ConcurrentSkipListSet<Integer>();
  }

  public void add(int value) {
    if(data.add(value)) size++;
  }

  public boolean contains(int value) {
    return cacher.contains(value) || data.contains(value);
  }

  public boolean isEmpty() {
    return  cacher.isEmpty() && data.isEmpty();
  }

  public int size() { return size; }

  public void clear() {
    data.clear();
    cacher.clear();
  }

  public void write(FileChannel channel)  throws Exception {
//    System.out.println("kich thuoc tai day na "+ data.size());
    Iterator<Integer> iterator = data.iterator();
    while(iterator.hasNext()) {
      if(!cacher.add(iterator.next())) {
        iterator.remove();
      }
    }
    
    int index = 0;
    iterator = cacher.iterator();
    while(iterator.hasNext()) {
      int length =  2560;
      if(size - index <  length) {
        length = size - index;  
      }
      
      if(length <  1) break;
      ByteBuffer buff = ByteBuffer.allocateDirect(length*4);
      
      int i = 0;
      for(int k = 0; k < length; k++) {
        buff.putInt(i, iterator.next());
        index++;
        i += 4;
      }
      
      buff.rewind();

      if(channel.isOpen()) channel.write(buff);
      buff.clear();
    }
//    System.out.println("==== > "+ index);
  }
  
  public void addToList(List<Integer> list){
    Iterator<Integer> iterator = data.iterator();
    while(iterator.hasNext()) {
      int value = iterator.next();
      if(!cacher.add(value)) iterator.remove();
    }
    list.addAll(cacher);
  }

  public void loadFile(File file) {
    if(!file.exists() || file.length() < 1) return ; 

    RandomAccessFile random = null;
    FileChannel channel = null;
    try {
      random  = new RandomAccessFile(file, "r");
      random.seek(0);
      
      channel = random.getChannel();
      ByteBuffer buff = ByteBuffer.allocate((int)file.length());      
      channel.read(buff);
      buff.rewind();      
      
      int size_ = ((int)file.length())/4;
      IntBuffer intBuffer = buff.asIntBuffer();
      for(int i = 0; i < size_; i++) {
        if(cacher.add(intBuffer.get(i))) size++;
      }
      
      buff.clear();
      channel.close();
      random.close();
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    } finally {
      try {
        if(random != null) random.close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
  }
}
