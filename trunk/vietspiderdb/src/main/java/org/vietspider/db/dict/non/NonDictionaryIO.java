/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db.dict.non;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeSet;

import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.RandomAccess;
import org.vietspider.common.io.UtilFile;
import org.vietspider.db.dict.VietnameseDictionary;
import org.vietspider.locale.vn.VietnameseConverter;
import org.vietspider.locale.vn.VietnameseSingleWords;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 15, 2009  
 */
public class NonDictionaryIO extends RandomAccess {
  
  public void importFileNew(String name) {
    File folder = UtilFile.getFolder("system/dictionary/vn/non/");
    File file = new File(folder, name);
    if(!file.exists() || file.length() < 1) return;

    RandomAccessFile random = null;
    try {
      random = new RandomAccessFile(file, "r");
      random.seek(0);
      while(true) {
        String line = readLine(random);
        if(line == null) break;
        line  = line.trim();
        if(line.length() < 1) continue;
        
        String key = VietnameseConverter.toTextNotMarked(line);
        NWord nWord = new NWord();
        try {
//          System.out.println(key + ":[" + line + ":0:]");
          nWord.fromText(key + ":[" + line + ":0:]");
        } catch (Exception e) {
          LogService.getInstance().setMessage(e, e.toString() + " = " + line);
          continue;
        }
        NonSignDictionary.getInstance().save(nWord);
      }
    } catch (IOException e) {
      LogService.getInstance().setThrowable(e);
    } finally {
      try {
        if(random != null) random.close();
      } catch (Exception e2) {
        LogService.getInstance().setThrowable(e2);
      }
    }
  }
  
  public void importFile() {
    File folder = UtilFile.getFolder("system/dictionary/vn/non/");
    File file = new File(folder, "non.dict.txt");
    if(!file.exists() || file.length() < 1) return;

//    NonSignDictionary.getInstance().getMap().clear();
    RandomAccessFile random = null;
    try {
      random = new RandomAccessFile(file, "r");
      random.seek(0);
      while(true) {
        String line = readLine(random);
        if(line == null) break;
        line  = line.trim();
        if(line.length() < 1) continue;
        NWord nWord = new NWord();
        try {
          nWord.fromText(line);
        } catch (Exception e) {
          LogService.getInstance().setMessage(e, e.toString() + " = " + line);
          continue;
        }
        NonSignDictionary.getInstance().save(nWord);
      }
    } catch (IOException e) {
      LogService.getInstance().setThrowable(e);
    } finally {
      try {
        if(random != null) random.close();
      } catch (Exception e2) {
        LogService.getInstance().setThrowable(e2);
      }
    }
  }
  
  public void exportFile() {
    File folder = UtilFile.getFolder("system/dictionary/vn/non/");
    File file = new File(folder, "non.dict."+String.valueOf(System.currentTimeMillis())+".txt");
    
    SortedMap<String, String> map = NonSignDictionary.getInstance().getMap();
    Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
    try {
      StringBuilder builder = new StringBuilder();
      while(iterator.hasNext()) {
        if(builder.length() > 1000) {
          RWData.getInstance().append(file, builder.toString().getBytes(Application.CHARSET));
          builder.setLength(0);
          builder.append('\n');
        } else {
          if(builder.length() > 0) builder.append('\n');
        }
        builder.append(iterator.next().getValue());
      }
      RWData.getInstance().append(file, builder.toString().getBytes(Application.CHARSET));
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }
  
  public void exportSignFile() {
    File folder = UtilFile.getFolder("system/dictionary/vn/non/");
    File file = new File(folder, "non.dict."+String.valueOf(System.currentTimeMillis())+".txt");
    
    SortedMap<String, String> map = VietnameseDictionary.getInstance().getMap();
    Iterator<String> iterator = map.keySet().iterator();
    StringBuilder builder = new StringBuilder();
    try {
      while(iterator.hasNext()) {
       String value  = iterator.next();
       if(value.indexOf(';') > -1
           || value.indexOf(':') > -1
           || value.indexOf('.') > -1) {
         iterator.remove();
         continue;
       }
       String key = VietnameseConverter.toTextNotMarked(value);
       String line  = key + ":[" + value + ":0:]";
       
       if(builder.length() > 1000) {
         RWData.getInstance().append(file, builder.toString().getBytes(Application.CHARSET));
         builder.setLength(0);
         builder.append('\n');
       } else {
         if(builder.length() > 0) builder.append('\n');
       }
       builder.append(line);
      }
      RWData.getInstance().append(file, builder.toString().getBytes(Application.CHARSET));
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }
  
  public void exportSingleFile() {
    File folder = UtilFile.getFolder("system/dictionary/vn/non/");
    File file = new File(folder, "non.dict."+String.valueOf(System.currentTimeMillis())+".txt");
    
    TreeSet<String> words = new VietnameseSingleWords().getDict();
    StringBuilder builder = new StringBuilder();
    try {
      Iterator<String> iterator = words.iterator();
      while(iterator.hasNext()) {
       String value  = iterator.next();
       String key = VietnameseConverter.toTextNotMarked(value);
       String line  = key + ":[" + value + ":0:]";
       
       if(builder.length() > 1000) {
         RWData.getInstance().append(file, builder.toString().getBytes(Application.CHARSET));
         builder.setLength(0);
         builder.append('\n');
       } else {
         if(builder.length() > 0) builder.append('\n');
       }
       builder.append(line);
      }
      RWData.getInstance().append(file, builder.toString().getBytes(Application.CHARSET));
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }
  
}
