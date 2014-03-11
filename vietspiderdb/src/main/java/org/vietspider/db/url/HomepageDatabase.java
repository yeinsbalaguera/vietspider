/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db.url;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.vietspider.common.io.LogService;
import org.vietspider.common.io.UtilFile;
import org.vietspider.common.text.NameConverter;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 27, 2009  
 */
public class HomepageDatabase {
  
  protected String name;
  protected String folder;
  protected boolean exists = false;

  public HomepageDatabase(String _name, boolean create) {
    NameConverter converter = new NameConverter();
    name  = NameConverter.encode(_name);
    if(create) {
      File file = UtilFile.getFolder("sources/homepages/"+name+"/");
      folder = file.getAbsolutePath();
      exists = true;
    } else {
      File file = new File(UtilFile.getFolder("sources/homepages/"), name+"/");
      folder = file.getAbsolutePath();
      exists = file.exists();
    }
  }
  
  public boolean exists() { return exists; }
  
  public void saveUrl(String url) {
    UrlData data = new UrlData(url, folder);
    UrlDatabases.getInstance().save(data);
  }
  
  public void removeUrl(String url) {
    UrlData data = new UrlData(url, folder, UrlData.REMOVE);
    UrlDatabases.getInstance().save(data);
  }
  
  public int getTotalPage() {
    if(!exists) return 0;
    UrlDatabase database = UrlDatabases.getInstance().getDatabase(folder);
    return database.getTotalPage(100);
  }
  
  public List<String> loadPage(int page) {
    return loadPage(page, 100);
  }
  
  public  List<String> loadPage(int page, int pageSize) {
    if(page < 1) return new ArrayList<String>();
    UrlDatabase database = UrlDatabases.getInstance().getDatabase(folder);
    int total = database.getTotalPage(pageSize);
    if(page > total) return new ArrayList<String>();
    try {
      return database.loadUrlByPage(page, pageSize);
    } catch (Throwable e) {
      LogService.getInstance().setThrowable("URLDATABASE", e);
    }
    return new ArrayList<String>();
  }
  

}
