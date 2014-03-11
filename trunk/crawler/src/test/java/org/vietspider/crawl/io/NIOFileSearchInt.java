/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.io;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 20, 2007  
 */
public class NIOFileSearchInt {
  
  private final static long BUFFER_SIZE = 1011;

  public NIOFileSearchInt() {
  }

  public boolean search(File file, int value) {
    FileInputStream stream = null;
    FileChannel channel = null;
    try {
      stream = new FileInputStream(file);
      channel = stream.getChannel();
      
      int from = 0;
      ByteBuffer byteBuffer = ByteBuffer.allocate((int)BUFFER_SIZE);
      ByteInputStream byteStream = null;

      while( channel.read(byteBuffer, from) > -1) {
        channel.read(byteBuffer);
        byteBuffer.rewind();      
        
        byte [] bytes = byteBuffer.array();
//        System.out.println(from + " : "+ bytes.length);
        from += bytes.length; 
        
        if(byteStream == null) {
          byteStream = new ByteInputStream(bytes);
        } else {
          byteStream.append(bytes);
        }

        while(byteStream.available()) {
          int code = byteStream.readInt();
          if(code != value) continue;
          channel.close();
          stream.close();
          byteBuffer.clear();
          return true;
        }
        byteBuffer.clear();
      }
    } catch (Exception e) {
      e.printStackTrace();
      //LogService.getInstance().setThrowable(e);
    } finally {
      try {
        if(channel != null) channel.close();
        if(stream != null) stream.close();
      } catch (Exception e) {
        e.printStackTrace();
        //LogService.getInstance().setThrowable(e);
      }
    }
    return false;
  }

  private class ByteInputStream {
    
    private byte [] buff;
    
    private int pos = 0;
    
    public ByteInputStream(byte [] bytes) {
      this.buff = bytes;
    }
      
    public void append(byte [] bytes) {
      System.out.println("con lai  "+(buff.length - pos));
      byte [] newBuff = new byte[(buff.length - pos) + bytes.length];
      System.arraycopy(buff, pos, newBuff, 0, (buff.length - pos));
      System.arraycopy(bytes, 0, newBuff, (buff.length - pos), bytes.length);
      buff = newBuff;
      pos = 0;
      System.out.println("tai day "+buff.length);
    }
    
    public int read() {
      return (pos < buff.length) ? (buff[pos++] & 0xff) : -1;
    }
    
    public boolean available() {
      return buff.length - pos >= 4;
    }
    
    public final int readInt(){
      int ch1 = read();
      int ch2 = read();
      int ch3 = read();
      int ch4 = read();
      return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
    }

  }

}
