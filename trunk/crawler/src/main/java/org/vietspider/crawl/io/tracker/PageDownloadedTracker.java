/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.io.tracker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.vietspider.common.Application;
import org.vietspider.common.Install;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.MD5Hash;
import org.vietspider.common.text.NameConverter;
import org.vietspider.crawl.CrawlingSources;
import org.vietspider.crawl.link.Link;
import org.vietspider.je.codes.Md5UrlDatabases;
import org.vietspider.link.V_URL;
import org.vietspider.model.Source;
import org.vietspider.model.SourceUtils;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 12, 2008  
 */
public class PageDownloadedTracker extends Thread {
  
  private volatile static PageDownloadedTracker dtracker;
  
  public synchronized static PageDownloadedTracker getInstance() {
    if(dtracker == null) {
      dtracker = new PageDownloadedTracker();
      dtracker.start();
    }
    return dtracker;
  }
  
  private final static long TIMEOUT = 15*60*1000L;
  
  private PageDownloadedTracker() {
    Application.addShutdown(new Application.IShutdown() {

      public String getMessage() { return "Close URL Tracker Database"; }

      public int getPriority() { return 2; }

      public void execute() {
        excute = false;
        Iterator<Map.Entry<String, Md5UrlDatabases>> iterator = holder.entrySet().iterator();
        while(iterator.hasNext()) {
          Map.Entry<String, Md5UrlDatabases> entry = iterator.next();
          Md5UrlDatabases tracker = entry.getValue();
//          tracker.writeTemp();
          try {
            tracker.commit(false);
          } catch (Throwable e) {
            LogService.getInstance().setThrowable(e);
          }
          tracker.close();
        }
      }
    });
  }
  
//  public volatile  static int MAX_DATE = 31;
  
  private ConcurrentHashMap<String, Md5UrlDatabases> holder = new ConcurrentHashMap<String, Md5UrlDatabases>();
  
  private ConcurrentLinkedQueue<String> closeTrackers =  new ConcurrentLinkedQueue<String>();
  
  private volatile boolean excute = true;
  
  public void run() {
    while(excute) {
      
//      System.out.println(" truoc khi xu ly "+ holder.size());
      
      try {
        process();
      } catch (Throwable e) {
        LogService.getInstance().setThrowable(e);
      }
      
//      System.out.println(" sau khi xu ly "+ holder.size() + " \n\n");
      
      try {
        Thread.sleep(30*1000l) ;
      } catch(Exception ex) {
      }
    }
  }
  
  private void process() throws Throwable {
    List<String> removes = new ArrayList<String>();
    
    Iterator<Map.Entry<String, Md5UrlDatabases>> iterator = holder.entrySet().iterator();
    while(iterator.hasNext()) {
      Map.Entry<String, Md5UrlDatabases> entry = iterator.next();
      String key = entry.getKey();
      Md5UrlDatabases tracker = entry.getValue();
      
//      System.out.println(" close and remove by timeout " 
//          + key + " : "+ tracker.hashCode()+ " : "+ tracker.getGroup());
      
      if(System.currentTimeMillis() - tracker.getLastAccess() >= TIMEOUT) {
        removes.add(key);
        closeTrackers.remove(key);
//        System.out.println(" close and remove by timeout "+ key + " : "+ tracker.hashCode());
      } else if(tracker.isClose()) {
        iterator.remove();
        closeTrackers.remove(key);
      } else if(closeTrackers.contains(key)) {
        removes.add(key);
        closeTrackers.remove(key);
      } else {
        try {
          tracker.commit(true);
        } catch (Throwable e) {
          LogService.getInstance().setThrowable(e);
        }
        try {
          tracker.nextDatabase();
        } catch (Throwable e) {
          LogService.getInstance().setThrowable(e);
        }
      }
    }
    
//    LogService.getInstance().setMessage("TRACKER", null, "Before: holder size " + holder.size());
    
    for(int i = 0; i < removes.size(); i++) {
      String key = removes.get(i);
      Md5UrlDatabases tracker = holder.get(key);
      holder.remove(key);
      if(tracker == null) continue;
      try {
        tracker.commit(false);
      } catch (Throwable e) {
        LogService.getInstance().setThrowable(e);
      }
      tracker.close();
      //      LogService.getInstance().setMessage("TRACKER", null, "Close: " + key + "/"+ tracker.isClose());
      //      System.out.println("=======+++ = > "+ key);
    }
    
    if(removes.size() > 0) Runtime.getRuntime().gc();

    /*iterator = holder.keySet().iterator();
    while(iterator.hasNext()) {
      String key = iterator.next();
      System.out.println("===== > "+key);
    }*/
//    LogService.getInstance().setMessage("TRACKER", null, "Removed: holder size " + holder.size());
  }
  
