package org.vietspider.common.util;


/**
 * Author : Nhu Dinh Thuan
 *          thuan.nhu@exoplatform.com
 * Sep 19, 2006  
 */
public class TestStack {
  
  public static void main(String [] args){
    Stack<Integer> stack = new Stack<Integer>();
    stack.push(2);
    stack.push(4);
   
    while(stack.hasNext()){
      System.out.println(stack.pop());
    }
  }
  
}
