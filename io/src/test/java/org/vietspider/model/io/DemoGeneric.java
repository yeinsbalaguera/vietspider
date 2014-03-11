/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.model.io;

import java.lang.reflect.Method;
import java.util.ArrayList;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 23, 2009  
 */
public class DemoGeneric {
  public static void main(String[] args) throws Exception {
    ArrayList<String> sources = new ArrayList<String>();
    sources.add("vi vo");
    sources.add("vo van");
    
    Method method = ArrayList.class.getMethod("add", Object.class);
    System.out.println(method);
    method.invoke(sources, new Integer(3));
    
    System.out.println((Object)sources.get(2));
  }
}
