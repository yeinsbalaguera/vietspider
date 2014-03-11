/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.common.util;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 13, 2008  
 */
public class Iterator<T> {
  
  private Node<T> node;
  private Node<T> pre;
  
  public Iterator(Node<T> node) {
    this.node = node;
  }
  
  public boolean hasNext() {
    if(pre == null) return node != null;
    return node.next != null;
  }
  
  public T next() {
    if(pre == null) {
      pre = node;  
      return node.value;
    }
    pre = node;
    node = node.next;
    return node.value;
  }
  
  public void remove() {
    pre.next = node.next;
    node = pre;
  }
  
}
