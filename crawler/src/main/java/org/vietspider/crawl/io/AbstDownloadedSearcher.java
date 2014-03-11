/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.io;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.vietspider.common.io.LogService;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 27, 2007  
 */
public abstract class AbstDownloadedSearcher {
  
  protected final static long SPLIT_SIZE = 100000;
  
  private final static int BUFFER_SIZE = 1024;
  
  protected final static int SAVE_SIZE = 50;
  
  protected void writeNewFile(File file, Integer...codes) throws Exception {
    FileOutputStream outputStream = null; 
    FileChannel outputChannel = null;
    
    try {
      outputStream = new FileOutputStream(file, true);
      outputChannel = outputStream.getChannel();

      ByteBuffer intBuffer = ByteBuffer.allocate(codes.length*4);
      for(Integer integer : codes) intBuffer.putInt(integer.intValue());
      intBuffer.rewind();
      outputChannel.write(intBuffer);        
      intBuffer.clear();
      
      outputChannel.close();
      outputStream.close();
    } finally {
      try {
        if(outputChannel != null) outputChannel.close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
      
      try {
        if(outputStream != null) outputStream.close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }

  }
  
  protected long search(File file, int code)  {
    RandomAccessFile random = null;
    try {
      random = new RandomAccessFile(file, "r");
      long value = search(random, file, code, 0, random.length());
      random.close();
      return value;
    } catch (EOFException e) {
      return 1;
    } catch (FileNotFoundException e) {
      return 1;
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
      return 1;
    } finally {
      try {
        if(random != null) random.close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
  }
  
  protected long search(RandomAccessFile random, File file, int code, long start, long end) throws Exception {
    //check start
    random.seek(start);
    int value = random.readInt();
    if(value == code) return -1;
    if(value > code) return start;

    //check end
//    if(writing && compare) return -2;
    random.seek(end - 4);
    value  = random.readInt();
    if(value < code) return end;

    long seek = (end+start)/2;
    if(seek%4 != 0) seek -= 2;
    
//    if(writing && compare) return -2;
    random.seek(seek);
    value  = random.readInt();
//    System.out.println("start "+ start+ " end "+end + " seek "+seek + " : "+value);
    if(value > code) return search(random, file, code, start, seek);
    return search(random, file, code, seek, end);
  }
  
  protected void append(FileChannel outputChannel, InputStream input) throws Exception {
    int read = -1;
    byte bytes [] = new byte[BUFFER_SIZE];
    try {
      while((read = input.read(bytes)) > -1){
        ByteBuffer outputBuff = ByteBuffer.allocateDirect(read);
        outputBuff.put(bytes, 0, read);
        outputBuff.rewind();
        outputChannel.write(outputBuff);
        outputBuff.clear(); 
      } 
    } finally {
      try {
        input.close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
  }
  
}
