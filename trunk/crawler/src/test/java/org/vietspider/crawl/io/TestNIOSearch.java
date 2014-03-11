/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.io;

import java.io.File;
import java.io.RandomAccessFile;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 20, 2007  
 */
public class TestNIOSearch {
  
  public static void main(String[] args) throws Exception {
    File file = new File("F:\\Temp2\\downloaded2\\2007_11_02");
    RandomAccessFile random = new RandomAccessFile(file, "rwd");
    StringBuilder builder = new StringBuilder();
    int counter = 0;
    while(true) {
      try {
        if(counter == 7) {
          builder.append('\n');
          counter = 0;
        }
        builder.append(',').append(random.readInt());
        counter++;
      } catch (Exception e) {
        break;
      }
    }
//    System.out.println(builder);
    
    System.out.println("=======================================================================");
    
    NIOFileSearchInt searchInt = new NIOFileSearchInt();
    System.out.println(searchInt.search(file, 2137518767));
    
  }
}
