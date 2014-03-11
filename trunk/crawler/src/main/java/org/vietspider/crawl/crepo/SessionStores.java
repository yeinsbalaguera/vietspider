/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.crepo;

import java.io.File;
import java.io.FileFilter;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.vietspider.common.io.UtilFile;
import org.vietspider.crawl.CrawlingSession;
import org.vietspider.model.Source;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jun 22, 2009  
 */
public class SessionStores extends Thread {

  private final static SessionStores mananger = new SessionStores();

  public final static SessionStores getInstance() { return mananger; }

  public static SessionStore getStore(Integer keyCode) {
    return mananger.getSStore(keyCode);
  }

  public static void remove(Integer keyCode) {
    mananger.removeSStore(keyCode);
  }

  private volatile Map<Integer, SessionStore> stores;

  private SessionStores() {
    stores  = new ConcurrentHashMap<Integer, SessionStore>();
    start();
  }
  
  public void destroy() {
    Iterator<Map.Entry<Integer, SessionStore>> iterator = stores.entrySet().iterator();
    while(iterator.hasNext()) {
      Map.Entry<Integer, SessionStore> entry = iterator.next();
      SessionStore store = entry.getValue();
      store.endSession();
      iterator.remove();
    }
  }

  public void removeSStore(Integer keyCode) {
    SessionStore store = stores.remove(keyCode);
    if(store != null) store.endSession();
  }

  public void run() {
    while(true) {
//      System.out.println(" ==== ===========  >"+ stores.size());
      Iterator<Map.Entry<Integer, SessionStore>> iterator = stores.entrySet().iterator();
      while(iterator.hasNext()) {
        Map.Entry<Integer, SessionStore> entry = iterator.next();
        SessionStore store = entry.getValue();
//        System.out.println(" store "+ store.source.getFullName() + "  : "+ store.isTimeout());
        if(store.isTimeout()) {
          store.endSession();
          iterator.remove();
        } else {
          store.checkDownloaded();
        }
        //        else {
        ////          store.getTempLink().process();
        //          store.getCodes().expire();
        //        }
      }

      try {        
        Thread.sleep(10*1000);
      } catch (Exception e) {
      }
    }
  }

  private SessionStore getSStore(Integer keyCode) {
    //    System.out.println("tong so "+ stores.size());
    SessionStore store = stores.get(keyCode);
    if(store != null) store.setLastAccess();
    //    if(store != null) System.out.println(" thay co da duoc "+ store.hashCode()+ " : ");
    return store;
  }

  public SessionStore create(CrawlingSession executor, Source source) throws Throwable {
    SessionStore store = new SessionStore(executor, source);
    stores.put(source.getCodeName(), store);
    return store;
  }
  

  public void deleteExpireStore() {
    File folder = UtilFile.getFolder("track/link2/");
    File [] files = UtilFile.listModifiedFiles(folder, new FileFilter() {
      
      private long current = System.currentTimeMillis();
      private long expire  = 5*24*60*60*1000l;
      
      public boolean accept(File f) {
        return f.isFile() &&  current - f.lastModified() >= expire;
      }
    });
    
    if(files == null) return;
    
    for(int i = 0; i < files.length; i++) {
      files[i].delete();
    }
  }
  
}
