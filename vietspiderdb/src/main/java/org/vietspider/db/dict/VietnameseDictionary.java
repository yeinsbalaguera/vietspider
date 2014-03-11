/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db.dict;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.SortedMap;

import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
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
public class VietnameseDictionary extends JeDatabase {
  
  private static volatile VietnameseDictionary instance;
  
  public static synchronized VietnameseDictionary getInstance() {
    if(instance != null) return instance;
    try {
      instance = new VietnameseDictionary();
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    return instance;
  }

  protected WordIndex2 wordIndex2;

  protected SortedMap<String, String> map;

  public SortedMap<String, String> getMap() { return map; }

  private VietnameseDictionary() throws Exception {
    EnvironmentConfig envConfig = new EnvironmentConfig();
    envConfig.setTransactional(false);
    envConfig.setAllowCreate(true);

    File folder = UtilFile.getFolder("system/dictionary/vn/");
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
    
    createWordIndex();

    Application.addShutdown(new Application.IShutdown() {

      public String getMessage() { return "Close URL Database";}

      public int getPriority() { return 2; }

      public void execute() {
        close();
      }
    });
  }
  
  public int size() { return map.size(); }

  public final boolean contains(String word) {
    if (isClose) return false;
    return wordIndex2.contains(word);
//    return map.containsKey(word);
  }

  public final String search(String word) {
    if (isClose) return null;
    return map.get(word);
  }

  public final void remove(String word) {
    if (isClose) return;
    map.remove(word);
    wordIndex2.remove(word);
    saveIndex();
  }

  public final void save(String word, String value) {
    if (isClose) return;
    map.put(word, value);
    wordIndex2.add(word);
    saveIndex();
  }
  
  public WordIndex2 getWordIndex() {
    if(wordIndex2 != null) return wordIndex2;
    createWordIndex();
    return wordIndex2;
  }
  
  private void createWordIndex() {
    File folder = UtilFile.getFolder("system/dictionary/vn/");
    File file = new File(folder, "word.index");
    if(file.exists() && file.length() > 0) {
      try {
        byte [] bytes = RWData.getInstance().load(file);
        ObjectInputStream objectInput = new ObjectInputStream(new ByteArrayInputStream(bytes));
        wordIndex2 = (WordIndex2) objectInput.readObject();
        objectInput.close();
        return;
      } catch (Exception e) {
        file.delete();
        LogService.getInstance().setThrowable(e);
      }
    }
    
    wordIndex2 = new WordIndex2(0);
    Iterator<String> iterator = map.keySet().iterator();
    while(iterator.hasNext()) {
      wordIndex2.add(iterator.next());
    }
    saveIndex();
  }
  
  public void saveIndex() {
    File folder = UtilFile.getFolder("system/dictionary/vn/");
    File file = new File(folder, "word.index");
    try {
      ByteArrayOutputStream bytesOutput = new ByteArrayOutputStream() ;
      ObjectOutputStream objectOutputStream = new ObjectOutputStream(bytesOutput) ;
      objectOutputStream.writeObject(wordIndex2);
      objectOutputStream.close();
      if(file.exists()) file.delete();
      RWData.getInstance().save(file, bytesOutput.toByteArray());
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }

}
