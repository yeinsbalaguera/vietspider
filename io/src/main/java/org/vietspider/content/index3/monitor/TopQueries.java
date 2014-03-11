/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.index3.monitor;

import java.io.File;
import java.util.Comparator;
import java.util.Iterator;
import java.util.concurrent.ConcurrentSkipListSet;

import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 9, 2010  
 */
public class TopQueries {
  
  public static int MAX = 1000;

  private ConcurrentSkipListSet<Query> collection;
  
  public TopQueries() {
    collection = new ConcurrentSkipListSet<Query>(new Comparator<Query>() {
      public int compare(Query o1, Query o2) {
        long t = o2.getTotal() - o1.getTotal();
        if(t > 0) return 1;
        if(t < 0) return -1;
        return 0;
      }
    });
    loadInit();
  }
  
  public ConcurrentSkipListSet<Query> getCollection() {
    return collection;
  }
  
  public void add(Query newQuery) {
    if(newQuery.getTotal() < 11) return;
    if(collection.size() >= MAX) {
      Query query = collection.last();
      if(newQuery.getTotal() < query.getTotal()) return;
    }
    
    addToCollection(newQuery);
    while(collection.size() > MAX) {
      Query query = collection.last();
      collection.remove(query);
    }
  }
  
  private void addToCollection(Query newQuery) {
    Iterator<Query> iterator = collection.iterator();
    while(iterator.hasNext()) {
      Query  query = iterator.next();
      if(newQuery.getPattern().equals(query.getPattern())) {
        iterator.remove();
        break;
      }
    }
    collection.add(newQuery);
  }

  private void loadInit() {
    File folder = UtilFile.getFolder("/track/search/queries/");
    File file = new File(folder, "top.queries.txt");
    if(!file.exists() || file.length() < 0) return;
    try {
      byte [] bytes = RWData.getInstance().load(file);
      String text = new String(bytes, Application.CHARSET);
      String [] elements = text.split("\n");
      for(int i = 0; i < elements.length; i++) {
        try {
          Query query = new Query();
          query.fromString(elements[i]);
          addToCollection(query);
        } catch (Exception e) {
          LogService.getInstance().setThrowable(e);
        }
      }
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
      file.delete();
    }
  }

  public void saveFile() {
    File folder = UtilFile.getFolder("/track/search/queries/");
    File file = new File(folder, "top.queries.txt");
    StringBuilder builder = new StringBuilder();
    Iterator<Query> iterator = collection.iterator();
    while(iterator.hasNext()) {
      Query  query = iterator.next();
      if(builder.length() > 0) builder.append('\n');
      builder.append(query.toString());
    }
    
    try {
      RWData.getInstance().save(file, builder.toString().getBytes(Application.CHARSET));
    }catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }

}
