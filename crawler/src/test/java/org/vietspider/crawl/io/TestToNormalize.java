/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.io;

import java.net.URI;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jun 9, 2008  
 */
public class TestToNormalize {
  
  public static void main(String[] args) throws Exception {
    String address = null;
    address = "http://aaa.vn/ccc/../bbb/../../z.html";
    print(address);
    
    address = "http://aaa.vn/ccc/..";
    print(address);
    
    address = "http://aaa.vn/..";
    print(address);
    
    address = "http://aaa.vn/../nn";
    print(address);
    
    address = "http://aaa.vn/../nn/../";
    print(address);
    
    address = "http://aaa.vn/ccc/../../bb/..";
    print(address);
    
    address = "http://aaa.vn/ccc/../../bb/..ahh";
    print(address);
    
    address = "http://aaa.vn/ccc/../bbb/../z.html";
    print(address);
    address = "http://aaa.vn/ccc/../";
    print(address);
    
    address = "http://aaa.vn/ccc/bbb/../../z.html";
    print(address);
    
    address = "http://aaa.vn/ccc/bbb/aaa/../../z.html";
    print(address);
    
    address = "http://aaa.vn/ccc/bbb/aaa/z.html";
    print(address);
  }
  
  private static void print(String address) {
    System.out.println(address);
    try {
      URI uri = new URI(address);
      System.out.println("uri: "+uri.normalize().toASCIIString());
    } catch (Exception e) {
    }
    System.out.println("me : "+normalize(address));
    System.out.println("================================================");
  }
  
  private static String normalize(String address) {
    StringBuilder builder = new StringBuilder(address);
    int start = address.indexOf('/', 8);
    if(start < 0) return address;
    
    int idx = builder.indexOf("/..", start);
    if(idx < 0) return address;
    
    while(idx > -1) {
      
      while(idx <= start) {
        idx = builder.indexOf("/..", idx+3);
        if(idx < 0) break;
      }
      
      if(idx < 0) break;
      
      int i = idx-1;
      while(i >= start) {
        if(builder.charAt(i) == '/') {
          if(idx + 3 < builder.length() 
              && builder.charAt(idx+3) == '/') {
            builder.delete(i, idx+3);
          } else {
            if(idx + 3 >= builder.length()) {
              builder.delete(i+1, idx+3);
            } else {
              start = idx+4;
            }
          } 
          break;
        }
        i--;
      }
      idx = builder.indexOf("/..", start);
    }
    
    return builder.toString();
  }
  
}