  public void close(final Source source) {
//    System.out.println(" se close "+ source.getCodeName());
    closeTrackers.add(SourceUtils.getCodeName(source));
  }
  
  public void registry(final Source source) {
    new Thread() {
      public void run() {
        createNormalTracker(source.getGroup(), SourceUtils.getCodeName(source));
      } 
    }.start() ; 
  }
  
  private synchronized Md5UrlDatabases createNormalTracker(String group, String codeName) {
    Md5UrlDatabases tracker = holder.get(codeName);
    if(tracker != null && !tracker.isClose()) return tracker;
    try {
      String name  = NameConverter.encode(codeName);
      int max_db_instance = 2;
      long cachedSize = 1*1024*1024l;
      if(Application.LICENSE == Install.PROFESSIONAL) {
        max_db_instance = 3;
        cachedSize = 5*1024*1024l;
      } else if(Application.LICENSE == Install.ENTERPRISE) {
        max_db_instance = 6;
        cachedSize = 10*1024*1024l;
      } else if(Application.LICENSE == Install.SEARCH_SYSTEM) {
        max_db_instance = 12;
        cachedSize = 50*1024*1024l;
      }
     /* if("CLASSIFIED".equalsIgnoreCase(group)) {
        tracker = new Md5UrlDatabases("track/site/" + group + "/" + name, 
            "code", codeName, 3*24*60*60*1000l, max_db_instance, 2*1024*1024l);
      } else if("FORUM".equalsIgnoreCase(group)) {
//        int max = CrawlerConfig.EXPIRE_DATE/3+1;
        tracker = new Md5UrlDatabases("track/site/" + group + "/" + name, 
            "code", codeName, 30*24*60*60*1000l, max_db_instance, 5*1024*1024l);  
      }*/
      
      tracker = new Md5UrlDatabases("track/site/" + group + "/" + name, 
          "code", codeName, 7*24*60*60*1000l, max_db_instance, cachedSize);  
      
//      tracker = new NormalMd5UrlDatabases(group, NameConverter.encode(codeName), codeName, max);
      holder.put(codeName, tracker);
//    } catch (RecoveryException e) {
//      LogService.getInstance().setThrowable(codeName, e.getCause());
//      LogService.getInstance().setThrowable(codeName, e);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(codeName, e);
    }
    return tracker;
   
  //@TODO remove at next version build 14
//    createSimpleTracker(group);
  }
  
//@TODO remove at next version build 14
 /* private synchronized void createSimpleTracker(String group) {
    File folder = new File(UtilFile.getFolder("track"), "url/");
    if(!folder.exists()) return;
    
    Preferences preferences = Preferences.userNodeForPackage(PageDownloadedTracker.class);
    long last = System.currentTimeMillis();
    try {
      String value = preferences.get("code.last.update", "");
      if(value == null || value.trim().isEmpty()) {
        preferences.put("code.last.update", String.valueOf(last));
      } else {
        last = Long.parseLong(value);
      }
    } catch (Exception e) {
      preferences.put("code.last.update", String.valueOf(last));
    }
    
    if(System.currentTimeMillis() - last >= 10*24*60*60*1000) {
      UtilFile.deleteFolder("track/url/");
      return; 
    }
      
    Md5UrlDatabases tracker = holder.get("URL/"+group);
    if(tracker != null && !tracker.isClose()) return;
    try {
      tracker = new SimpleMd5UrlDatabases(group);
    } catch (RecoveryException e) {
      LogService.getInstance().setThrowable(group, e.getCause());
      LogService.getInstance().setThrowable(group, e);
      return;
    } catch (Exception e) {
      LogService.getInstance().setThrowable(group, e);
      return;
    }
    holder.put("URL/"+group, tracker);
  }*/
  
