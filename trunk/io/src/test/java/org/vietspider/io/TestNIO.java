/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.io;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 6, 2008  
 */
public class TestNIO {
  
  public static void main(String[] args) throws Exception {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(bos);
    dos.writeLong(10);
    dos.writeInt(-4);
    // etc.
    dos.flush();
    byte[] data = bos.toByteArray();  
    
    File file  = new File("D:\\Temp\\nio.txt");
    FileOutputStream output = new FileOutputStream(file);
    FileChannel channel = output.getChannel();
    ByteBuffer buff = ByteBuffer.allocateDirect(data.length);
    buff.put(data);
    buff.putInt(3);
    buff.rewind();
    if(channel.isOpen()) channel.write(buff);        
    buff.clear();
    channel.close();
    output.close();
    
    
    RandomAccessFile random = new RandomAccessFile(file, "r");
    channel = random.getChannel();

    int read = 0;
    while(read < random.length()) {
      buff = ByteBuffer.allocate(300*12);
      int r = channel.read(buff, read);
      if(r == -1) break;
      read += r;
      buff.rewind(); 
    }
    
    System.out.println(buff.getLong());
    System.out.println(buff.getInt(8));
  }
  
}
