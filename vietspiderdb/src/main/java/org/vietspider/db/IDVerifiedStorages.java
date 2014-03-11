/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.vietspider.bean.Article;
import org.vietspider.common.Application;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 24, 2011  
 */
public class IDVerifiedStorages {
  
  private final static IDVerifiedStorages STORAGES = new IDVerifiedStorages();
  public final static IDVerifiedStorages getInstance() { return STORAGES; }
  
  public final static void save(String name, Article article) {
    getInstance().getStorage(name).add(article.getId());
  }
  
  public final static void save(String name, String id) {
    getInstance().getStorage(name).add(id);
  }
  
  private Map<String, IDVerifiedStorage> holder = new ConcurrentHashMap<String, IDVerifiedStorage>();
  
  
  public IDVerifiedStorages() {
    Application.addShutdown(new Application.IShutdown() {

      public String getMessage() { return "Close Article Database";}

      public void execute() {
        Iterator<Map.Entry<String, IDVerifiedStorage>> iterator = holder.entrySet().iterator();
        while(iterator.hasNext()) {
          IDVerifiedStorage tracker = iterator.next().getValue();
          tracker.save();
          iterator.remove();
        }
      }
    });
  }
  
  public IDVerifiedStorage getStorage(String name) {
    Iterator<Map.Entry<String, IDVerifiedStorage>> iterator = holder.entrySet().iterator();
    while(iterator.hasNext()) {
      Map.Entry<String, IDVerifiedStorage> entry = iterator.next();
      if(name.equals(entry.getKey())) return entry.getValue();
    }
    IDVerifiedStorage storage = new IDVerifiedStorage(name);
    holder.put(name, storage);
    return storage;
  }
  
}
