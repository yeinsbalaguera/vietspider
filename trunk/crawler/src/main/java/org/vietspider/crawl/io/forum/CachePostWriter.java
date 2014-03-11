/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.io.forum;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Calendar;
import java.util.Iterator;

import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.crawl.io.forum.CachePostTracker.PostValue;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 11, 2008  
 */
class CachePostWriter extends PostWriter<PostValue> {

  void save() {
    Calendar calendar = Calendar.getInstance();
    long currentTime = calendar.getTimeInMillis();
    
    Iterator<Integer> iterator = data.keySet().iterator();
    
    while(iterator.hasNext()) {
      int key = iterator.next();
      PostValue postValue = data.get(key);
      if(currentTime - postValue.updateTime < PostTracker.EXPIRE) continue;
      iterator.remove();
    }
    
    File tempFile = null;
    String tempCode = String.valueOf(currentTime);
    try {
      tempFile = new File(file.getParentFile(), file.getName()+".temp"+tempCode);
      if(tempFile.exists()) tempFile.delete();
      tempFile.createNewFile();
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
      tempFile = null;
    }
    if(tempFile == null) return;

    RandomAccessFile random = null;
    FileChannel channel = null;
    try {
      random  = new RandomAccessFile(tempFile, "rwd");
      random.seek(0);
      channel = random.getChannel();
      
      int size = data.size()*16;
      ByteBuffer buff = ByteBuffer.allocateDirect(size);
      int i = 0;
      
      iterator = data.keySet().iterator();
      
      while(iterator.hasNext()) {
        int key = iterator.next();
        
        if(i > (buff.capacity() - 4)) break;
        buff.putInt(i, key);
        i += 4;
        
        PostValue postValue = data.get(key);
        
        if(i > (buff.capacity() - 4)) break;
        buff.putInt(i, postValue.post);
        i += 4;
        
        if(i > (buff.capacity() - 8)) break;
        buff.putLong(i, postValue.updateTime);
        i += 8;
      }
      
      buff.rewind();
      if(channel.isOpen()) channel.write(buff);
      
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

    if(!tempFile.exists()) return;
    
    try {
      if(tempFile.length() > file.length()) {
        RWData.getInstance().copy(tempFile, file);
      }
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }

    tempFile.delete();
  }


}
