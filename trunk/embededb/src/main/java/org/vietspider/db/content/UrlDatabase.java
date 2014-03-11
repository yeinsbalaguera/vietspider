/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db.content;

import java.io.File;

import org.vietspider.db.TextDatabase;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 3, 2009  
 */
public class UrlDatabase  extends TextDatabase {
  
  public UrlDatabase(File folder, String name, long cachedSize) throws Exception {
    super(folder, name, cachedSize);
  }

  public void save(String url, String id) throws Throwable {
    if(isClose) return;
//    System.out.println(" conent "+ new String(bytes, "utf-8"));
//    System.out.println("save la "+url + " / "+ id);
    map.put(url.toLowerCase(), id.getBytes());
  }
  
  public String load(String url) throws Throwable {
    if(isClose) return null;
    byte [] bytes = map.get(url.toLowerCase());
    if(bytes == null) return null;
    return new String(bytes);
  }
  
  void sync() throws Throwable {
    db.sync();
  }
}
