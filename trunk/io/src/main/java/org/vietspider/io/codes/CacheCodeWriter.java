/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.io.codes;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

import org.vietspider.common.io.ConcurrentSetInt;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 12, 2008  
 */
public class CacheCodeWriter extends CodesWriter<ConcurrentSetInt> {
  
  public void save() {
    if(codes.size() < 1) return;
    
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
    FileChannel channel= null;
    try {
      random  = new RandomAccessFile(tempFile, "rwd");
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

    try {
      RWData.getInstance().copy(tempFile, file);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }

    tempFile.delete();
  }

}
