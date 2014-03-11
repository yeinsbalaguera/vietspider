/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.tp.vn.comparator;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 30, 2009  
 */
public class Node {
  
  private int code;

  private int time1;
  private int time2;
  
  public Node(int code, int time1, int time2) {
    this.code = code;
    this.time1 = time1;
    this.time2 = time2;
  }
  
  
  public int getCode() { return code; }
  
  public int getTime1() { return time1; }

  public int getTime2() { return time2; }

}
