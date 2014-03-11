/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.io;

import java.io.File;
import java.io.FileInputStream;

import org.vietspider.common.io.RWData;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 24, 2007  
 */
public class TestDataReader {
  public static void main(String[] args) throws Exception {
    File file  = new File("F:\\Temp2\\Test\\file\\datapath.map");
    FileInputStream fileInputStream = new FileInputStream(file);
    byte [] dateData = new byte[10];
    byte [] name = new byte[6];
    while(true) {
      int available = fileInputStream.read(dateData);
      if(available < 0) break;
      System.out.println("Date data :"+ new String(dateData, 0, available));
      available = fileInputStream.read(name);
      if(available < 0) break;
      System.out.println("backup name :"+ new String(name, 0, available));
    }
    
    System.out.println("=================================================================");
    
    String value = new String(RWData.getInstance().load(file));
    String [] elements = value.split("BACKUP");
    for(String ele : elements) {
      System.out.println("=================> "+ele);
    }
  }
}

