/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.dict.source.monitor2;

import java.util.ArrayList;
import java.util.List;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 14, 2009  
 */
public class TestHashCode {
  public static void main(String[] args) {
    String value = "aaa";
    System.out.println(value.hashCode());
    
    List list  = new ArrayList();
    System.out.println(list.hashCode());
    
    list  = new ArrayList();
    System.out.println(list.hashCode());
  }
}
