/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db.website;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;

import org.vietspider.bean.website.Website;
import org.vietspider.bean.website.Websites;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.MD5Hash;
import org.vietspider.db.VDatabases;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 14, 2009  
 */
public class WebsiteDatabases extends VDatabases<WebsiteDatabase> implements IWebsiteDatabases {

  protected volatile java.util.Set<String> waitUrls = new ConcurrentSkipListSet<String>();
  protected volatile java.util.Queue<Website> waitWebsites = new ConcurrentLinkedQueue<Website>();

  public WebsiteDatabases() {
    super(5*60*1000L, 15*1000L);
  }
  
  @SuppressWarnings("unused")
  public void commit(boolean exit) {
    commitWebsites();
  }

  public void run() {
//    new DatabaseOptimization(this);
    
    while(execute) {
      commitUrls();
      commitWebsites();
      closeExpires();
      try {
        Thread.sleep(sleep);
      } catch (Exception e) {
      }
    }
  }
  
  public void saveWebsites(List<Website> websites) {
    for (int j = 0; j < websites.size(); j++) {
      waitWebsites.add(websites.get(j));
    }
  }
  
  public void save(Website website) {
    waitWebsites.add(website);
  }
  
  public void save(String..._urls) {
    Collections.addAll(waitUrls, _urls);
  }
  
  public void save(List<String> _urls) {
    waitUrls.addAll(_urls);
  }
  
  public void load(Websites websites) {
    WebsiteDatabase database = getDatabase(websites.getStatus(), websites.getLanguage());
    try {
      websites.setList(database.loadUrlByPage(websites.getPage(), websites.getPageSize()));
    } catch (Throwable e) {
      LogService.getInstance().setThrowable(e);
    }
    
    Iterator<Website> iterator = websites.getList().iterator();
    while(iterator.hasNext()) {
      Website website = iterator.next();
      if(filter(website.getHost(), 
          websites.getStatus(), websites.getLanguage())) {
        iterator.remove();
      }
    }
    
    websites.setTotalPage(database.getTotalPage(websites.getPageSize()));
  }
  
  private boolean filter(String host, int status, String lang) {
    Iterator<Website> iterator = waitWebsites.iterator();
    while(iterator.hasNext()) {
      Website website = iterator.next();
      if(website == null || website.getHost() == null) continue;
      if(website.getHost().equalsIgnoreCase(host)) {
        if(website.getStatus() != status) return true;
        if(!website.getLanguage().equalsIgnoreCase(lang)) return true;
      }
    }
    return false;
  }
  
  public Website search(String host)  {
    String _host = host.toLowerCase();
    
    Iterator<Website> iterator = waitWebsites.iterator();
    while(iterator.hasNext()) {
      Website waitWebsite = iterator.next();
      if(waitWebsite == null || waitWebsite.getHost() == null) continue;
      if(waitWebsite.getHost().equalsIgnoreCase(host)) return waitWebsite;
    }
    
    Website website = searchInternal(_host);
    if(website != null)  return website;
    if(_host.startsWith("www.")) {
      return searchInternal(_host.substring(4));
    } 
    return searchInternal("www."+ _host);
  }
  
  private Website searchInternal(String host)  {
    MD5Hash md5Hash = MD5Hash.digest(host);
    Website website = search(md5Hash, Website.UNAVAILABLE, "en");
    if(website != null) return website;
    website = search(md5Hash, Website.NEW_ADDRESS, "en");
    if(website != null) return website;
    website = search(md5Hash, Website.CONFIGURATION, "vn");
    if(website != null) return website;
    website = search(md5Hash, Website.NEW_ADDRESS, "vn");
    if(website != null) return website;
    website = search(md5Hash, Website.CONFIGURATION, "en");
    if(website != null) return website;
    website = search(md5Hash, Website.UNAVAILABLE, null);
    if(website != null) return website;
    website = search(md5Hash, Website.UNAVAILABLE, "vn");
    if(website != null) return website;
    website = search(md5Hash, Website.CONFIGURATION, null);
    if(website != null) return website;
    website = search(md5Hash, Website.NEW_ADDRESS, null);
    if(website != null) return website;
    return null;
  }
  
