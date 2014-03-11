/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.link;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 14, 2009  
 */
public class TestLink {
  
  public static void main(String[] args) {
    StringBuilder builder = new StringBuilder("Trên mạng chả ai thèm quan tâm gà con là cái thằng nào ");
    System.out.println(builder);
    if(builder.length() >= 10) {
      builder.delete(10, builder.length());
    }
    System.out.println(builder);
  }
}
