/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db.website;

import java.util.List;

import org.vietspider.bean.website.Website;
import org.vietspider.common.io.LogService;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 5, 2009  
 */
public class DatabaseOptimization extends Thread {
  
  private WebsiteDatabases databases;
  
  public DatabaseOptimization(WebsiteDatabases databases) {
    this.databases = databases;
    this.start();
  }
  
  public void run() {
//    optimize(databases.getDatabase(Website.NEW_ADDRESS, null), false);
//    optimize(databases.getDatabase(Website.NEW_ADDRESS, "vn"), true);
//    optimize(databases.getDatabase(Website.NEW_ADDRESS, "en"), true);
    
    convert(databases.getDatabase(Website.NEW_ADDRESS, "vn"));
    convert(databases.getDatabase(Website.NEW_ADDRESS, "en"));
  }

  private void convert(WebsiteDatabase database) {
    int total  = database.getTotalPage(100);
    for(int page = 1; page <= total; page++) {
      try {
        List<Website> list = database.loadUrlByPage(page, 100);
        for(int k = 0; k < list.size(); k++) {
          Website website = list.get(k);
          if(website.getHtml()  == null 
              || website.getHtml().trim().isEmpty() 
              || "head".equalsIgnoreCase(website.getHtml().trim())) {
            website.setLanguage("*");
            databases.save(website);
          }
        }
      } catch (Throwable e) {
        LogService.getInstance().setThrowable(e);
      }
    }
  }
  
  /*private void optimize(WebsiteDatabase database, boolean redetect) {
    File fileIndex = database.getFileIndex();
    File newFileIndex = new File(fileIndex.getParentFile(), fileIndex.getName()+".opt");
    if(newFileIndex.exists()) newFileIndex.delete();
    if(newFileIndex.exists()) {
      LogService.getInstance().setMessage(null, "Cann't delete file " + newFileIndex.getName());
      return;
    }
    
    try {
      newFileIndex.createNewFile();
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    
    PageIO<Md5HashEntry> io = new PageIO<Md5HashEntry>() {
      public Md5HashEntry createEntry() {
        return new Md5HashEntry();
      }
    };
    io.setData(newFileIndex, MD5Hash.DATA_LENGTH);
    
    SortedMap<byte[], String> map  = database.getMap();
    Set<Entry<byte[], String>> set = map.entrySet();
    Iterator<Entry<byte[], String>> iterator = set.iterator();
    int counter = 0;
    while(iterator.hasNext()) {
      Entry<byte[], String> entry = iterator.next();
      byte[] bytes = entry.getKey();
      MD5Hash md5Hash = new MD5Hash(bytes);
      io.write(new Md5HashEntry(md5Hash));
      
      System.out.println(md5Hash.toString());
      
      counter++;
      if(counter%100 == 0) io.commit();
      
      if(!redetect) continue;
      
      try {
        Website website = XML2Object.getInstance().toObject(Website.class, entry.getValue());
        if(website.getHtml()  == null 
            || website.getHtml().trim().isEmpty() 
            || "head".equalsIgnoreCase(website.getHtml().trim())) {
          website.setLanguage("*");
          databases.save(website);
        }
      } catch (Exception e) {
         LogService.getInstance().setThrowable(e);
      }
    }
    io.commit();
    newFileIndex.renameTo(fileIndex);
  }*/
  
}
