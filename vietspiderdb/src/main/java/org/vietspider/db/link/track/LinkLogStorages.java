/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db.link.track;

import java.io.File;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.vietspider.common.Application;
import org.vietspider.common.Install;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.UtilFile;
import org.vietspider.common.text.CalendarUtils;
import org.vietspider.common.text.NameConverter;
import org.vietspider.model.Source;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 28, 2010  
 */
public class LinkLogStorages extends Thread {

  private static LinkLogStorages INSTANCE;

  public synchronized final static LinkLogStorages getInstance() {
    if(INSTANCE != null) return INSTANCE;
    INSTANCE = new LinkLogStorages();
    return INSTANCE;
  }

  private Map<String, LogFileWriter> holder = new ConcurrentHashMap<String, LogFileWriter>();

  protected volatile Queue<LinkLog> waitData = new ConcurrentLinkedQueue<LinkLog>();
  private SourceTrackerStorage trackerStorage;

  private String current;
  private int date;

  public LinkLogStorages() {
    Application.addShutdown(new Application.IShutdown() {

      public String getMessage() { return "Close Link Log Database";}

      public void execute() { closes(); }
    });

    Calendar calendar = Calendar.getInstance();
    current  = CalendarUtils.getFolderFormat().format(calendar.getTime());
    try {
      trackerStorage = new SourceTrackerStorage(current);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    date = calendar.get(Calendar.DATE);

    this.start();
  }

  public void sourceNull(String group, String category, String name) {
    LinkLog linkLog = new LinkLog(group+"."+category+"."+name, "");
    linkLog.setMessage("{source.not.found}");
    save(linkLog);
  }

  public void sourceNull(String fullName) {
    LinkLog linkLog = new LinkLog(fullName, "");
    linkLog.setMessage("{source.not.found}");
    save(linkLog);
  }

  public void entryNull(String fullName) {
    LinkLog linkLog = new LinkLog(fullName, "");
    linkLog.setMessage("{entry.not.found}");
    save(linkLog);
  }

  public void save(LinkLog log) {
    if(Application.LICENSE == Install.PERSONAL) return;
    waitData.add(log);
  }

  public void save(Source source, String message) {
    if(Application.LICENSE == Install.PERSONAL) return;
    String [] homepages = source.getHome();
    for(int i = 0; i < homepages.length; i++) {
      LinkLog log = new LinkLog(source.getFullName(), homepages[i]);
      log.setMessage(message);
      log.setLevel(0);
      log.setType(LinkLog.TYPE_HOME);
      log.setPhase(LinkLog.PHASE_SOURCE);
      waitData.add(log);
    }
  }

  public void save(Source source, String message, String url) {
    if(Application.LICENSE == Install.PERSONAL) return;
    LinkLog log = new LinkLog(source.getFullName(), url);
    log.setMessage(message);
    log.setLevel(0);
    log.setType(LinkLog.TYPE_HOME);
    log.setPhase(LinkLog.PHASE_SOURCE);
    waitData.add(log);
  }
  
  public void save(Source source, Throwable e, String url) {
    if(Application.LICENSE == Install.PERSONAL) return;
    LinkLog log = new LinkLog(source.getFullName(), url);
    log.setMessage(e.toString());
    log.setThrowable(e);
    log.setLevel(0);
    log.setType(LinkLog.TYPE_HOME);
    log.setPhase(LinkLog.PHASE_SOURCE);
    waitData.add(log);
  }

  public void save(Source source, Throwable exception) {
    if(Application.LICENSE == Install.PERSONAL) return;
    String [] homepages = source.getHome();
    for(int i = 0; i < homepages.length; i++) {
      LinkLog log = new LinkLog(source.getFullName(), homepages[i]);
      log.setMessage(exception.toString());
      log.setThrowable(exception);
      log.setLevel(0);
      log.setType(LinkLog.TYPE_HOME);
      log.setPhase(LinkLog.PHASE_SOURCE);
      waitData.add(log);
    }
  }

  public void run() {
    while(true) {
      //                  System.out.println("  ==== truoc >"+ waitData.size());
      while(!waitData.isEmpty()) {
        LinkLog dataLog = waitData.poll();
        LogFileWriter storage = getCurrent(dataLog.getChannel());
        try {
          if(storage != null) storage.save(dataLog);
        } catch (Exception e) {
          LogService.getInstance().setMessage(e, e.toString());
        }
        if(trackerStorage != null) trackerStorage.add(dataLog);
      }
      
      try {
        if(trackerStorage != null 
            && trackerStorage.isCommit()) trackerStorage.commit();
      } catch (Throwable e) {
        LogService.getInstance().setThrowable(e);
      }
      //                  System.out.println("  ==== sau >"+ waitData.size());

      closeExpires();

      Calendar calendar = Calendar.getInstance();
      if(calendar.get(Calendar.DATE) != date) {
        //export 
//        if(current != null) {
//          SourceTrackerStorage storage = null;
//          try {
//            storage = new SourceTrackerStorage(current);
//          } catch (Exception e) {
//            LogService.getInstance().setThrowable(e);
//          }
//          if(storage != null)  {
//            storage.export();
//            storage.close();
//          }
//        }
        
        current  = CalendarUtils.getFolderFormat().format(calendar.getTime());

        if(trackerStorage != null) {
          trackerStorage.export();
          trackerStorage.close();
        }

        try {
          trackerStorage = new SourceTrackerStorage(current);
        } catch (Exception e) {
          LogService.getInstance().setThrowable(e);
        }
        
        date = calendar.get(Calendar.DATE);
      }

      try {
        Thread.sleep(5*1000l);
      } catch (Exception e) {
        LogService.getInstance().setMessage(e, e.toString());
      }
    }
  }

  public synchronized LogFileWriter getCurrent(String fullName) {
    return getWriter(current, fullName);
  }

  public synchronized LogFileWriter getWriter(String dateFolder, String fullName) {
    if(Application.LICENSE == Install.PERSONAL) return null;
    String fileName = NameConverter.encode(fullName);
    String key = dateFolder + "/" + fileName;
    LogFileWriter storage = holder.get(key);
    if(storage != null && !storage.isTimeout()) return storage; 
    try {
      storage = new LogFileWriter(dateFolder, fileName);
      holder.put(key, storage);
      return storage;
    } catch (Exception e) {
      LogService.getInstance().setMessage(e, e.toString());
    }  
    return null;
  }

  public void closeExpires()  {
    Iterator<Map.Entry<String, LogFileWriter>> iterator = holder.entrySet().iterator();
    while(iterator.hasNext()) {
      LogFileWriter tracker = iterator.next().getValue();
      if(tracker.isTimeout()) {
        tracker.close();
        iterator.remove();
      } 
    }
  }

  void closes() {
    while(!waitData.isEmpty()) {
      //      System.out.println(" chay vao day roi "+ waitData.size());
      LinkLog dataLog = waitData.poll();
      LogFileWriter storage = getCurrent(dataLog.getChannel());
      try {
        if(storage != null) storage.save(dataLog);
      } catch (Exception e) {
        LogService.getInstance().setMessage(e, e.toString());
      }
    }
    
    try {
      if(trackerStorage != null 
          && trackerStorage.isCommit()) trackerStorage.commit();
    } catch (Throwable e) {
      LogService.getInstance().setThrowable(e);
    }
    
    if(trackerStorage != null) trackerStorage.close();

    Iterator<Map.Entry<String, LogFileWriter>> iterator = holder.entrySet().iterator();
    while(iterator.hasNext()) {
      LogFileWriter tracker = iterator.next().getValue();
      tracker.close();
      iterator.remove();
    }
  }

  public synchronized void export(String dateFolder) {
    if(dateFolder == null || dateFolder.trim().isEmpty()) return;
    if(dateFolder.charAt(dateFolder.length() - 1) == '/') {
      dateFolder = dateFolder.substring(0, dateFolder.length() - 1);
    }
//    System.out.println(" export  + "+ dateFolder);

    if(dateFolder.equals(current)) {
      if(trackerStorage != null) trackerStorage.export();
      return;
    }
    
    
    File folder = UtilFile.getFolder("track/logs/sources/" + dateFolder);
    File file = new File(folder, "summary.txt");
    if(file.exists()) return;

    SourceTrackerStorage storage = null;
    try {
      storage = new SourceTrackerStorage(dateFolder);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    
    if(storage == null) return;
    
    storage.export();
    storage.close();
  }
}
