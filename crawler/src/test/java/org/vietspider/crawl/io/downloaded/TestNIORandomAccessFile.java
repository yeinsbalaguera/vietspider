/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.io.downloaded;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.ConcurrentSkipListSet;

import org.vietspider.common.io.LogService;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 13, 2008  
 */
public class TestNIORandomAccessFile {
  
  static int [] values = {1, 234, 45, 233, -654, -78, 10, 230};
  
  private static void write() throws Exception {
    ConcurrentSkipListSet<Integer>  codes = new ConcurrentSkipListSet<Integer>();
    int min = -500000;
    int max = 500000;
    for(int i = min; i < max; i++) {
      codes.add(i);
    }
    
    RandomAccessFile random = null;
    File file = new File("D:\\Temp\\c.txt");
    try {
      random  = new RandomAccessFile(file, "rwd");
      FileChannel channel = random.getChannel();
      
      int size = codes.size()*4;//values.length*4;
      ByteBuffer buff = ByteBuffer.allocateDirect(size);
      int i = 0;
      for(Integer code : codes){
        buff.putInt(i, code);
        i += 4;
      }
//      for(int i = 0; i < values.length; i++){
//        buff.putInt(i*4, values[i]);
//      }
      
      buff.rewind();
      if(channel.isOpen()) channel.write(buff);
      
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
  
  private static void readAll() throws Exception {
    RandomAccessFile random = null;
    File file = new File("D:\\java\\headvances\\core\\trunk\\vietspider\\startup\\src\\test\\data\\track\\downloaded\\ARTICLE\\");
    file = new File(file, "2008_05_01");
    try {
      random  = new RandomAccessFile(file, "r");
      FileChannel channel = random.getChannel();
      ByteBuffer buff = ByteBuffer.allocate((int)file.length());      
      channel.read(buff);
      buff.rewind();      
      buff.clear();
      channel.close();
      
      int size = ((int)file.length())/4;
      IntBuffer intBuffer = buff.asIntBuffer();
      int [] newValues = new int[size];
      for(int i = 0; i < newValues.length; i++) {
        newValues[i] = intBuffer.get(i);
      }
      
      for(int i = 0; i < newValues.length; i++) {
        System.out.println(newValues[i]);
//        if(newValues[i] == 2107870136) {
//          System.out.print("thay co " + newValues[i]+ " , ");
//        }
//        if(i %100 == 0) System.out.println();
      }
    } finally {
      
    }
  }
  
  private static void read() throws Exception {
    RandomAccessFile random = null;
    File file = new File("D:\\Temp\\c.txt");
    try {
      
      long length = file.length();
      int size = ((int)file.length())/4;
      int [] newValues = new int[size];
      
      random  = new RandomAccessFile(file, "r");
      
      random.seek(0);
      long pos = 0;
      int i  = 0;
      while(pos < length) {
        newValues[i] = random.readInt();
        pos = random.getFilePointer();
        i++;
      }
      
//      for(i = 0; i < newValues.length; i++) {
//        System.out.print(newValues[i]+ " , ");
//        if(i % 100 == 0) System.out.println();
//      }
    } finally {
      
    }
  }

  public static void main(String[] args)  throws Exception {
//    long start = System.currentTimeMillis();
//    write();
//    long end = System.currentTimeMillis();
//    System.out.println(" thuc hien het "+ (end - start)+ " voi list");
    
//    long start = System.currentTimeMillis();
    readAll();
//    long end = System.currentTimeMillis();
//    System.out.println(" thuc hien het "+ (end - start)+ " voi list");
    
//    long start = System.currentTimeMillis();
//    read();
//    long end = System.currentTimeMillis();
//    System.out.println(" thuc hien het "+ (end - start)+ " voi list");
  }
  
}
