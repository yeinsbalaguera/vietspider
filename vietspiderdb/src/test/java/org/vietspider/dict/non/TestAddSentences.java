/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.dict.non;

import java.io.File;
import java.util.List;

import org.vietspider.common.io.DataWriter;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.db.dict.non.DictSequenceSplitter;
import org.vietspider.db.dict.non.NonDictionaryIO;
import org.vietspider.db.dict.non.NonSignDictionary;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 21, 2009  
 */
public class TestAddSentences {

  private static void save(List<String> list)  throws Exception {
    for (int i = 0; i < list.size(); i++) {
      NonSignDictionary.getInstance().insert(list.get(i).toLowerCase());
    } 
  }

  private static void save(List<String> list, String name)  throws Exception {
    File folder = UtilFile.getFolder("system/dictionary/vn/non/sentences2/");
    File file = new File(folder, name);
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < list.size(); i++) {
      if(builder.length() > 0) builder.append('\n');
      builder.append(list.get(i));
    } 
    RWData.getInstance().save(file, builder.toString().getBytes("utf-8"));
  }

  public static void main(String[] args) throws Exception {
    File file = new File("D:\\java\\headvances3\\trunk\\vietspider\\startup\\src\\test\\data\\");
    System.setProperty("vietspider.data.path", file.getCanonicalPath());

    NonDictionaryIO nonDictionaryIO = new NonDictionaryIO();
    nonDictionaryIO.importFileNew("them3.txt");

    DictSequenceSplitter splitter = new DictSequenceSplitter();

    File folder = UtilFile.getFolder("system/dictionary/vn/non/sentences");
    File [] files = folder.listFiles();
    for(int i = 0; i < files.length; i++) {
      byte  [] bytes = RWData.getInstance().load(files[i]);
      String text = new String(bytes, "utf-8");
      List<String> sequences = splitter.split(text);
      
      save(sequences, files[i].getName());
//      save(sequences);
    }

  }
}
