/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.common.util;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import org.vietspider.common.io.ConcurrentSetInt;
import org.vietspider.common.io.ConcurrentSetIntCacher;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 8, 2009  
 */
public class TestMultiThreadWithConcurrentInt {

  static ConcurrentSetInt tintSet = new ConcurrentSetIntCacher();

  public static void main(String[] args) {
    AddThread [] addThreads = new AddThread[100];
    for(int i = 0; i < addThreads.length; i++) {
      addThreads[i] = new AddThread(10);
      addThreads[i].start();
    }

    SaveThread [] iteratorThreads = new SaveThread[50];
    for(int i = 0; i < iteratorThreads.length; i++) {
      iteratorThreads[i] = new SaveThread(300, i);
      iteratorThreads[i].start();
    }

    ToListThread[] toListThreads = new ToListThread[10];
    for(int i = 0; i < toListThreads.length; i++) {
      toListThreads[i] = new ToListThread(700);
      toListThreads[i].start();
    }

  }

  private  static class SaveThread extends Thread {

    private long sleep = 300;
    private int index;

    SaveThread(long sl, int i) {
      this.sleep = sl;
      this.index = i;
    }

    public void run() {
      while(true) {
        try {
          File file  = new File("D:\\Temp\\codes\\", String.valueOf(index));
          RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
          tintSet.write(randomAccessFile.getChannel());
          randomAccessFile.close();
          try {
            Thread.sleep(sleep);
          } catch (Exception e) {
          }
        } catch (Exception e) {
          e.printStackTrace();
          System.exit(0);
        }
      }
    }
  }

  private  static class ToListThread extends Thread {

    private long sleep = 300;

    ToListThread(long sl) {
      this.sleep = sl;
    }

    public void run() {
      while(true) {
        try {
          List<Integer> list = new ArrayList<Integer>();
          tintSet.addToList(list);
          try {
            Thread.sleep(sleep);
          } catch (Exception e) {
          }
        } catch (Exception e) {
          e.printStackTrace();
          System.exit(0);
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
        if(tintSet.size() > 1000000) {
          System.exit(0);
        }
        
        int index = (int)(Math.random()*1000000000);
        int n = (int)(Math.random()*100);
        if(n < 50) index = -index;
        tintSet.add(index);
        try {
          Thread.sleep(sleep);
        } catch (Exception e) {
        }
      }
    }
  }

}
