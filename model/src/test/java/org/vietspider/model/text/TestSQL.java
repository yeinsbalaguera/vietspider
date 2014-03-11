/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.model.text;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Nov 6, 2006
 */
public class TestSQL {
  
  private static StringBuilder replace(String name, StringBuilder sql, String value){
    int index = sql.indexOf(name);
    if(index < 0) return sql;
    StringBuilder builder = new StringBuilder(); 
    while(index > -1){
      builder.setLength(0);  
      builder.append(sql.substring(0, index));
      builder.append(value);      
      builder.append(sql.substring(index+name.length()));
      sql.setLength(0);
      sql.append(builder);
      index = sql.indexOf(name);
    }
    return builder;
  }
  
  public static void main(String[] args) {
    StringBuilder builder = new StringBuilder(" coi nhan tam $a thi co $a");
    builder = replace("$a", builder, "thuan");
    System.out.println(builder);
  }
}
