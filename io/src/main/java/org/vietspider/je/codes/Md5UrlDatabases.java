/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.je.codes;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.vietspider.common.Application;
import org.vietspider.common.Install;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.MD5Hash;
import org.vietspider.common.io.UtilFile;
import org.vietspider.common.text.CalendarUtils;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 10, 2009  
 */
public class Md5UrlDatabases {

  protected  volatile List<IUrlDatabase> databases;
  protected volatile IUrlDatabase current;
  protected int currentDate = -1;

  private volatile boolean isClose = false;

  protected String folder_path;
  protected String type;
  protected long expire_db = 1*24*60*60*1000l;
  private static final long MAX_DATABASE_SIZE = 500*1000l;

  protected long cachedSize = -1;//5*1024*1024 

  protected String group;

  protected int max = 30;

  protected volatile long lastAccess = System.currentTimeMillis();

  public long getLastAccess() { return lastAccess; }

  public Md5UrlDatabases(String folderPath, String type_, 
      String group_, long expire_db_date, int max,  long cachedSize) throws Exception {
    this.folder_path = folderPath;
    this.type = type_;
    this.group = group_;
    this.cachedSize = cachedSize;
    this.expire_db = expire_db_date;
    this.max = max;
    databases = new ArrayList<IUrlDatabase>();

    if(Application.LICENSE == Install.SEARCH_SYSTEM) {
      Md5UrlCached.CACHED_SIZE = 100000;
    }

    File folder = UtilFile.getFolder(folderPath + "/");
//        System.out.println("===== > "+ folder.getAbsolutePath());
    File [] files = UtilFile.listFiles(folder, new FileFilter(){
      @Override
      public boolean accept(File f) {
        return f.isDirectory();
      }
    });

    Calendar calendar = Calendar.getInstance();

    if(files.length > 0) {
      current = createCurrentDb(files[0], true);
      int i = current.getName().equalsIgnoreCase(files[0].getName()) ? 1 : 0; 
      for(;i < Math.min(max, files.length); i++) {
        try {
          addDatabaseToList(files[i]);
        } catch (Exception e) {
          LogService.getInstance().setThrowable(group, e);
        }
      }

      for(;i < files.length; i++) {
        UtilFile.deleteFolder(files[i]);
      }
    } else {
      current = createCurrentDb(calendar.getTime(), true);
    }
    
//    System.out.println(" add database type "+ current.getClass().getSimpleName());

    currentDate = calendar.get(Calendar.DATE);

    //    codes = new ConcurrentHashMap<MD5Hash, Integer>();
  }

  private IUrlDatabase createCurrentDb(File folder, boolean writeAllowed) throws Exception {
    Date date = CalendarUtils.getFolderFormat().parse(folder.getName());
    return createCurrentDb(date, writeAllowed);
  }

  private IUrlDatabase createCurrentDb(Date date, boolean writeAllowed) throws Exception {
    Calendar calendar = Calendar.getInstance();
    if(date == null) return createCurrentDb(calendar.getTime(), writeAllowed);
    if(calendar.getTimeInMillis() - date.getTime() >= expire_db) {
      return createCurrentDb(calendar.getTime(), writeAllowed);
    }

    //move from date to month
    String name = CalendarUtils.getFolderFormat().format(date.getTime());
    File folder = UtilFile.getFolder(folder_path + "/" + name+ "/");

    Md5UrlDatabase database = new Md5UrlDatabase(folder, name, type, cachedSize, writeAllowed);
    if(database.size() >= Md5UrlCached.CACHED_SIZE) return database;
//    Md5UrlCached md5UrlCached = Md5UrlCacheds.getInstance().getCached(folder.getAbsolutePath());
//    if(md5UrlCached != null) return md5UrlCached;
    try {
      return new Md5UrlCached(database, folder, name, type);
//      Md5UrlCacheds.getInstance().put(md5UrlCached);
//      return md5UrlCached;
    } catch (Throwable e) {
      LogService.getInstance().setThrowable(e);
      return database;
    }
  }

  public void nextDatabase() throws Exception {
    Calendar calendar = Calendar.getInstance();
    int d = calendar.get(Calendar.DATE);
    if(d == currentDate) return;
    currentDate = d;

    Date date = CalendarUtils.getFolderFormat().parse(current.getName());
    if(calendar.getTimeInMillis() - date.getTime() < expire_db 
        && current.size() < MAX_DATABASE_SIZE) return;

    //    try {
    //      current.close();
    //    } catch (Throwable e) {
    //      LogService.getInstance().setThrowable(group, e);
    //    }
    //    addDatabase(createCurrentDb(date, false));
    addDatabase(0, current);
    current = createCurrentDb(calendar.getTime(), true);
  }

