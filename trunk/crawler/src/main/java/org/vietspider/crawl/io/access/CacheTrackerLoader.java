/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.io.access;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.ConcurrentSkipListMap;

import org.vietspider.common.io.LogService;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 12, 2008  
 */
class CacheTrackerLoader {
  
  void load(File file, ConcurrentSkipListMap<Integer, Long> codes) {
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
      
      for(int i = 0 ; i < buff.capacity() - 12; ){
        int key = buff.getInt(i);
        i += 4;
        long access = buff.getLong(i);
        i += 8;
        codes.put(key, access);
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
