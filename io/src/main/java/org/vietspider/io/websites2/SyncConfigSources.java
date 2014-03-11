/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.io.websites2;

import java.io.File;

import org.vietspider.bean.website.Website;
import org.vietspider.bean.website.Websites;
import org.vietspider.common.io.UtilFile;
import org.vietspider.model.Source;
import org.vietspider.model.SourceIO;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 1, 2009  
 */
class SyncConfigSources extends Thread {
  
  private RWebsiteDatabases rdb;
  
  SyncConfigSources(RWebsiteDatabases rdb) {
    this.rdb = rdb;
    this.start();
  }
  
  public void run() {
    sync(UtilFile.getFolder("sources/sources/")); 
  }
  
  private void sync(File file) {
    if(file.isDirectory()) {
      File [] files = file.listFiles();
      for(int i = 0; i < files.length; i++) {
        sync(files[i]);
      }
      return;
    }
    if(file.getName().indexOf(".v.") > -1) return;
    Source source = SourceIO.getInstance().load(file);
    if(source == null || source.getHome() == null) return;
    
    String address = Websites.toAddressWebsite(source.getHome()[0]);
    String host = Website.toHost(address);
    Website website = rdb.search(host);
//    System.out.println(" thay gia tri "+ host + " : "+ website);
//    if(website != null) System.out.println(" co homepage "+ website.getAddress());
    
    if(website != null 
        && website.getStatus() == Website.CONFIGURATION) return;
    
    if(website == null) website = new Website();
    website.setAddress(address); 
    website.setLanguage("vn");
    website.setStatus(Website.CONFIGURATION);
    website.setSource(source.getFullName());
    
    WebsiteStorage.getInstance().save(website);
  }

}
