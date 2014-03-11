/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawler.util;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 22, 2008  
 */
public class TestThread {
  
  private static class Chay implements Runnable { 
    
    public void run () {
      int counter = 0;
      while(true) {
        System.out.println(" chay roi ");
        counter++;
        if(counter == 10) return;
        try {
          Thread.sleep(1000);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
    
    public static void main(String[] args) {
      Thread thread = new Thread(new Chay());
      System.out.println("buoc 1 " + thread.isAlive());
      thread.start();
      System.out.println("buoc 2 " + thread.isAlive());
      
      while(thread.isAlive()) {
        try {
          Thread.currentThread().sleep(2000);
        } catch (Exception e) {
          e.printStackTrace();
        }
        System.out.println("buoc 3 " + thread.isAlive());
      }
    }
  }
}
