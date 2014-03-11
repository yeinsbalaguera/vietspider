/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.pool2;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 22, 2009  
 */
public class PrintTask implements Callable<Object> {
  
  public Object call() {
    long sleep = (long)(10000*Math.random());
    System.out.println(hashCode() + " : " + sleep + " is new  session " );
    long start = System.currentTimeMillis();
    try {
      URL url = new URL("http://news.cnet.com/");
      url.openStream();
    } catch (Exception e) {
      e.printStackTrace();
    }
    long end = System.currentTimeMillis();
    System.out.println("==== >"+ (end - start) +" : "+ hashCode() + " is end session " );
    return null;
  }

  
  public static void main(String[] args) {
    ExecutorService threadExecutor = Executors.newFixedThreadPool( 3 );
    List<PrintTask> list = new ArrayList<PrintTask>(); 
    for(int i = 0; i < 10; i++) {
      list.add(new PrintTask());
    }
    try {
      threadExecutor.invokeAny(list, 500l, TimeUnit.MILLISECONDS);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
