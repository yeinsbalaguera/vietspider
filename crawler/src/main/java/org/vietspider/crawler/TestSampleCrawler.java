/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 7, 2009  
 */
public class TestSampleCrawler {
  public static void main(String[] args)   
  
  throws InterruptedException, ExecutionException {  
    @SuppressWarnings("serial")  
    List<String> urls = new ArrayList<String>() {{  
      add("http://www.java2jee.blogspot.com");  
      add("http://www.yahoo.com");  
      add("http://www.msdn.com");  
      add("http://c2.com/xp/ExtremeProgrammingRoadmap.html");  
      add("http://apache.org/");  
      add("http://sourceforge.net/");  
    }};  
    List<Future<Integer>> futures = new ArrayList<Future<Integer>>(urls.size());  

    final ExecutorService service = Executors.newFixedThreadPool(2);  

    try {  
      long start = System.nanoTime();  
      for (String url : urls) {  
        futures.add(service.submit(new CallableExample(url)));  
      }  

      long result = 0;  
      for (Future<Integer> future : futures) {  
        result += future.get();  
      }  
      System.out.println("\nTotal bytes: " + result);  
      System.out.println("Total time: "+ (System.nanoTime() - start) / 1000000   + "ms");  
    } finally {  
      service.shutdown();  
    }  
  }  
}
