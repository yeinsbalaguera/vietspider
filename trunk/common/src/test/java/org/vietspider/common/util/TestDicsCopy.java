/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.common.util;

import java.io.File;
import java.io.FileInputStream;

import org.vietspider.common.io.DataWriter;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 15, 2009  
 */
public class TestDicsCopy {
  public static void main(String[] args) throws Exception {
   org.vietspider.common.io.RWData writer = org.vietspider.common.io.RWData.getInstance();
    File file1 = new File("E:\\MPEGAV\\AVSEQ01.DAT");
    File file2 = new File("D:\\a.dat");
    FileInputStream input = new FileInputStream(file1);
//    FileOutputStream output = new FileOutputStream(file2);
    writer.save(file2, input);
//    writer.copy(file1, file2);
  }
}
