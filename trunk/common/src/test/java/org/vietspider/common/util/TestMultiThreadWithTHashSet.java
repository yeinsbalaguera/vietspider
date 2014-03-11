/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.common.util;

import gnu.trove.TIntHashSet;
import gnu.trove.TIntIterator;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 8, 2009  
 */
public class TestMultiThreadWithTHashSet {

  static TIntHashSet tintSet = new TIntHashSet();

  static  int size  = 1000;
  static int [] values = new int [size];

  public static void main(String[] args) {
    for(int i = 0; i < size; i++) {
      values[i] = i;//(int)(Math.random()*size);
    }

    testTIntset();

    AddThread [] addThreads = new AddThread[50];
    for(int i = 0; i < addThreads.length; i++) {
      addThreads[i] = new AddThread(500);
      addThreads[i].start();
    }

    IteratorThread [] iteratorThreads = new IteratorThread[50];
    for(int i = 0; i < iteratorThreads.length; i++) {
      iteratorThreads[i] = new IteratorThread(300);
      iteratorThreads[i].start();
    }

    RemoveThread [] removeThreads = new RemoveThread[50];
    for(int i = 0; i < removeThreads.length; i++) {
      removeThreads[i] = new RemoveThread(400);
      removeThreads[i].start();
    }
  }

  private static void testTIntset() {
    long start2  = System.currentTimeMillis();
    for(int i = 0; i < size; i++) {
      tintSet.add(values[i]);
    }
    long end2= System.currentTimeMillis();
    System.out.println(tintSet.getClass().getName() + " add 3 "+ (end2 - start2));

    start2  = System.currentTimeMillis();
    for(int i = 0; i < size; i++) {
      tintSet.contains(values[i]);
    }
    end2= System.currentTimeMillis();
    System.out.println(tintSet.getClass().getName() + " contains "+ (end2 - start2));

    System.out.println(tintSet.getClass().getName() + " " + tintSet.size());
  }

  private   static class IteratorThread extends Thread {

    private long sleep = 300;

    IteratorThread(long sl) {
      this.sleep = sl;
    }

    public void run() {
      TIntIterator iterator = tintSet.iterator();
      try {
        int counter = 0;
        int [] set = tintSet.toArray();
        System.out.println("kich thuoc " + set.length + " trong khi "+ tintSet.size());
//        while(iterator.hasNext()) {
//        for(int value : set) {
//          if(value == 0) continue;
//          System.out.print(value+", ");
          try {
            Thread.sleep(sleep);
          } catch (Exception e) {
          }
//          counter++;
//          if(counter%10 == 0) System.out.println();
//        }
      } catch (Exception e) {
        e.printStackTrace();
        System.exit(0);
      }
    }
  }

  private  static class RemoveThread extends Thread {

    private long sleep = 300;

    RemoveThread(long sl) {
      this.sleep = sl;
    }

    public void run() {
      for(int i = 0; i < 100; i++) {
        int index = (int)Math.random()*tintSet.size();
        tintSet.remove(index);
//        System.out.println(" remove data from index "+ index);
        try {
          Thread.sleep(sleep);
        } catch (Exception e) {
        }
      }
    }
  }

  private static class AddThread extends Thread {

    private long sleep = 300;

    AddThread(long sl) {
      this.sleep = sl;
    }

    public void run() {
      while(true) {
        int index = (int)Math.random()*tintSet.size();
        tintSet.add(index);
//        System.out.println(" add data from index "+ index);
        try {
          Thread.sleep(sleep);
        } catch (Exception e) {
        }
      }
    }
  }

}
