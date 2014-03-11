/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.miming;

import java.io.File;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 11, 2009  
 */
public class TestConvertTempData {
  public static void main(String[] args) throws Throwable {
    File file = new File("D:\\java\\headvances3\\trunk\\vietspider\\startup\\src\\test\\data\\");
    System.setProperty("vietspider.data.path", file.getCanonicalPath());
    
//    TpTempDatabase2 database2 = new TpTempDatabase2();
//    TpTempDatabase database1 = new TpTempDatabase();
//    database1.convert(database2);
  }
}
