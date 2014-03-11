/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.dict;

import java.io.File;

import org.vietspider.db.dict.VietnameseDictionary;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 21, 2009  
 */
public class TestConvertDictFromFileToDb {
  
  public static void main(String[] args) throws Exception {
    File file = new File("D:\\java\\headvances3\\trunk\\vietspider\\startup\\src\\test\\data\\");
    System.setProperty("vietspider.data.path", file.getCanonicalPath());
    System.out.println(VietnameseDictionary.getInstance().size());
    
    /*InputStream inputStream = VietnameseFullWords.class.getResourceAsStream("full.vn.dict.cfg");
    DataReader reader = RWData.getInstance();
    try {
      ByteArrayOutputStream arrayOutputStream = reader.loadInputStream(inputStream);
      byte [] bytes = arrayOutputStream.toByteArray();
      String data = new String(bytes, Application.CHARSET);
      String [] dict  = data.split(";");
      for(String ele : dict) {
        VietnameseDictDatabase.getInstance().save(ele, "");
      }
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }*/
    
  }
}
