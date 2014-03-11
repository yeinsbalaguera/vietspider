/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.io.access;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Iterator;

import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 11, 2008  
 */
public class CacheTrackerWriter extends TrackerWriter {
  
  public void save() {
//    File file = UtilFile.getFile("system", "last-update");
    File tempFile = null;
    try {
      tempFile = new File(file.getParentFile(), file.getName()+".temp");
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
      
      int size = codes.size()*12;
      ByteBuffer buff = ByteBuffer.allocateDirect(size);
      int i = 0;
      
      Iterator<Integer> iterator = codes.keySet().iterator();
      while(iterator.hasNext()) {
        int key = iterator.next();
        if(i > (buff.capacity() - 4)) break;
        buff.putInt(i, key);
        i += 4;
        
        if(i > (buff.capacity() - 8)) break;
        buff.putLong(i, codes.get(key));
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

    try {
      RWData.getInstance().copy(tempFile, file);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }

    tempFile.delete();
  }


}