  private Website search(MD5Hash key, int type, String language) {
    WebsiteDatabase database = getDatabase(type, language);
    try {
      return database.searchWebsite(key);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    return null;
  }
  
  public void remove(String host)  {
    remove(MD5Hash.digest(host));
  }
  
  private void remove(MD5Hash md5Hash)  {
    remove(md5Hash, Website.UNAVAILABLE, "en");
    remove(md5Hash, Website.NEW_ADDRESS, "en");
    remove(md5Hash, Website.CONFIGURATION, "vn");
    remove(md5Hash, Website.NEW_ADDRESS, "vn");
    remove(md5Hash, Website.CONFIGURATION, "en");
    remove(md5Hash, Website.UNAVAILABLE, null);
    remove(md5Hash, Website.UNAVAILABLE, "vn");
    remove(md5Hash, Website.CONFIGURATION, null);
    remove(md5Hash, Website.NEW_ADDRESS, null);
  }
  
  private void remove(MD5Hash key, int type, String language) {
    WebsiteDatabase database = getDatabase(type, language);
    if(database == null) return;
    try {
      database.remove(key);
    } catch (Throwable e) {
      LogService.getInstance().setThrowable(e);
    }
  }
  
  synchronized void commitUrls()  {
    if(waitUrls == null || waitUrls.size() < 1) return;
    Iterator<String> iterator = waitUrls.iterator();
    while(iterator.hasNext()) {
      String url = iterator.next();
//      System.out.println(" commit url "+url);
      iterator.remove();
      String host = Website.toHost(url);
      if(host == null) continue;
      Website website = search(host);
      if(website != null) continue;
      
      website = new Website();
      website.setAddress(url);
      if(website.getHost() != null) waitWebsites.add(website);
    }
  }
  
  synchronized void commitWebsites()  {
    if(waitWebsites == null || waitWebsites.size() < 1) return;

    while(!waitWebsites.isEmpty()) {
      Website website = waitWebsites.poll();
      if(website == null || website.getHost() == null) continue;
      WebsiteDatabase newDatabase = getDatabase(website.getStatus(), website.getLanguage());
      
//      System.out.println(" chuan bi lam viec "+ website.getHost());
      remove(website.getHost());
      if(website.getHash() != null 
          && !website.getHash().trim().isEmpty()) {
        remove(new MD5Hash(website.getHash()));
      }
      
//      System.out.println(" ====>" + oldWebsite.getPath() + " : "+ oldDatabase);
//      System.out.println(newDatabase.getPath() + "/" +  website.getStatus());
      
//      System.out.println(" save website " + website);
      try {
        newDatabase.save(website);
      } catch (Throwable e) {
        LogService.getInstance().setThrowable(e);
      }
    }
  }

  synchronized WebsiteDatabase getDatabase(int type, String language){
    String folerType = "new";
    String folerLang = "unknown";
    
    if(type == Website.UNAVAILABLE)  {
      folerType = "ignore";
    } else if(type == Website.CONFIGURATION)  {
      folerType = "config";
    }
    
    if("vn".equalsIgnoreCase(language)) {
      folerLang = "vn";
    } else if("en".equalsIgnoreCase(language)) {
      folerLang = "en";
    }
    
    return getDatabase("sources/websites/"+ folerType+"/"+folerLang+"/");
  }
  
  synchronized WebsiteDatabase getDatabase(String path){
    if(path == null || path.trim().isEmpty()) return null;
    WebsiteDatabase tracker = holder.get(path);
    if(tracker != null && !tracker.isClose()) return tracker;
    try {
      tracker = new WebsiteDatabase(path);
//    } catch (RecoveryException e) {
//      LogService.getInstance().setThrowable(path, e.getCause());
//      LogService.getInstance().setThrowable(path, e);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(path, e);
    }
    if(tracker != null) holder.put(path, tracker);
    return tracker;
  }

  public void setSleep(long sleep) { this.sleep = sleep; }
  
  public void loadNewWebsite(Websites websites) {
    WebsiteDatabase database = getDatabase(Website.NEW_ADDRESS, null);
    int total = database.getTotalPage(100);
    for(int page = 1;  page <= total; page++) {
      try {
        List<Website>  list = database.loadUrlByPage(page, 100);
        websites.setList(list);
//        LogService.getInstance().setMessage(null, "new websites "+ list.size());
        if(list.size() > 0) break;
      } catch (Throwable e) {
        LogService.getInstance().setThrowable(e);
      }
    }
  }
}
