/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.io;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 5, 2009  
 */
public class CyclicBarrierExample {
  
  public static int k;
  public static int arr[] = new int[10];
  private CyclicBarrier barrier;

  void startThread(){   
    barrier = new CyclicBarrier(5, new Runnable() {
      public void run() {
        out();
      }
    });
    
    
    for (int j=0; j<5 ;j++ ){
      new Thread(new Counter()).start();
    }    
  }

  public void out(){ 
    System.out.println("Ket thuc thread k="+k);
    for (int j=0; j<5 ;j++ ) {
      System.out.println("out arr["+j+"]="+arr[j]);
    }
  }

  synchronized static void count() {
    k++;
    arr[k]=k;
    System.out.println("thread "+k);
    System.out.println("arr["+k+"]="+arr[k]);    
  }

  class Counter implements Runnable {    
    public void run() {  
      count();        
      try {
        barrier.await();
      } catch (InterruptedException ex) {
        return;
      } catch (BrokenBarrierException ex) {
        return;
      }
    }
  }

  public static void main(String[] args) {
    CyclicBarrierExample a=new CyclicBarrierExample();
    a.startThread();
  }
}
