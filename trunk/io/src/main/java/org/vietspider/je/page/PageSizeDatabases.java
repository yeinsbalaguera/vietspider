/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.je.page;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.vietspider.common.io.LogService;
import org.vietspider.common.io.MD5Hash;
import org.vietspider.common.io.UtilFile;
import org.vietspider.common.text.CalendarUtils;

import com.sleepycat.je.DeadlockException;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 10, 2009  
 */
public class PageSizeDatabases {
  
  protected  volatile List<IPageSizeDatabase> databases;
  protected volatile IPageSizeDatabase current;
  protected int currentDate = -1;

  private volatile boolean isClose = false;
  
  protected String folder_path;
  protected String type;
  protected long expire_db = 1*24*60*60*1000;
  
  protected long cachedSize = -1;//5*1024*1024 
  
  protected String group;
  
  protected int max = 30;
  
  protected volatile long lastAccess = System.currentTimeMillis();
  
  public long getLastAccess() { return lastAccess; }
  
  public PageSizeDatabases(String folderPath, String type_, 
      String group_, long expire_db, int max,  long cachedSize) throws Exception {
    this.folder_path = folderPath;
    this.type = type_;
    this.group = group_;
    this.cachedSize = cachedSize;
    this.expire_db = expire_db;
    this.max = max;
    databases = new ArrayList<IPageSizeDatabase>();

    File folder = UtilFile.getFolder(folderPath + "/");
    File [] files = UtilFile.listFiles(folder, new FileFilter(){
      @Override
      public boolean accept(File f) {
        return f.isDirectory();
      }
    });
    
    if(files.length > 0) {
      current = createCurrentDb(files[0], true);
      int i = 1; 
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
      current = createCurrentDb(Calendar.getInstance().getTime(), true);
    }

    Calendar calendar = Calendar.getInstance();
    currentDate = calendar.get(Calendar.DATE);
  }

  private IPageSizeDatabase createCurrentDb(File folder, boolean writeAllowed) throws Exception {
    Date date = CalendarUtils.getFolderFormat().parse(folder.getName());
    return createCurrentDb(date, writeAllowed);
  }

  private IPageSizeDatabase createCurrentDb(Date date, boolean writeAllowed) throws Exception {
    if(date == null) return createCurrentDb(Calendar.getInstance().getTime(), writeAllowed);
    Calendar calendar = Calendar.getInstance();
    if(calendar.getTimeInMillis() - date.getTime() >= expire_db) {
      return createCurrentDb(Calendar.getInstance().getTime(), writeAllowed);
    }

    String name = CalendarUtils.getFolderFormat().format(date.getTime());
    File folder = UtilFile.getFolder(folder_path + "/" + name+ "/");
    
    PageSizeDatabase database = new PageSizeDatabase(folder, name, type, cachedSize, writeAllowed);
    if(database.size() >= PageSizeCached.CACHED_SIZE) return database;
    try {
      return new PageSizeCached(database, folder, name, type);
    } catch (Throwable e) {
      LogService.getInstance().setThrowable(e);
      return database;
    }
  }
  
  protected void nextDatabase()  throws Exception {
    Calendar calendar = Calendar.getInstance();
    int d = calendar.get(Calendar.DATE);
    if(d == currentDate) return;
    currentDate = d;

    Date date = CalendarUtils.getFolderFormat().parse(current.getName());
    if(calendar.getTimeInMillis() - date.getTime() < expire_db 
        && current.size() < 5000000) return;

    try {
      current.close();
    } catch (Throwable e) {
      LogService.getInstance().setThrowable(group, e);
    }
    addDatabase(createCurrentDb(date, false));
    current = createCurrentDb(Calendar.getInstance().getTime(), true);
  }

  private void addDatabaseToList(File folder) throws Exception  {
    String name  = folder.getName();
    if(name.equals(current.getName())) return;
    for(int i = 0; i < databases.size(); i++) {
      if(name.equals(databases.get(i).getName())) return;
    }
    
    PageSizeDatabase database = new PageSizeDatabase(folder, name, type, cachedSize, false);
    if(database.size() >= PageSizeCached.CACHED_SIZE) {
      addDatabase(database);
    } else {
      try {
        addDatabase(new PageSizeCached(database, folder, name, type));
      } catch (Throwable e) {
        LogService.getInstance().setThrowable(e);
        addDatabase(database);
      }
    }
  }

  public void writeTemp() {
    try {
      nextDatabase();      
    } catch (DeadlockException e) {
      LogService.getInstance().setMessage(group, e, e.getMessage());
    } catch (Exception e) {
      LogService.getInstance().setThrowable(group, e);
    }
  }
  
  public void close() {
    isClose = true;
    
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

  public void write(MD5Hash hash, long post) throws Throwable {
    if(isClose) return;
    lastAccess = System.currentTimeMillis();
    current.update(hash, post);
  }

  public long read(MD5Hash hash) throws Throwable {
    if(isClose) return -1;
    lastAccess = System.currentTimeMillis();

    long value = current.search(hash);
    if(value > -1) return value;

    for(int i = 0; i < databases.size(); i++) {
      if(databases.get(i) == current) continue;
      value = databases.get(i).search(hash);
      if(value < 0) continue;
      current.update(hash, value);
      return value;
    }

    return -1;
  }
  
  private void addDatabase(IPageSizeDatabase newDatabase) {
    databases.add(newDatabase);
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
    
    Iterator<IPageSizeDatabase> iterator = databases.iterator();
    while(iterator.hasNext()) {
      IPageSizeDatabase db = iterator.next();
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
