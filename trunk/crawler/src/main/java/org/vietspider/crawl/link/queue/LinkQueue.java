/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.link.queue;

import java.util.Iterator;
import java.util.concurrent.LinkedBlockingDeque;

import org.vietspider.crawl.link.Link;
import org.vietspider.model.Source;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jun 25, 2009  
 */
public class LinkQueue {
  
  private LinkedBlockingDeque<Link> queue;
  private int codeName = -1;
  
  public LinkQueue(Source source) {
    codeName = source.getCodeName();
    queue = new LinkedBlockingDeque<Link>();
  }
  
  public LinkQueue(int code) {
    this.codeName = code;
    queue = new LinkedBlockingDeque<Link>();
  }
  
  public int size() { return queue.size(); }
  
  public void push(Link v) {
//    if(v.getSource().getCodeName() != codeName) return;
    queue.addLast(v);    
  }
  
  public void clear(){
    queue.clear();
  }
  
  public boolean hasNext() { return !queue.isEmpty(); }
  
  public synchronized Link pop() { return queue.pollFirst();  }

  public void appendFirst(LinkQueue queue2) {
    Iterator<Link> iterator = queue2.queue.iterator();
    while(iterator.hasNext()) {
      queue.addFirst(iterator.next());
    }
  }
  
  public LinkQueue clone() {
    LinkQueue cqueue = new LinkQueue(codeName);
    Iterator<Link> iterator = queue.iterator();
    while(iterator.hasNext()) {
      cqueue.push(iterator.next());
    }
    return cqueue;
  }

  public int getCodeName() { return codeName; }
}
