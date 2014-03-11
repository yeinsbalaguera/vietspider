/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.io;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 24, 2007  
 */
public class MyStack {

  private int cursor;
  private int last;
  private int stack[];

  public MyStack(int size) {// truyen vao kick thuoc voi constructor
    cursor= -1;
    last = -1;
    this.stack=new int[size];
  }

  public int getSize() {// tra ve kich thuc stack
    return stack.length;
  }

  public int pop() {
    if(cursor > 0) {
      cursor--;
      return stack[cursor];
    }
    throw new RuntimeException("Ngan xep rong");
  }

  // kiem tra xem ngan xep da rong hay chua 
  public boolean hasNext() { return cursor > 0; } 

  public void moveLast() {
    cursor = last;
  }
  

  public void push(int value) {
    if(cursor < stack.length - 1) {
      cursor++;
      last = cursor;
      stack[cursor] = value;
      return;
    }
    throw new RuntimeException("Ngan xep da day");
  }    
}
