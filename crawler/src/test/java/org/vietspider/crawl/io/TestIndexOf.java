/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.io;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 18, 2007  
 */
public class TestIndexOf {
  public static void main(String[] args) {
    String str1 = "SOURCE:";
    String str2 = "SOURCE:ARTICLE.Công nghệ.TC Kiến thức ngày nay: Cann't extract title with content within A block.";
    System.out.println(str2.indexOf(str1));
  }
}
