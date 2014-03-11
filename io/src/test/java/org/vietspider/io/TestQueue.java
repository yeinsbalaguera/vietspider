/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.io;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 24, 2007  
 */
public class TestQueue {
  
  public static void main(String[] args) {
    MyQueue queue = new MyQueue(4);
    queue.push(34);
    queue.push(9);
    queue.push(12);
    queue.push(67);
    
    try {//thu dat them phan tu nua
      queue.push(45);
    }catch (Exception e) { 
      System.err.println(e.toString());
    }
    
    while(queue.hasNext()) {
      System.out.println(" element is "+ queue.pop());
    }
    
    queue.moveFirst();
    
    int tong  = 0;
    
    while(queue.hasNext()) {
      tong += queue.pop();
    }
    
    System.out.println(" tong la "+tong);
  }
  
}
