/***************************************************************************
 * Copyright 2003-2007 by VietSpider - All rights reserved.                *    
 **************************************************************************/
package org.vietspider.common.io;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Jun 4, 2007
 */
@Deprecated
public class DataReader {

  public synchronized byte[] load(String path) throws Exception {
    return load(new File(path));
  }

  public synchronized byte[] load(File file) throws Exception {
    if (!file.exists() || !file.isFile()) return new byte[0];
    FileInputStream input = null;
    try {
      input = new FileInputStream(file);
      return load(input, file.length());    
    } finally {
      try {
        if(input != null) input.close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
  }

  public synchronized byte[] load(FileInputStream input, long fsize) throws Exception {
    FileChannel channel = null;
    try {
      channel = input.getChannel();
      ByteBuffer buff = ByteBuffer.allocate((int)fsize);      
      channel.read(buff);
      buff.rewind();      
      byte[] data = buff.array();

      buff.clear();
      channel.close();
      
      return data;
    } finally {
      try {
        if(channel != null) channel.close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
  }

  public ByteArrayOutputStream loadInputStream(InputStream input) throws Exception {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    if(input == null) return output;
    byte[] data  = new byte[1024];      
    int available = -1;
    while((available = input.read(data)) != -1) {
      output.write(data, 0, available);
    }
//    if(output.size() == 2 
//       && new String(output.toByteArray()).equals("-1")) {
//      return new BytesOutputStream();
//    }
    return output;
  }
  
  public void writeInputStream(InputStream input, OutputStream output) throws Exception {
    byte[] data  = new byte[1024];      
    int available = -1;
    while((available = input.read(data)) > -1) {
      output.write(data, 0, available);
    }   
  }

}
