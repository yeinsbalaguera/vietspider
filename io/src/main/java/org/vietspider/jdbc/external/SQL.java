/***************************************************************************
 * Copyright 2003-2012 by VietSpider - All rights reserved.                *    
 **************************************************************************/
package org.vietspider.jdbc.external;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *  Author : Nhu Dinh Thuan
 *  Email:nhudinhthuan@yahoo.com
 *  Website: vietspider.org       
 * Jan 16, 2012
 */
class SQL {

  private String variable;
  private List<String> queryElements = new ArrayList<String>();
  private String defaultValue;
  private List<String> uiqElements = new ArrayList<String>();

  SQL(String text) {
    text = text.trim();
    String sql = null;
    if(text.charAt(0) == '@') {
      int idx = text.indexOf('=');
      if(idx > 0) {
        variable = text.substring(0, idx).trim();
        sql = text.substring(idx+1).trim();
      }
    }
    if(sql == null) sql = text;
    
    int idx = sql.indexOf("@default.value");
    if(idx  > 0) {
      int start = idx;
      while(start > 0) {
        char c = sql.charAt(start);
        if(c == '[') break;
        start--;
      }
      
      int end = idx+1;
      while(end < sql.length()) {
        char c = sql.charAt(end);
        if(c == ']') break;
        end++;
      }
      if(end > start) {
        defaultValue = sql.substring(start, end);
        sql = sql.substring(0, start) + sql.substring(end+1);
        idx = defaultValue.indexOf('=');
        if(idx < 0) idx = defaultValue.indexOf(' ');
        if(idx > 0) defaultValue = defaultValue.substring(idx+1).trim();
      }
    }
    
    idx = sql.indexOf("@unique.identifier");
    if(idx  > 0) {
      int start = idx;
      while(start > 0) {
        char c = sql.charAt(start);
        if(c == '[') break;
        start--;
      }
      
      int end = idx+1;
      while(end < sql.length()) {
        char c = sql.charAt(end);
        if(c == ']') break;
        end++;
      }
      if(end > start) {
        String uniqueIdentifierQuery = sql.substring(start, end);
        sql = sql.substring(0, start) + sql.substring(end+1);
        idx = uniqueIdentifierQuery.indexOf('=');
        if(idx < 0) idx = uniqueIdentifierQuery.indexOf(' ');
        if(idx > 0) uniqueIdentifierQuery = uniqueIdentifierQuery.substring(idx+1).trim();
        SQLUtils.splitSQL(uiqElements, uniqueIdentifierQuery);
      }
    }
    
    SQLUtils.splitSQL(queryElements, sql);
    
  }
  
  public String getDefaultValue() { return defaultValue; }
  
//  public String getUniqueIdentifierQuery() { return uniqueIdentifierQuery; }
  
  String getVariable() { return variable; }


  boolean isType(String prefix) {
    for(int i = 0; i < queryElements.size(); i++) {
      if(queryElements.get(i).indexOf(prefix) > -1) return true;
    }
    return false;
  }
  
  List<String> getQuery() { return queryElements; }
  
  List<String> getUniqueIdentifierQuery() { return uiqElements; }
  

  public static void main(String[] args) {
    String text = "insert into table (name, class) values('@name', '@class_name')";
    SQL sql = new SQL(text);
    HashMap<String, Object> map = new HashMap<String, Object>();
    map.put("@name", "nhu dinh thuan");
    map.put("@class_name", "te te");
    System.out.println(SQLUtils.build(sql.getQuery(), map)[0]);

    text = "@test=insert into table (name, class) values('@name', '@class_name', '@name')";
    sql = new SQL(text);
    System.out.println(SQLUtils.build(sql.getQuery(), map)[0]);
    
    text = "@test=insert into table (name, class) values('@name', '@class_name', '@name?')" +
    		" [@default.value=abc]" +
        "[@unique.identifier=select top 1 from table1]";
    sql = new SQL(text);
    System.out.println(SQLUtils.build(sql.getQuery(), map)[0]);
    System.out.println(SQLUtils.build(sql.getQuery(), map)[1]);
    System.out.println(sql.getDefaultValue());
//    System.out.println(sql.getUniqueIdentifierQuery());
    
    
    text = "@test=insert into table (name, class) values('@name', '@class_name', '@name?', '@class_name)";
    sql = new SQL(text);
    System.out.println(SQLUtils.build(sql.getQuery(), map)[0]);
    System.out.println(SQLUtils.build(sql.getQuery(), map)[1]);
    
    String format = "yyyyMMdd HH:mm:ss";
    SimpleDateFormat dateFormat = new SimpleDateFormat(format);
  }

}
