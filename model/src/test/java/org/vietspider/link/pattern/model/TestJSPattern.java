/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.link.pattern.model;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 22, 2008  
 */
public class TestJSPattern {
  public static void main(String[] args) {
    String pattern;
    String value;
    
    
    pattern = "toggle_info('../ajax/product_detail.php?divid=*&product_id=*','*')";
    value = "toggle_info('../ajax/product_detail.php?divid=dfs&product_id=35sdfsdf','23432')";
    System.out.println(new JSPattern(pattern).match(value));
    
    pattern = "toggle_info('*','*')";
    value = "toggle_info('../ajax/product_detail.php?divid=df&product_id=35sdfsdf', 23432)";
    System.out.println(new JSPattern(pattern).match(value));
    
    
  }
}
