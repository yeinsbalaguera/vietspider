/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.io.downloaded;

import java.nio.ByteBuffer;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 14, 2008  
 */
public class TestBuffer {
  
  public static void main(String[] args) {
    ByteBuffer buff = ByteBuffer.allocateDirect(367874);
    System.out.println(buff.capacity());
  }
}
