/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.common.util;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

import org.vietspider.common.io.ConcurrentSetInt;
import org.vietspider.common.io.ConcurrentSetIntCacher;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 7, 2009  
 */
public class TestConnurrentSetIntWriter {
  
  static  int size  = 5000000;
  static int [] values = new int [size];
  
  private static void testNonBlocking(ConcurrentSetInt  hashSet) {
    long start  = System.currentTimeMillis();
    for(int i = 0; i < size; i++) {
      hashSet.add(values[i]);
    }
    long end = System.currentTimeMillis();
    System.out.println("add " + (end - start));
    
    start  = System.currentTimeMillis();
    for(int i = 0; i < size; i++) {
      hashSet.contains(values[i]);
    }
    end = System.currentTimeMillis();
    System.out.println("contains " + (end - start));
    System.out.println(" total "+ hashSet.size());
  }
  
  public static void main(String[] args) throws Exception {
    ConcurrentSetInt  hashSet = new ConcurrentSetIntCacher();
    
    for(int i = 0; i < size; i++) {
      if(i < size/2) {
        values[i] = (int)(Math.random()*size);
      } else {
        values[i] = -(int)(Math.random()*size);
      }
    }
    testNonBlocking(hashSet);
    
//    hashSet.add(100);
//    hashSet.add(-10000);
//    hashSet.add(-34);
//    hashSet.add(13);
//    hashSet.add(-2);
//    hashSet.add(89);
//    hashSet.add(13);
    
//    File file  = new File("D:\\Temp\\intwriter.txt");
    File file = new File("D:\\Temp\\codes\\a.txt");
    if(!file.exists()) file.createNewFile();
    
    RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
    int a = -2111509170;
    try {
      FileChannel channel = randomAccessFile.getChannel();
      hashSet.write(channel);
    } finally {
      randomAccessFile.close();
    }
    
    randomAccessFile = new RandomAccessFile(file, "rw");
    try {
      long pos = 0;
      long length = file.length();
      int counter = 0;
      while(pos < length) {
        int value = randomAccessFile.readInt();
        hashSet.add(value);
        if(counter%10 == 0) System.out.println();
        System.out.print(", "+ value);
        counter++;
        pos += 4;
      }
    } finally {
      randomAccessFile.close();
    }
    
  }
}
