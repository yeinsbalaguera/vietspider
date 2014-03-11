/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.chars.chardet;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import org.vietspider.chars.jchardet.nsDetector;
import org.vietspider.chars.jchardet.nsICharsetDetectionObserver;
import org.vietspider.chars.jchardet.nsPSMDetector;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Sep 21, 2006
 */
public class TestFileDetect {
  
  public static boolean found = false ;
  
  public static void main(String[] args) throws Exception {
    File file = new File("D:\\Temp\\audit\\20040224.AUD.47plus.txt.processed");
//    File file  = new File("E:\\Temp\\html\\thoibaoviet.html");
    
    FileInputStream input = new FileInputStream(file);
    BufferedInputStream buffer = new BufferedInputStream(input);    
    byte[] data  = new byte[buffer.available()];      
    int available = -1;
    
    System.out.println(data.length);
    
    nsDetector det = new nsDetector(nsPSMDetector.ALL) ;

    // Set an observer...
    // The Notify() will be called when a matching charset is found.

    det.Init(new nsICharsetDetectionObserver() {
      public void Notify(String charset) {
        HtmlCharsetDetector.found = true ;
        System.out.println("CHARSET = " + charset);
      }
    });
    
    boolean done = false ;
    boolean isAscii = true ;
    
    while( (available = buffer.read(data)) > -1){
      if (isAscii)
        isAscii = det.isAscii(data,available);

      // DoIt if non-ascii and not done yet.
      if (!isAscii && !done)
        done = det.DoIt(data,available, false);
    }   
    
    det.DataEnd();

    if (isAscii) {
      System.out.println("CHARSET = ASCII");
      found = true ;
    }

    if (!found) {
      String prob[] = det.getProbableCharsets() ;
      for(int i=0; i<prob.length; i++) {
        System.out.println("Probable Charset = " + prob[i]);
      }
    }
    input.close();
  }
  
  
}
