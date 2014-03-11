package org.vietspider.common.util;
/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 19, 2006  
 */
public class Queue<T> {
  
  protected Node<T> current = null;
  
  protected Node<T> first = null;

  public T pop() {   
    if(first == null) return null;
    T result = first.value;
    first = first.next;
    if(first == null) current = null;
    return result;
  }
  
  public T last() {
    if(current == null) return null;
    return current.value; 
  }
  
  public T get() {
    if(first == null) return null;
    return first.value; 
  }
  
  public void remove() {
    first = first.next;
    if(first == null) current = null;
  }
  
  public boolean hasNext() { 
    return first != null; 
  }
  
  public void clear(){
    first = null;
    current = null;
  }

  public void push(T v) {
    Node<T> newNode = new Node<T>(v);    
    if(current != null){
      current.next = newNode;
      current = newNode;
      return;
    }
    current = newNode;
    first = current;
  }
  
}
