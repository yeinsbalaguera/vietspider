/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.io;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Iterator;
import java.util.concurrent.ConcurrentSkipListSet;

import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 7, 2007  
 */
public class DownloadedConverter {
  
  private static ConcurrentSkipListSet<Integer> saveCodes = new ConcurrentSkipListSet<Integer>();
  
  private static void convertFile(File file, File output) throws Exception {
    String value = new String(RWData.getInstance().load(file), "utf-8");
    String [] elements = value.split(",");
    for(String ele : elements) {
      try {
//        System.out.println(ele);
        saveCodes.add(Integer.parseInt(ele));
      }catch (Exception e) {
//        e.printStackTrace();
      }
    }
    save(new File(output, file.getName()));
  }
  
  private static void save(File file) {
    try {
      if(!file.exists()) file.createNewFile();
      RandomAccessFile randomAccess = new RandomAccessFile(file, "rw");
      if(randomAccess == null) return ;

      Iterator<Integer> iter = saveCodes.iterator();
      while(iter.hasNext()) {
        Integer code = iter.next();
        try {
          randomAccess.seek(randomAccess.length());
          randomAccess.writeInt(code);
        } catch (Exception e) {
          LogService.getInstance().setThrowable(e);
        }
        iter.remove();
      }
      randomAccess.close();
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }
  
  private static void convert(File folder, File output) {
    File  [] files = folder.listFiles();
    for(File file : files) {
      try {
        convertFile(file, output);
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
  }
  
  public static void main(String[] args) {
    File folder = new File("F:\\Temp2\\downloaded\\");
    File output = new File("F:\\Temp2\\downloaded2\\");
    convert(folder, output);
  }
}
