/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.io.access;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.vietspider.common.io.LogService;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 28, 2008  
 */
public class FileTrackerReader  {
  
  synchronized long search(File file, int code) {
    FileChannel channel = null;
    RandomAccessFile random = null;
    long value  = -1;
    try {
      random = new RandomAccessFile(file, "r");
      channel = random.getChannel();
      
      int read = 0;
      while(read < random.length()) {
        ByteBuffer buff = ByteBuffer.allocate(300*12);
        int r = channel.read(buff, read);
        if(r == -1) break;
        read += r;
        buff.rewind(); 
        
        for(int i = 0 ; i < r - 12; ){
          int key = buff.getInt(i);
          i += 4;
          long accessTime = buff.getLong(i);
          i += 8;
          
          if(key != code) continue;
          value  = accessTime;
          break;
        }
        if(value != -1) break;
      }
     
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
    
    return value;
  }
 
}
