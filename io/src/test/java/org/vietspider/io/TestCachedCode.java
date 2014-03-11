/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.io;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 9, 2009  
 */
public class TestCachedCode {
  
  
  public static void main(String[] args) throws Throwable {
    /*final int MAX_SIZE = 1000000;
    final int MAX_RANDOM = 1000000000;
    final CachedCodes cachedCodes = CachedCodes.getInstance();
    new Thread() {
      public void run() {
        while(true) {
          int max  = (int)(Math.random()*10000);
          for(int i = 0; i < max; i++) {
            int key  = (int)(Math.random()*MAX_RANDOM);
            int value  = (int)(Math.random()*MAX_RANDOM);
            cachedCodes.put(key, value);
            if(cachedCodes.get(key) != value) {
              System.err.println(" chung da bi sai roi ");
              System.exit(0);
            }
          }
//          if(isPrint(cachedCodes.size())) {
            System.out.println(" dat duoc tong so " + cachedCodes.size());
//          }
          int beforeClean  = cachedCodes.size();
          cachedCodes.clean();
          int afterClean   = cachedCodes.size();
          
          if(afterClean - beforeClean > 0) {
            System.out.println(" remove duoc  " + (afterClean - beforeClean) + " :  con lai "+ cachedCodes.size() + " : "+ beforeClean) ;
            System.out.println(" \n");
          }
          
          try {
            Thread.sleep(500);
          } catch (Exception e) {
            // TODO: handle exception
          }
        }
      }
    }.start();*/
  }
  
  private static boolean isPrint(int size) {
    if(size > 10000 && size < 11000) return true;
    if(size > 100000 && size < 111000) return true;
    if(size > 40000 && size < 4111000) return true;
    if(size > 70000 && size < 8111000) return true;
    if(size > 1000000 && size < 2000000) return true;
    return false;
  }
}

