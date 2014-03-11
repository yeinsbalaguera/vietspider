/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawler;

import java.util.HashMap;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 29, 2007  
 */
public class TestHashMap {
  
  
  public static void main(String[] args) {
    HashMap<Object, Integer> map = new HashMap<Object, Integer>();
    map.put("thuan", 12312);
    Student student = new Student("thuan");
    map.put(student, 3243243);
    
    System.out.println(map.get("thuan"));
    System.out.println(map.get(student));
    
  }
  
  
  private static class Student {
    
    private String name;
    
    Student(String name) {
      this.name = name;
    }

    public String getName() { return name; }
  }
}