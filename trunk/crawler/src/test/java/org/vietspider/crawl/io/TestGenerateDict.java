/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.io;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 8, 2010  
 */
public class TestGenerateDict {
  public static void main(String[] args) {
    StringBuilder builder = new StringBuilder();
    for(int i = 13; i <= 69; i++) {
      for(int j = 35232; j <= 36105; j++) {
        if(builder.length() > 0) builder.append(',');
        builder.append("i="+i+"&ii="+j);
      }
    }
    System.out.println(builder);
  }
}