  private void addDatabaseToList(File folder) throws Exception  {
    String name  = folder.getName();
    if(name.equals(current.getName())) return;
    for(int i = 0; i < databases.size(); i++) {
      if(name.equals(databases.get(i).getName())) return;
    }
    
//    System.err.println(" =================  > call here");

    Md5UrlDatabase database = new Md5UrlDatabase(folder, name, type, cachedSize, false);
    if(database.size() >= Md5UrlCached.CACHED_SIZE) {
      addDatabase(-1, database);
    } else {
//      Md5UrlCached md5UrlCached = Md5UrlCacheds.getInstance().getCached(folder.getAbsolutePath());
      try {
//        if(md5UrlCached  == null) {
        Md5UrlCached md5UrlCached = new Md5UrlCached(database, folder, name, type);
//        }
//        Md5UrlCacheds.getInstance().put(md5UrlCached);
        addDatabase(-1, md5UrlCached);
      } catch (Throwable e) {
        LogService.getInstance().setThrowable(e);
        addDatabase(-1, database);
      }
    }
    //    addDatabase(new Md5UrlDatabase(folder, name, type, cachedSize));
  }

  public void close() {
    isClose = true;

    //    System.out.println(" chuan bi "+ codes.size());
    try {
      if(current != null) current.close();
    } catch (Throwable e) {
      LogService.getInstance().setThrowable(group, e);
    }

    for(int i = 0; i < databases.size(); i++) {
      try {
        databases.get(i).close();
      } catch (Throwable e) {
        LogService.getInstance().setThrowable(group, e);
      }
    }
    current = null;
    databases.clear();
  }

  public void write(MD5Hash hash, int post) throws Throwable {
    if(isClose) return;
    lastAccess = System.currentTimeMillis();
    current.save(hash, post);
    //    System.out.println("===== > write " + source + " : " + addressCode+ " post ");
    //    codes.put(hash, post);
    //    tempSize++;
  }

  public synchronized int read(MD5Hash hash, boolean resave) throws Throwable {
    if(isClose) return -1;

    lastAccess = System.currentTimeMillis();

    //    Integer cacheValue = codes.get(hash);
    //    if(cacheValue != null) return cacheValue.intValue();

    int value = current.search(hash);
    //    if(value > 0)  System.out.println(current.getName() + " : " + value);
    if(value > -1) return value;

    //    System.out.println("===== > "+databases.size());
    for(int i = 0; i < databases.size() ; i++) {
      if(databases.get(i) == current) continue;
      value = databases.get(i).search(hash);
      //      System.out.println(" doc den database thu i "+ databases.get(i).getName() + " : " + value+ " on "+ i);
      if(value < 0) continue;
      if(resave) current.save(hash, value);

      /* codes.put(hash, value);
      tempSize++;*/

      return value;
    }

    return -1;
  }

  public void remove(MD5Hash hash) throws Throwable {
    if(isClose) return;
    lastAccess = System.currentTimeMillis();
    current.remove(hash);
    for(int i = 0; i < databases.size() ; i++) {
      if(databases.get(i) == current) continue;
      databases.get(i).remove(hash);
    }
    //    System.out.println("===== > write " + source + " : " + addressCode+ " post ");
    //    codes.put(hash, post);
    //    tempSize++;
  }


  private void addDatabase(int index, IUrlDatabase newDatabase) {
    if(index < 0) {
      databases.add(newDatabase);
    } else {
      databases.add(index, newDatabase);
    }
    closeExpire();
  }

  private void closeExpire() {
    int size  = databases.size() - max;
    if(size  < 1) return;

    for(int i = max; i < databases.size(); i++) {
      try {
        databases.get(i).close();
      } catch (Throwable e) {
        LogService.getInstance().setThrowable(group, e);
      }
    }

    Iterator<IUrlDatabase> iterator = databases.iterator();
    while(iterator.hasNext()) {
      IUrlDatabase db = iterator.next();
      if(db.isClose()) iterator.remove();
    }

  }

  public boolean isClose() { return isClose; }

  public String getGroup() { return group; }

  public void commit(boolean check) throws Throwable {
    if(current == null) return;
    current.commit(check);
  }

}
