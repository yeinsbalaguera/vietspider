/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.common.util;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Sep 19, 2006
 */
public class Node<T> {

  T value;
  
  Node<T> next;

  Node(T v) {
    value = v; 
  }
  
  Node(T v, Node<T> n) {
    value = v; 
    next = n;
  }
  
  public Node<T> getNext() { return next; }

  public void setNext(Node<T> next) { this.next = next; }
}
