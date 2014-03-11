/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.plugin.index;

import java.io.File;
import java.util.SortedMap;

import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.UtilFile;
import org.vietspider.db.JeDatabase;

import com.sleepycat.bind.tuple.StringBinding;
import com.sleepycat.collections.StoredSortedMap;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 21, 2009  
 */
class AbbreviateDictionary extends JeDatabase {

  private static volatile AbbreviateDictionary instance;

  private static synchronized  AbbreviateDictionary getInstance() {
    if(instance != null) return instance;
    try {
      instance = new AbbreviateDictionary();
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    return instance;
  }

  protected SortedMap<String, String> map;

  private AbbreviateDictionary() throws Exception {
    EnvironmentConfig envConfig = new EnvironmentConfig();
    envConfig.setTransactional(false);
    envConfig.setAllowCreate(true);

    File folder = UtilFile.getFolder("system/dictionary/vn/abbr/classified/");
    envConfig.setCacheSize(512*1024); 

    this.env = new Environment(folder, envConfig);
    DatabaseConfig dbConfig = new DatabaseConfig();
    dbConfig.setTransactional(false);
    dbConfig.setAllowCreate(true);
    dbConfig.setDeferredWrite(true);
    this.db = env.openDatabase(null, "dictionary", dbConfig);
    StringBinding keyBinding = new StringBinding();
    StringBinding dataBinding = new StringBinding();
    this.map = new StoredSortedMap<String, String> (db, keyBinding, dataBinding, true);

    Application.addShutdown(new Application.IShutdown() {

      public String getMessage() { return "Close URL Database";}

      public int getPriority() { return 2; }

      public void execute() {
        close();
      }
    });
  }

  public int size() { return map.size(); }

  public final String search(String word) {
    if (isClose) return null;
    return map.get(word);
  }

  public final void remove(String word) {
    if (isClose) return;
    map.remove(word);
  }

  public String compile(String text) {
    StringBuilder builder = new StringBuilder();
    int index = 0;
    int start = index;
    int length = text.length();
    while(index < length) {
      char c = text.charAt(index);
      if(!Character.isLetterOrDigit(c)) {
        String word = text.substring(start, index);
        String value = map.get(word.toLowerCase());
        builder.append(word);
        //        System.out.println("thay co "+ "|"+word+"|"+ value);
        if(value != null) {
          builder.append(' ').append('(').append(value).append(')');
        }
        builder.append(c);
        start = index+1;
      } 
      index++;
    }
    if(start < length)  {
      String word = text.substring(start, length);
      String value = map.get(word.toLowerCase());
      builder.append(word);
      //    System.out.println("thay co "+ "|"+word+"|"+ value);
      if(value != null) {
        builder.append('(').append(value).append(')');
      }
    }
    return builder.toString();
  }

  public final void save(String word, String value) {
    if (isClose) return;
    word = word.trim().toLowerCase();
    value = value.trim().toLowerCase();
    String ovalue = map.get(word);
    if(ovalue == null || ovalue.trim().isEmpty()) {
      map.put(word, value);
    } else { 
      if(ovalue.indexOf(value) > -1) return;
      map.put(word, ovalue+ ", " +value);
    }
  }

}
