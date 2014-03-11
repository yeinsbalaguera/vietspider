/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.pool;

import java.util.Iterator;
import java.util.concurrent.LinkedBlockingDeque;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 8, 2009  
 */
public abstract class QueueEntry<V> {
  
  protected volatile LinkedBlockingDeque<SessionEntry<V>> queue;
  protected volatile int capacity;
  
  public QueueEntry() {
    this.capacity = 10;
    queue = new LinkedBlockingDeque<SessionEntry<V>>();
  }
  
  public void appendFirst(SessionEntry<V> [] entries) {
    for(int i = 0; i < entries.length; i++) {
//      System.out.println(" tai day co === > "+ entries[i].hashCode());
      queue.addFirst(entries[i]);
    }
  }
  
  public int size() { return queue.size(); }
  
  public void clear() { queue.clear(); }
  
  public boolean isFull() {return queue.size() >= capacity;}
  
  public void removeElement(String key) {
    Iterator<SessionEntry<V>> iterator = queue.iterator();
    while(iterator.hasNext()) {
      SessionEntry<?> entry = iterator.next();
      if(entry.equalsKey(key)) queue.remove(entry);
    }
  }
  
  public void add(SessionEntry<V> entry) { queue.add(entry); }
  
  public boolean isEmpty() { return queue.isEmpty(); }

  public void setCapacity(int capacity) { this.capacity = capacity; }
  
  abstract public <S extends SessionEntry<V>> S nextEntry(int id);
  
  public abstract <S extends SessionEntry<V>> S[] toArray();
  
  public abstract void buildList(StringBuilder builder) ;
}
