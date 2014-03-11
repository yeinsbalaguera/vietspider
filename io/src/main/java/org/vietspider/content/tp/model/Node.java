/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.tp.model;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 30, 2009  
 */
public class Node {
  
  private int total1;
  private int total2;

  private int time1;
  private int time2;
  
  public Node(int total, int time1, int time2) {
    this.total1 = total;
    this.total2 = total;
    this.time1 = time1;
    this.time2 = time2;
  }
  
  public Node(int total1, int total2, int time1, int time2) {
    this.total1 = total1;
    this.total2 = total2;
    this.time1 = time1;
    this.time2 = time2;
  }
  
  public int getTotal2() { return total2; }
  
  public int getTotal1() { return total1; }

  public int getTime1() { return time1; }

  public int getTime2() { return time2; }

}
