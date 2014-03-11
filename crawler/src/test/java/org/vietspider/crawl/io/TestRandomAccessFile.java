/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.io;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.RandomAccessFile;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 19, 2007  
 */
public class TestRandomAccessFile {
  
  private static String readLine(RandomAccessFile random) throws Exception {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    int c = -1;
    boolean eol = false;

    while (!eol) {
      switch (c = random.read()) {
      case -1:
      case '\n':
        eol = true;
        break;
      case '\r':
        eol = true;
        long cur = random.getFilePointer();
        if ((random.read()) != '\n') random.seek(cur);
        break;
      default:
        outputStream.write(c);
      break;
      }
    }
    if ((c == -1) && (outputStream.size() == 0)) return null;
    return new String(outputStream.toByteArray(), "UTF-8");
  }
  
  public static void main(String[] args) throws Exception {
    File file = new File("F:\\Temp2\\load2");
    if(file.exists()) file.delete();
    file.createNewFile();
    RandomAccessFile random = new RandomAccessFile(file, "rwd");
    
    random.writeUTF("nhữ đình thuận");
    random.writeInt(1);
    random.writeLong(1000);
    random.writeUTF("Yahoo hử");
    random.writeInt(2);
    random.writeLong(145400);
    random.writeUTF("pẰNG PẰNG");
    random.writeInt(3);
    random.writeLong(5435345);
    
    random.seek(0);
    while(true){
      try {
        String name  = random.readUTF();
        long start = random.getFilePointer();
        int key = random.readInt();
        long value = random.readLong();
        long end = random.getFilePointer();
        System.out.println(name + " : " + key + " : "+ value);
        if(name.indexOf("hử") > -1) {
          random.seek(start);
          random.writeInt(2546560);
          random.seek(end);
        }
      } catch (Exception e) {
//        e.printStackTrace();
        break;
      }
    }
    
    System.out.println("===========================================");

    random.seek(0);
    while(true){
      try {
        long start = random.getFilePointer();
        String name  = random.readUTF();
        int key = random.readInt();
        long value = random.readLong();
        long end = random.getFilePointer();
        System.out.println(name + " : " + key + " : "+ value);
      } catch (Exception e) {
//        e.printStackTrace();
        break;
      }
    }
//    String line = "";
//    long start = 0;
//    while((line = readLine(random)) != null){
//      if(line != null && line.indexOf("Thanglong") > -1) {
//        random.seek(start);
//        random.write((line + " thuan an cut\n").getBytes("utf-8"));
//      }
//      start = random.getFilePointer();
//    }
  }
}
