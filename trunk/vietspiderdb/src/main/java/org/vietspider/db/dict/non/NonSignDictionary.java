/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db.dict.non;

import java.io.File;
import java.util.List;
import java.util.SortedMap;

import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.UtilFile;
import org.vietspider.db.JeDatabase;
import org.vietspider.locale.vn.VietnameseConverter;

import com.sleepycat.bind.tuple.StringBinding;
import com.sleepycat.collections.StoredSortedMap;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 15, 2009  
 */
public class NonSignDictionary extends JeDatabase {

  private static volatile NonSignDictionary instance;

  public static synchronized NonSignDictionary getInstance() {
    if(instance != null) return instance;
    try {
      instance = new NonSignDictionary();
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    return instance;
  }

  protected SortedMap<String, String> map;
  private DictSequenceSplitter splitter = new DictSequenceSplitter();

  private NonSignDictionary() throws Exception {
    EnvironmentConfig envConfig = new EnvironmentConfig();
    envConfig.setTransactional(false);
    envConfig.setAllowCreate(true);

    File folder = UtilFile.getFolder("system/dictionary/vn/non/");
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

      public String getMessage() { return "Close non dictionary database";}

      public int getPriority() { return 2; }

      public void execute() {
        close();
      }
    });
  }

  public int size() { return map.size(); }

  public final NWord search(String key) {
    if (isClose) return null;
    String text = map.get(key);
    if(text == null || text.length() < 1) return null;
    NWord nWord = new NWord();
    try {
      nWord.fromText(text);
    } catch (Exception e) {
      map.remove(key);
      LogService.getInstance().setMessage(e, e.toString() + " = " + key);
    }
    return nWord;

  }

  public final void remove(String key) {
    if (isClose) return;
    map.remove(key);
  }
  
  public final void insertDoc(String text) {
    List<String> sequences = splitter.split(text);
    for (int i = 0; i < sequences.size(); i++) {
      insert(sequences.get(i).toLowerCase());
    }
  }
  
  public final void insert(String line) {
    int counter = count(line);
    if(counter > 25) return;
    String key = VietnameseConverter.toTextNotMarked(line);
    NWord nWord = new NWord();
    try {
//      System.out.println(key + ":[" + line + ":0:]");
      nWord.fromText(key + ":[" + line + ":0:]");
    } catch (Exception e) {
      LogService.getInstance().setMessage(e, e.toString() + " = " + line);
      return;
    }
    NonSignDictionary.getInstance().save(nWord);
  }

  public final void save(NWord word) {
    if (isClose) return;
    String value = map.get(word.getKey());
    if(value != null && value.length() > 0) {
      NWord old = new NWord();
      try {
        old.fromText(value);
        old.merge(word);
        map.put(old.getKey(), old.toText());
        return;
      } catch (Exception e) {
        LogService.getInstance().setMessage(e, e.toString() + " = " + word.getKey());
      }
    }
    map.put(word.getKey(), word.toText());
  }

  public final void clear() {
    if(isClose) return;
    map.clear();
  }
  
  private int count(String text) {
    int i = 0;
    int length = text.length();
    int counter = 1;
    while(i < length){
      char c = text.charAt(i);
      if(c == ' ') counter++;
      i++;
    }
    return counter;
  }

  public SortedMap<String, String> getMap() { return map; }


}
