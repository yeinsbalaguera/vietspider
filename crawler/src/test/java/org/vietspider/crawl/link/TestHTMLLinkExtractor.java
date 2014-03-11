/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.link;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 27, 2009  
 */
public class TestHTMLLinkExtractor {
  public static void main(String[] args) {
    String address = "thay co http://vn.rd.yahoo.com/blog/mod/comment/*http:/vn.myblog.yahoo.com/alittlesaigoninmylife/article?mid=95";
    int idx = address.toLowerCase().indexOf("http:", 10);
    if(idx > -1) {
      idx += 5;
      while(true){        
        char c = address.charAt(idx);
        if(c != '/' && c != ':') break; 
        idx++;
      }

      String subaddress = "http://" + address.substring(idx);
      System.err.println(" thay co "+ address);
      System.err.println(" thay co "+ subaddress);
    }
  }
}
