/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.dict.non;

import java.io.File;

import org.vietspider.db.dict.non.NonDictionaryIO;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 15, 2009  
 */
public class TestNonDictionaryIO {
  
  public static void main(String[] args) throws Exception {
    File file = new File("D:\\java\\headvances3\\trunk\\vietspider\\startup\\src\\test\\data\\");
    System.setProperty("vietspider.data.path", file.getCanonicalPath());
    
    NonDictionaryIO nonDictionaryIO = new NonDictionaryIO();
//    nonDictionaryIO.exportSignFile();
//    nonDictionaryIO.exportSingleFile();
    
    nonDictionaryIO.importFile();
    
    nonDictionaryIO.exportFile();
    
//    nonDictionaryIO.importFileNew("them.txt");
  }
}
