/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.solr2.search;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 13, 2011  
 */
public class Proximity {
  
  String value;
  int counter;

  public Proximity(String value, int counter) {
    this.value = value;
    this.counter = counter;
  }

  public String getValue() {
    return value;
  }

  public int getCounter() {
    return counter;
  }
  
  
}
