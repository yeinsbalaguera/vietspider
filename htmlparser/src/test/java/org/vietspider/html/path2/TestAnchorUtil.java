/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.path2;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 17, 2008  
 */
public class TestAnchorUtil {
  public static void main(String[] args) {
    String path = ".0.2.2.2.1.17.0.2";
    int idx = path.indexOf('.', 1);
    System.out.println("idx " + idx);
    path = path.substring(0, idx);
    if(idx > 0) path = path.substring(1, idx);
    System.out.println("path " + path);
    idx = Integer.parseInt(path);
  }
}
