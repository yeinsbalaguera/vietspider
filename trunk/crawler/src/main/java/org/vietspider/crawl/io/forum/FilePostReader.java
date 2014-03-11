/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.io.forum;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.vietspider.common.io.LogService;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 11, 2008  
 */
class FilePostReader implements Runnable {

  private File file;
  private int value;
  private int addressCode;

  public synchronized void run() {
    if(file != null ) value  = read();
    file = null;
    notifyAll(); // wake up waiters
  }

  int getValue(){ return value; }

  void read(File f, int code) {
    this.file = f;
    this.value = -1;
    this.addressCode = code;
    new Thread(this).start();
  }

  boolean isRunning() { return file != null; }

  /*private int read() {
    if(file == null || file.length() < 1) return -1; 

    RandomAccessFile random = null;
    try {
      random  = new RandomAccessFile(file, "r");
      random.seek(0);
      int v  = -1;
      long length = file.length();

      long pos = 0;
      while(pos < length) {
        try {
          int key = random.readInt();

          pos = random.getFilePointer();
          if(pos >= (length - 4)) break;

          int post = random.readInt();

          pos = random.getFilePointer();
          if(pos >= (length - 8)) break;

          random.readLong();

          pos = random.getFilePointer();

          if(key != addressCode) continue;
          v = post;
          break;  
        } catch (Exception e) {
          LogService.getInstance().setThrowable(e);
          break;
        }
      }
      random.close();
//    System.out.println("read  "+ addressCode + " value "+ v);
      return v;

    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
      return -1;
    } finally {
      try {
        if(random != null) random.close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
  }*/
  
  private int read() {
    if(file == null || file.length() < 1) return -1; 

    RandomAccessFile random = null;
    FileChannel channel = null;
    try {
      random  = new RandomAccessFile(file, "r");
      random.seek(0);
      channel = random.getChannel();
      
      int v  = -1;
      
      int max = 300*(4+4+8);
      ByteBuffer buff = ByteBuffer.allocate(max);      

      int read = -1;
      while((read = channel.read(buff)) != -1) {
        buff.rewind();
        
        ByteArrayInputStream byteInput = new ByteArrayInputStream(buff.array());
        DataInputStream dataInputStream = new DataInputStream(byteInput);

        long pos = 0;
        while(pos <= read - 16) {
          int key = dataInputStream.readInt();
          int post = dataInputStream.readInt();
          dataInputStream.readLong();
          
          if(key == addressCode) {
            v = post;
            break;
          }
          pos += 16;
        }
        
        if(v != -1) break;
      }
      random.close();
      channel.close();
//      System.out.println("read  "+ addressCode + " value "+ v);
      return v;

    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
      return -1;
    } finally {
      try {
        if(random != null) random.close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
      
      try {
        if(channel != null) channel.close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
  }
}
