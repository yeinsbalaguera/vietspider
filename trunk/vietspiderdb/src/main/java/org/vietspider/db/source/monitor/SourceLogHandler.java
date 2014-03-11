/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db.source.monitor;

import java.io.EOFException;
import java.io.File;
import java.util.Date;

import org.vietspider.cache.InmemoryCache;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.UtilFile;
import org.vietspider.common.text.CalendarUtils;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jun 9, 2009  
 */
public class SourceLogHandler /*extends Thread*/ {
  
  private static volatile SourceLogHandler INSTANCE;

  public static synchronized SourceLogHandler getInstance() {
    if(INSTANCE == null) {
      INSTANCE  = new SourceLogHandler();
    }
    return INSTANCE;    
  }

  private InmemoryCache<String, MenuInfo> caches;
  
  private SourceMonitorDatabases databases;
  
  private MonitorDataSaver saver;
  
//  private volatile boolean execute = true;
  
  protected SourceLogHandler()  {
    caches = new InmemoryCache<String, MenuInfo>("menu", 3);
    caches.setLiveTime(1*60);
    
    databases = new SourceMonitorDatabases();
    saver = new MonitorDataSaver(databases.getWaitData());
  }
  
  
  
  public SourceMonitorDatabases getDatabases() {
    return databases;
  }

  public MenuInfo loadData(String date) {
    MenuInfo menuInfo = null;
    String cacheKey = date;
    try {
      menuInfo = caches.getCachedObject(cacheKey);
    } catch (Exception e) {
      LogService.getInstance().setThrowable("WEB_LOG_TRACKER", e);
    }
    if(menuInfo != null ) return menuInfo.clone();

    menuInfo = new MenuInfo();

    try {
      Date instanceDate = CalendarUtils.getDateFormat().parse(date);
      date = CalendarUtils.getFolderFormat().format(instanceDate);
      File file = new File(UtilFile.getFolder("content/summary/"), date);
      if(file.exists()) (new SourceLogFileUtils()).loadFile(file, menuInfo);
      SourceMonitorDatabase database = databases.getDatabase(date);
      SourceLogExporter exporter = new SourceLogExporter(database, date);
      exporter.export();
      exporter.loadData(menuInfo);
//      if(database != null) database.loadDB(menuInfo);
    } catch (EOFException e) {
      LogService.getInstance().setMessage("WEB_LOG_TRACKER", e, null);
    } catch (Exception e) {
      LogService.getInstance().setThrowable("WEB_LOG_TRACKER", e);
    }
    
    try {
      caches.putCachedObject(cacheKey, menuInfo);
    } catch (Exception e) {
      LogService.getInstance().setThrowable("WEB_LOG_TRACKER", e);
    }
    return menuInfo;
  }
  
  public MonitorDataSaver getSaver() { return saver; }
  
  public File exportToFile(String dateFolder) {
    //  File file = new File(UtilFile.getFolder("content/summary/"), dateFolder);
    SourceMonitorDatabase database = databases.getDatabase(dateFolder);
    SourceLogExporter exporter = new SourceLogExporter(/*file,*/ database, dateFolder);
    //  boolean reexport = exportDates.remove(dateFolder);
    return exporter.export();
  }
  
  protected String getFolderData(String folderName) {
    File folder = null;
    try {
      folder = new File(UtilFile.getFolder("content/summary"), folderName+"_mdb"+"/");
      if(folder.exists())  return folder.getAbsolutePath();
    } catch (Exception e) {
      LogService.getInstance().setThrowable("WEB_LOG_TRACKER", e);
    }
    return null;
  }
  
  public SourceLog loadSourceLog(String date, String source) throws Exception  {
    Date instanceDate = CalendarUtils.getDateFormat().parse(date);
    date = CalendarUtils.getFolderFormat().format(instanceDate);
    
    SourceMonitorDatabase database = databases.getDatabase(date);
    if(database == null) return null;
    try {
      return database.search(source);
    } catch (Throwable e) {
      LogService.getInstance().setThrowable("WEB_LOG_TRACKER", e);
      return null;
    }
  }
}
