/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.pool2;

import java.io.File;
import java.util.Set;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 17, 2009  
 */
public class TestScan {
  public static void main(String[] args) {
    int status = 0;
    File file = new File("F:\\");
    try {
        Set<File> files0 = null;
        Set<File> files1 = null;
        long t0 ;
        
        t0 = System.nanoTime();
        files1 = DirScanner.listAllContentsUnder(file, 5);
        t0 = System.nanoTime() - t0;
        System.out.println(t0 + " ticks.");
        
        
        t0 = System.nanoTime();
        files0 = SerialDirScanner.listAllContentsUnder(file);
        t0 = System.nanoTime() - t0;
        System.out.println(t0 + " ticks.");
        
        System.out.println(files0.size() + "," + files1.size());
        System.out.println(files0.size() == files1.size());
        
       
    } catch (Throwable exc) {
        status = 1;
        exc.printStackTrace();
    } finally {
        System.exit(status);
    }
} 
}