  private synchronized Md5UrlDatabases getNormalTracker(Link link) {
    Source source = CrawlingSources.getInstance().getSource(link.getSourceFullName());
    String codeName = SourceUtils.getCodeName(source);
    Md5UrlDatabases tracker = holder.get(codeName);
    if(tracker == null || tracker.isClose()) registry(source);
    return tracker; 
  }
  
 /* private synchronized SimpleMd5UrlDatabases getSimpleTracker(Link link) {
    String group = link.getSource().getGroup();
    Md5UrlDatabases tracker = holder.get("URL/"+group);
    return (SimpleMd5UrlDatabases)tracker; 
  }*/
  
  public static boolean searchUrl(Link link, boolean resave) throws Throwable {
    Md5UrlDatabases tracker = getInstance().getNormalTracker(link);
//    System.out.println("thay co tracker "+ tracker);
    if(tracker == null) return false;
    if(tracker.read(link.getUrlId(), resave) == 1) return true;
    
    //@TODO remove at next version build 14
//    SimpleMd5UrlDatabases simple  = getInstance().getSimpleTracker(link);
//    if(simple == null) return false;
    
//    if(!simple.search(link)) return false;
//    System.out.println(" da thay "+ link.getAddress());
    
//    tracker.write(link.getUrlId(), 1);
    
    return false;
  }
  
  public static void saveUrl(Link link) throws Throwable {
    Md5UrlDatabases tracker = getInstance().getNormalTracker(link);
    if(tracker == null) return;
    tracker.write(link.getUrlId(), 1);
  }
  
  public static int searchCode(Link link, boolean resave) throws Throwable {
    Md5UrlDatabases tracker = getInstance().getNormalTracker(link);
    //tracker == null no save or downloaded site
    if(tracker == null) return 1;
    return tracker.read(link.getUrlId(), resave);
  }
  
  public static void saveCode(Link link, int code) throws Throwable {
    Md5UrlDatabases tracker = getInstance().getNormalTracker(link);
    if(tracker == null) return;
    tracker.write(link.getUrlId(), code);
  }
  
  public static void saveTitle(Link link) throws Throwable {
    Md5UrlDatabases tracker = getInstance().getNormalTracker(link);
    if(tracker == null) return;
    tracker.write(link.getTitleId(), 1);
  }
  
  public static boolean searchTitle(Link link, boolean resave) throws Throwable {
    Md5UrlDatabases tracker = getInstance().getNormalTracker(link);
    if(tracker == null) return true;
    return tracker.read(link.getTitleId(), resave) != -1;
  }
  
  public static void removeUrl(String group, String url) throws Throwable {
//    System.out.println(" chuan bi remove roi "+ url);
    if(url == null || (url = url.trim()).isEmpty()) return;
    String codeName = SourceUtils.getCodeName(url);
    Md5UrlDatabases tracker = getInstance().createNormalTracker(group, codeName);
    if(tracker == null) return;
//    System.out.println(tracker+ "  === > chuan bi remove roi "+ url);
    
    StringBuilder builder = new StringBuilder(url);
    String normalizeURL = new V_URL(builder.toString(), null).toNormalize();
    tracker.remove(MD5Hash.digest(normalizeURL.toLowerCase()));
  }
  
}
