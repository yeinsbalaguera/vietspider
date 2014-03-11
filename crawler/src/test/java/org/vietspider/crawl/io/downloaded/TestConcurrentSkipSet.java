/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.io.downloaded;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Iterator;
import java.util.concurrent.ConcurrentSkipListSet;

import org.vietspider.common.io.LogService;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 13, 2008  
 */
public class TestConcurrentSkipSet {
  
  private static void writeFile(ConcurrentSkipListSet<Integer>  codes, File file){
    RandomAccessFile random = null;
    try {
      random  = new RandomAccessFile(file, "rwd");
      random.seek(0);
      
      Iterator<Integer> iterator = codes.iterator();
      while(iterator.hasNext()) {
        random.writeInt(iterator.next());
      }
      
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

  private static void writeFile(Integer [] codes, File file){
    RandomAccessFile random = null;
    try {
      random  = new RandomAccessFile(file, "rwd");
      random.seek(0);

      for(Integer code : codes) {
        if(code == null) continue;
        random.writeInt(code);
      }

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

  public static void main(String[] args) {
    ConcurrentSkipListSet<Integer>  codes = new ConcurrentSkipListSet<Integer>();
//    List<Integer> codes = new ArrayList<Integer>();
    
    int min = -500000;
    int max = 500000;
    for(int i = min; i < max; i++) {
      codes.add(i);
    }
    
    
//    codes.add(1);
//    codes.add(-1);
//    codes.add(10);
//    codes.add(34);
//    codes.add(2);
//    codes.add(6);
//    codes.add(34);
//    codes.add(27);
//    codes.add(56);
//    codes.add(-1);
//    codes.add(-4);
    
//    int max_for = 10000000;
//    long start = System.currentTimeMillis();
//    for(int i = 0; i < max_for; i++) {
//      for(Integer code : codes) {
//        int v = code;
//      }
//    }
//    long end = System.currentTimeMillis();
//    System.out.println(" thuc hien het "+ (end - start)+ " voi list");
    
    // next for
    
    
    Integer [] array  = new Integer[codes.size()];
//    codes.add(1000);
    codes.remove(-4);
    array = codes.toArray(array);
    
    System.out.println(" bat dau ghi nhe ");
    
    File file1 = new File("D:\\Temp\\a.txt");
    
    long start = System.currentTimeMillis();
    writeFile(codes, file1);
    long end = System.currentTimeMillis();
    System.out.println(" thuc hien het "+ (end - start)+ " voi list");
    
    File file2 = new File("D:\\Temp\\b.txt");
    start = System.currentTimeMillis();
    writeFile(array, file2);
    end = System.currentTimeMillis();
    System.out.println(" thuc hien het "+ (end - start)+ " voi list");
    
//    if(array[0] == null) {
//      System.out.println(" hihihuusdfsd fsdfhds ");
//    }
    
//    Object [] array = codes.toArray();
    
    
//    start = System.currentTimeMillis();
//    for(int i = 0; i < max_for; i++) {
//      for(Integer code : array) {
//        int v = (Integer)code;
//      }
//    }
//    end = System.currentTimeMillis();
//    System.out.println(" thuc hien het "+ (end - start) + " voi array");
    
//    for(Integer code : array) {
//      System.out.print(code + " ,");
//    }
    
  }
}
