/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.io;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 24, 2007  
 */
public class TestStack {
  
  public static void main(String[] args) {
    MyStack stack = new MyStack(3);
    stack.push(34);
    stack.push(9);
    stack.push(67);
    
    try {//thu dat them phan tu nua
      stack.push(45);
    }catch (Exception e) { 
      System.err.println(e.toString());
    }
    
    while(stack.hasNext()) {
      System.out.println(" element is "+ stack.pop());
    }
    
    stack.moveLast();
    
    int tong  = 0;
    
    while(stack.hasNext()) {
      tong += stack.pop();
    }
    
    System.out.println(" tong la "+tong);
  }
  
}
