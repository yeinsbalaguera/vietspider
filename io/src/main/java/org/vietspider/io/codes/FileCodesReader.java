/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.io.codes;

import java.io.File;
import java.io.RandomAccessFile;

import org.vietspider.common.io.LogService;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 26, 2008  
 */
public class FileCodesReader implements Runnable {
  
  private File file;
  private int code;
  private long position = -1;
  
  public void putData(File f, int c) {
    this.file = f;
    this.code = c;
    position = -1;
    new Thread(this).start();
  }
  
  public synchronized void run() {
    if(code == -1 || file == null) return;
    
    position = search(file, code);
    
    code = -1;
    file = null;
    notifyAll(); // wake up waiters
  }
  
  public boolean isRunning() { return file != null; }
  
  public long getPosition() { return position; }
  
  public long search(File f, int c)  {
    RandomAccessFile random = null;
    try {
      random = new RandomAccessFile(f, "r");
      long length  = random.length();
      long value = binarySearch(random, c, 0, length);
      random.close();
      return value;
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
  }
  
  protected long binarySearch(RandomAccessFile random, int c, long start, long end) throws Exception {
    if(start+4 > end) return -1;
    
    random.seek(start);
    int value  = random.readInt();
    if(c < value) return -1;
    
    random.seek(end - 4);
    value  = random.readInt();
    if(value < c) return -1;
    
    long mid  = (start+end)/2;
    if(mid%4 == 2) mid -= 2;
    random.seek(mid);
    value  = random.readInt();
    
    if(value == c) {
      return mid;
    } else if(value > c) {
//      System.out.println(" left tai vi tri start "+ start + " mid: "+ mid+ " end: "+ end);
//      System.out.println(" left tai vi tri "+ mid + " C: "+ c + " , value "+ value + " == > " + (c < value));
      return binarySearch(random, c, start, mid);
    }
//    System.out.println(" right tai vi tri start "+ start + " mid: "+ mid+ " end: "+ end);
//    System.out.println(" right tai vi tri "+ mid + " C: "+ c + " , value "+ value + " == > " + (c < value));
    return binarySearch(random, c, mid+4, end);
  }
  
//  protected long searchLeft(RandomAccessFile random, long length, int c, long start, long end) throws Exception {
//    //check start
//    if(start > length) return -1;
//    random.seek(start);
//    int value = random.readInt();
//    if(value == c) return start;
//    
//    if(start + 4 == end) return -1;
//
//    long seek = (end+start)/2;
//    if(seek%4 != 0) seek = seek - 2;
//    
//    if(seek > length) return -1;
//    random.seek(seek);
//    value  = random.readInt();
////    System.out.println("\nsearch left :  start "+ start+ " end "+end + " seek "+seek);
////    System.out.println(" === > " + " code "+ c + ": center value "+ centerValue + " left value : "+leftValue);
//    return value == c ? seek : value > c ?
//        searchLeft(random, length, c, start, seek) : searchRight(random, length, c, seek, end);
//  }
//  
//  protected long searchRight(RandomAccessFile random, long length, int c, long start, long end) throws Exception {
//    //check end
//    random.seek(end - 4);
//    if((end - 4) > length) return -1;
//    int value  = random.readInt();
//    if(value == c) return end - 4;
//    
//    if(start + 4 == end) return -1;
//
//    long seek = (end+start)/2;
//    if(seek%4 != 0) seek -= 2;
//    
//    if(seek > length) return -1;
//    random.seek(seek);
//    value  = random.readInt();
//    System.out.println(" right tai vi tri "+ seek + " C: "+ c + " , value "+ value);
//    return value == c ? seek : value > c ? 
//        searchLeft(random, length, c, start, seek): searchRight(random, length, c, seek, end);
//  }
//  
  
}
