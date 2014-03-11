/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.pool;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.crawl.CrawlSessionEntry;
import org.vietspider.model.Source;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 2, 2011  
 */
public abstract class CrawlQueue<V> {
  
  private static CrawlQueue<?> instance;
  
  @SuppressWarnings("all")
  public final synchronized static <T> CrawlQueue<T> getInstance(Class<? extends CrawlQueue<T>> clazz) {
    if(instance != null && clazz.isInstance(instance)) {
      return (CrawlQueue<T>)instance;
    }
    try {
    instance = clazz.newInstance();
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
      Application.addError(clazz);
    }
    return (CrawlQueue<T>)instance;
  }
  
  public abstract boolean validate(Source source);
  
  public abstract void removeElement(String key);
  
  public abstract void setCapacity(int capacity) ;
  
  public abstract QueueEntry<V> getQueueEntry();
  
  public  abstract CrawlSessionEntry nextEntry(int id);
  
  public abstract boolean isEmpty();
  
  public abstract void appendFirst(CrawlSessionEntry [] entries);
  
  public abstract CrawlSessionEntry [] next(String key);
  
  public abstract boolean isSleep();
  
  public abstract void wake();
  
  public abstract boolean isInterrupted();
  
  public abstract boolean isAlive();
  
  public abstract SourceQueue getNormalQueue();
  
  public static String[] split(String text) {
    List<String> list = new ArrayList<String>();
    int i = 0;
    int start = 0;
    int length = text.length();
    while(i < length) {
      if(text.charAt(i) == '\n') {
        String line  = text.substring(start, i).trim();
        start = i+1;
        i++;
        if(line.length() < 1) continue;
        String [] elements = line.split("\\.");
        if(elements.length < 3) continue;
        StringBuilder builder = new StringBuilder(elements[1]);
        builder.append('.').append(elements[2]).append('.').append(elements[0]);
        list.add(builder.toString());
        continue;
      }
      i++;
    }
    if(start < length) {
      String line  = text.substring(start, length);
      String [] elements = line.split("\\.");
      if(elements.length > 2) {
        StringBuilder builder = new StringBuilder(elements[1]);
        builder.append('.').append(elements[2]).append('.').append(elements[0]);
        list.add(builder.toString());
      }
    }
    return list.toArray(new String[list.size()]);
  }
  
}
