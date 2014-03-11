/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.io.downloaded;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.vietspider.common.io.LogService;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 13, 2008  
 */
public class TestNIORandomData {
  
  private static class Person {
    int id  ;
    long value;
    
    Person (int i, long n) {
      this.id = i;
      this.value = n;
    }
  }
  
  static Person [] values = {
     new Person(1, 1221038213),
     new Person(143, -534535345),
     new Person(-45, -43534534),
     new Person(-7855, 10000),
     new Person(2, 325345345)
  };
  
  private static void write() throws Exception {
    RandomAccessFile random = null;
    File file = new File("D:\\Temp\\c.txt");
    try {
      file.delete();
      file.createNewFile();
      
      random  = new RandomAccessFile(file, "rwd");
      FileChannel channel = random.getChannel();
      
      int size = values.length*12;
      ByteBuffer buff = ByteBuffer.allocateDirect(size);
      int i = 0;
      for(Person value : values){
        buff.putInt(i, value.id);
        i += 4;
        buff.putLong(i, value.value);
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
        if(random != null) random.close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
  }
  
  private static void readAll() throws Exception {
    RandomAccessFile random = null;
    File file = new File("D:\\Temp\\c.txt");
    try {
      random  = new RandomAccessFile(file, "r");
      FileChannel channel = random.getChannel();
      ByteBuffer buff = ByteBuffer.allocate((int)file.length());      
      channel.read(buff);
      buff.rewind();
      
      Person [] newValues = new Person[(int)(file.length()/12)];
      int index = 0;
      for(int i = 0 ; i < file.length(); ){
        int id = buff.getInt(i);
        i += 4;
        long value = buff.getLong(i);
        i += 8;
        newValues[index] = new Person(id, value);
        index++;
      }
      
      buff.clear();
      channel.close();
      
      for(int i = 0; i < newValues.length; i++) {
        System.out.println(newValues[i].id+ " , " + newValues[i].value);
      }
    } finally {
      
    }
  }
  
  private static void read() throws Exception {
    RandomAccessFile random = null;
    File file = new File("D:\\Temp\\c.txt");
    try {
      
      long length = file.length();
      int size = ((int)file.length())/12;
      Person [] newValues = new Person[size];
      
      random  = new RandomAccessFile(file, "r");
      
      random.seek(0);
      long pos = 0;
      int i  = 0;
      while(pos < length) {
        int id = random.readInt();
        pos = random.getFilePointer();
        if(pos >= length) break;
        
        long value = random.readLong();
        pos = random.getFilePointer();
        
        newValues[i] = new Person(id, value); 
        i++;
      }
      
      for(i = 0; i < newValues.length; i++) {
        System.out.println(newValues[i].id+ " , " + newValues[i].value);
      }
    } finally {
      
    }
  }

  public static void main(String[] args)  throws Exception {
//    long start = System.currentTimeMillis();
//    write();
//    long end = System.currentTimeMillis();
//    System.out.println(" thuc hien het "+ (end - start)+ " voi list");
    
//    long start = System.currentTimeMillis();
//    readAll();
//    long end = System.currentTimeMillis();
//    System.out.println(" thuc hien het "+ (end - start)+ " voi list");
    
    long start = System.currentTimeMillis();
    read();
    long end = System.currentTimeMillis();
    System.out.println(" thuc hien het "+ (end - start)+ " voi list");
  }
  
}
