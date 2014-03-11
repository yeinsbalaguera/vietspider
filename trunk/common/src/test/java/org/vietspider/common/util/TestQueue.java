package org.vietspider.common.util;


/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 19, 2006  
 */
public class TestQueue {
  public static void main(String [] args){
    Queue<Integer> stack = new Queue<Integer>();
    stack.push(2);
    stack.push(5);
    stack.push(4);
    
    
    Queue<Integer> stack2 = new Queue<Integer>();
    stack2.push(-6);
    stack2.push(13);
    stack2.push(0);
    stack2.push(90);
    
//    stack.appendFirst(stack2);
    
    while(stack.hasNext()) {
      System.out.println(stack.pop());
    }
  }
}
