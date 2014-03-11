/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawler.util;

import org.vietspider.link.pattern.LinkPatternFactory;
import org.vietspider.link.pattern.LinkPatterns;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 25, 2008  
 */
public class TestLinkPatterns {
  
  public static void main(String[] args) throws Exception {
    String [] elements = {
        "http://blog.360.yahoo.com/blog-*?cq=*&p=*"
//        "http://*.vnweblogs.com/post/*/*/",
    };
    LinkPatterns linkPatterns = LinkPatternFactory.createPatterns(LinkPatterns.class, elements);
//    String url = "http://phamtran.vnweblogs.com/post/5666/56788#comments";
    String url = "http://blog.360.yahoo.com/blog-9M6DQM4lc7OholTvtGKTd_lXuAo--?cq=1&p=3329";
    System.out.println(" bi length " + url.length());
    System.out.println(linkPatterns.match(url));
  }
  
}
