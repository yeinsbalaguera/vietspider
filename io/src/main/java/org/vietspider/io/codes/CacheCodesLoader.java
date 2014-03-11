/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.io.codes;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;

import org.vietspider.common.io.ConcurrentSetInt;
import org.vietspider.common.io.LogService;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 12, 2008  
 */
public class CacheCodesLoader {
  
  public void load(File file, ConcurrentSetInt codes) {
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
      
      int size = ((int)file.length())/4;
      IntBuffer intBuffer = buff.asIntBuffer();
      for(int i = 0; i < size; i++) {
        codes.add(intBuffer.get(i));
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
  
  public void save(File file, ConcurrentSetInt codes) {
    RandomAccessFile random = null;
    FileChannel channel= null;
    try {
      random  = new RandomAccessFile(file, "rwd");
      random.seek(0);
      channel = random.getChannel();
      
      codes.write(channel);
      
     /* int size = codes.size()*4;
      ByteBuffer buff = ByteBuffer.allocateDirect(size);
      int i = 0;
      for(Integer code : codes){
        if(i >= buff.capacity()) break;
        buff.putInt(i, code);
        i += 4;
      }
      
      buff.rewind();
      if(channel.isOpen()) channel.write(buff);
      buff.clear();*/
      
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
