/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.io;

import java.util.Calendar;

import org.vietspider.bean.IDGenerator;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 3, 2008  
 */
public class TestIDGenerator {
  
  public static void main(String[] args) {
    IDGenerator generator = new IDGenerator();
    String id = generator.generateId(0, Calendar.getInstance());
    System.out.println(id);
    long aa  = 2008120310410111111l;
  }
  
}
