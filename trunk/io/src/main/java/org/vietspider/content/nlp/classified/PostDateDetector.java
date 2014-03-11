/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.nlp.classified;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.vietspider.common.text.CalendarUtils;
import org.vietspider.locale.DetachDate;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 6, 2009  
 */
public class PostDateDetector extends AbstractDateDetector {

  final String [] keys = {
      "ngày đăng", "ngày cập nhật", "cập nhật", "đăng lúc", "gửi lúc", "gửi ngày",
      "update", "đưa lúc", "post", "thời hạn"
    };

  public String detectDate(String text) {
    SimpleDateFormat dateFormat = CalendarUtils.getDateTimeFormat();
    List<String> list = split(text.toLowerCase());
    for(int k = 0; k < list.size(); k++) {
      String value  = list.get(k);
      int index = -1;
      for(int i = 0; i < keys.length; i++) {
        index = value.indexOf(keys[i]);
        if(index > -1) break;
//          System.out.println(keys[i]);
//          break;
//        }
      }
      
      if(index < 0) continue;
      
      for(int j = k; j < Math.min(list.size(), k + 4); j++) {
        value = list.get(j);
//        System.out.println("======================"+ index);
//        System.out.println(value);
        DetachDate detachDate = detectInstance(value);
        if(detachDate != null) return dateFormat.format(detachDate.toDate());
      }
    }
    
    for(int i = 0; i < Math.min(list.size(), 4); i++) {
      String value = list.get(i);
//      System.out.println("================111111111111111 ======");
//      System.out.println(value);
      DetachDate detachDate = detectInstance(value);
//      System.out.println(" ===> "+ detachDate);
      if(detachDate != null) return dateFormat.format(detachDate.toDate());
    }
    
    int i = Math.max(list.size() - 4, 0);
    for(; i < list.size(); i++) {
      String value = list.get(i);
//      System.out.println("============33333333333333333333==========");
//      System.out.println(value);
      DetachDate detachDate = detectInstance(value);
      if(detachDate != null) return dateFormat.format(detachDate.toDate());
    }
    
    return null;
  }
  
  private List<String> split(String text) {
    List<String> list = new ArrayList<String>();
    int start = 0;
    int index  = 0;
    int length = text.length();
    while(index < length) {
      if(text.charAt(index) == '\n') {
        String line = text.substring(start, index).trim();
        if(line.length() > 0) list.add(line);
        start = index+1;
      }
      index++;
    }
    return list;
  }

}
