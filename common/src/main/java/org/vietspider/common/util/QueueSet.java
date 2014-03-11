package org.vietspider.common.util;
/**
 * Author : Le Bien Thuy
 *          bienthuylx@yahoo.com
 * Sep 19, 2006  
 */
public class QueueSet<T> extends Queue<T> {
  
  public void push(T v) {
  	if(hasValue(v)) return;
    Node<T> newNode = new Node<T>(v);    
    if(current != null) {
      current.next = newNode;
      current = newNode;
      return;
    }
    current = newNode;
    first = current;
  } 
  
  private boolean hasValue(T v){
  	Node<T> temp = first;
  	while(temp != null){
  		if(temp.value.equals(v)) return true;
  		temp = temp.next;
  	}
		return false;
  }
  
}
