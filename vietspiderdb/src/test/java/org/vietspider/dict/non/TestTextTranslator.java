/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.dict.non;

import java.io.File;

import org.vietspider.common.io.DataWriter;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.db.dict.non.NonDictionaryIO;
import org.vietspider.db.dict.non.TextTranslator;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 21, 2009  
 */
public class TestTextTranslator {

  public static void main(String[] args) throws Exception {
    File file = new File("D:\\java\\headvances3\\trunk\\vietspider\\startup\\src\\test\\data\\");
    System.setProperty("vietspider.data.path", file.getCanonicalPath());

    TextTranslator translator = new TextTranslator();

    NonDictionaryIO nonDictionaryIO = new NonDictionaryIO();
    nonDictionaryIO.importFileNew("them.txt");
    
//    NonSignDictionary.getInstance().remove("kinh mong");
//    NonSignDictionary.getInstance().insert("kính mong");

    String text = "oi tau ngon lam day";
//    System.out.println(translator.compile(text));

    file  = UtilFile.getFile("system/dictionary/vn/non/", "input.txt");
    byte  [] bytes = RWData.getInstance().load(file);
    text = new String(bytes, "utf-8");
    
    file  = UtilFile.getFile("system/dictionary/vn/non/", "out.txt");
    org.vietspider.common.io.RWData.getInstance().save(file, translator.compile(text).getBytes("utf-8"));
    
  }
}
