/***************************************************************************
 * Copyright 2003-2012 by VietSpider - All rights reserved.                *    
 **************************************************************************/
package org.vietspider.jdbc.external;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *  Author : Nhu Dinh Thuan
 *  Email:nhudinhthuan@yahoo.com
 *  Website: vietspider.org       
 * Jan 19, 2012
 */
public class SQLUtils {
  
 static Object[] build(List<String> list, Map<String, Object> map) {
    List<Object> objects = new ArrayList<Object>();
    StringBuilder builder = new StringBuilder();
    for(int i = 0; i < list.size(); i++) {
      String element = list.get(i);
      if(element.charAt(0) == '@') {
        boolean param = false;
        if(element.charAt(element.length()-1) == '?') {
          element = element.substring(0, element.length()-1);
          param = true;
        }
        Object value = map.get(element);
        if(param) {
          objects.add(value);
          builder.append('?');
        } else {
          if(value != null) {
            builder.append(value);
          } else {
            builder.append(' ');
          }
        }
      } else {
        builder.append(element);
      }
    }
    
//    System.out.println(" =========  > query " + builder);
    if(objects.size() > 0) {
      objects.add(0, builder.toString());
      return objects.toArray(new Object[objects.size()]);
    }
    
    
    return new String[]{builder.toString()};
  }
  
  static void splitSQL(List<String> list, String sql) {
    int idx = 0;
    int start = 0;
    boolean isVar = false;
    while(idx < sql.length()) {
      char c = sql.charAt(idx);
      if(c == '@') {
        if(idx > start) {
          list.add(sql.substring(start, idx));
        }
        start = idx;
        isVar = true;
      } else if(isVar && !isValidName(c)) {
        if(idx > start) {
          list.add(sql.substring(start, idx));
        }
        start = idx;
        isVar = false;
      }
      idx++;
    }
    if(start < sql.length()) {
      list.add(sql.substring(start));
    }
  }
  
  private static boolean isValidName(char c) {
    switch (c) {
    case '.':
    case '_':
    case '?':
      return true;
    default:
      return Character.isLetterOrDigit(c);
    }
  }
}
