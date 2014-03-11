/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.io;

import java.io.File;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 31, 2008  
 */
public class TestFileRename {
  
  public static void main(String[] args) {
   File folder = new File("D:\\java\\headvances\\core\\trunk\\vietspider\\startup\\src\\test\\data\\sources\\homepages\\url\\PROFILE\\H-u1ED3 s-u01A1\\");
   File [] files = folder.listFiles();
   int counter = 1192;
   for(int i = 5; i < 123; i++) {
     File file  = new File(folder, "H-u1ED3 s-u01A1.360 Yahoo.homepages."+ String.valueOf(i));
     file.renameTo(new File(folder, "H-u1ED3 s-u01A1.360 Yahoo.homepages."+ String.valueOf(counter)));
     counter++;
   }
  }
}
