/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.io;

import java.io.File;

import org.vietspider.common.io.DataReader;
import org.vietspider.common.io.RWData;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 22, 2008  
 */
public class TestFileLength {
  
  public static void main(String[] args) throws Exception {
    File file  = new File("D:\\Temp\\print.asp.htm");
    System.out.println(file.length());
    
    byte [] bytes = RWData.getInstance().load(file);
    String value = new String(bytes, "utf-8");
    System.out.println(value.length());
  }
  
}
