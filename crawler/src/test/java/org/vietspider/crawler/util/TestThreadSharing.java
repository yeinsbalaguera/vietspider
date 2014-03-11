/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawler.util;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 26, 2008  
 */
public class TestThreadSharing {

  public static class person implements Runnable  {
    
    boolean bankrupt;
    boolean overdrawn_flag;
    int counter = 0;
    
    public synchronized void run() {
      while(true) {
        boolean temp = bankrupt; // no signal yet, bankrupt should be false
        if(counter == 10) {
          overdrawn_flag = true; // signal overdrawn
          break;
        }
        notifyAll(); // wake up waiters
        System.out.println(counter);
        counter++;
        try {
          Thread.sleep(100);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }

  }

  public static void main(String[] args) {
    person p = new person();
    Thread thread = new Thread(p);
    thread.start();
    synchronized (p) {
      while (p.overdrawn_flag == false)  { // wait for overdrawn
        try {
          System.out.println(" thuan waiting");
          p.wait(); 
        } catch (InterruptedException e) {
        }
      }
      System.out.println(" da ra roi ");
      p.bankrupt = true; // overdrawn, set bankrupt to true
    }
  }
}
