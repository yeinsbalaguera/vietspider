/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.io.forum;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Map;

import org.vietspider.common.io.LogService;
import org.vietspider.crawl.io.forum.CachePostTracker.PostValue;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 11, 2008  
 */
public final class CachePostLoader {
  
  void load(File file, Map<Integer, PostValue> data) {
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
      
      for(int i = 0 ; i < file.length(); ){
        int key = buff.getInt(i);
        i += 4;
        
        if(i > (buff.capacity() - 4)) break;
        int post = buff.getInt(i);
        i += 4;
        
        if(i > (buff.capacity() - 8)) break;
        long updateTime = buff.getLong(i);
        i += 8;
        
        data.put(key, new PostValue(post, updateTime));
      }
      
      buff.clear();
      channel.close();
      random.close();
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    } finally {
      try {
        if(channel != null) channel.close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
      
      try {
        if(random != null) random.close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
  }
  
}
