/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.link;

import java.net.URL;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 3, 2009  
 */
class URLValidator {
  
  private int max = 5;
  
  URLValidator(int max) {
    this.max = max;
  }
  
  boolean validate(URL url) {
    if(!validate(url.getPath(), '/')) return false;
    if(!validate(url.getQuery(), '&')) return false;
    return true;
  }
  
  private boolean validate(String value, char separator) {
//    System.out.println("====  >"+value);
    if(value == null) return true;
    int index = 1; 
    
    while(index < value.length()) {
      int end  = value.indexOf(separator, index);
      if(end < 0) return true;
      
      if(end == index) {
        index = end+1;
        continue;
      }
      
      String pattern = value.substring(index, end);
      int newEnd = value.indexOf(pattern, end);
      if(newEnd < 0) {
        index = end+1;
        continue;
      }
      
      pattern = value.substring(index, newEnd);
      if(count(value, pattern) >= max) return false;
      index = end+1;
    }
    
    return true;
  }
  
  private int count(String path, String pattern) {
    int count = 0;
    int start = 0;
    
    while(start < path.length()) {
     int index = path.indexOf(pattern, start);
     if(index < 0) {
//       System.out.println(" thay co counter la "+ pattern+ " : "+ count);
       return count; 
     }
     start = index + pattern.length();
     count++;
    }
//    System.out.println(" thay co counter la "+ pattern+ " : "+ count);
    return count;
  }
  
  public static void main(String[] args) throws Exception {
    String address = "http://1088.net.vn/index.php/index.php//aaa/component/user/component/user/component/user/#ja-mainnav" ;
    URL url = new URL(address);
    URLValidator validator = new URLValidator(3);
    System.out.println(validator.validate(url));
    
    address = "http://1088.net.vn/index.php/index.php/aaa/component/user/?param=b&&&vanhoa=a&vanhoa=a&vanhoa=a&vanhoa=a&vanhoa=a&" ;
    url = new URL(address);
    System.out.println(url.getQuery());
    System.out.println(validator.validate(url));
  }
  
}
