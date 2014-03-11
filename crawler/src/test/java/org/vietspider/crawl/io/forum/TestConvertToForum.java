/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.io.forum;

import java.io.File;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 20, 2008  
 */
public class TestConvertToForum {
  
  private static File outFolder = new File("D:\\Temp\\moom\\private\\FORUM2\\");
  
  private static void convert(File file) throws Exception {
    /*if(file.getName().endsWith(".lg")) return;
    
    System.out.println(" convert file "+ file.getAbsolutePath());
//    File newFile = new File(outFolder, file.getName()+".post");
    JdbmPostTracker jdbmPostTracker = new JdbmPostTracker();
    jdbmPostTracker.init(outFolder, file.getName()+".post", "PostTracker");
    
    RandomAccessFile random = null;
    try {
      random  = new RandomAccessFile(file, "r");
      random.seek(0);
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

          jdbmPostTracker.write(key, post);
        } catch (Exception e) {
          LogService.getInstance().setThrowable(e);
          break;
        }
      }
      random.close();
//      System.out.println("read  "+ addressCode + " value "+ v);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    } finally {
      try {
        jdbmPostTracker.endSession();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
      try {
        if(random != null) random.close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }  */
    
  }
  
  public static void main(String[] args) {
    File file = new File("D:\\Temp\\moom\\private\\FORUM\\");
    File [] files = file.listFiles();
    for(File f : files) {
      try {
        convert(f);
      } catch (Exception e) {
        e.printStackTrace();
      } 
      
    }
  }
}
