/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.browser;

import java.util.ArrayList;
import java.util.List;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 26, 2009  
 */
public class TemplateDetector {
  public static String [] split(String value) {
    List<String> list = new ArrayList<String>();
    int start  = 0;
    int index = 8;
    while(index < value.length()) {
      char c = value.charAt(index);
      if(c == '/' || c == '?' || c == '&' 
        || c == '#' || c == '|' ||  c == '=')  {
         list.add(value.substring(start, index));
         start = index+1;
      }
      index++;
    }
    if(start < value.length()) {
      list.add(value.substring(start, value.length())); 
    }
    
    
    return list.toArray(new String[list.size()]);
  }
  
  public static String toTemplate(String [] links) {
//    System.out.println(links.length);
    if(links.length < 2) return null;
    List<Integer> indexes = new ArrayList<Integer>();
    String [] values_1 = split(links[0]);
    
//    System.out.println(links[0]);
    for(int t = 1;  t < links.length; t++) {
      String [] values_2 = split(links[t]);
      if(values_1.length != values_2.length) continue;
//      System.out.println(links[t]);  
      for(int i = 0; i < values_1.length; i++) {
        if(!values_1[i].equalsIgnoreCase(values_2[i])) {
          indexes.add(i);
        }
      }
      if(indexes.size() > 0) break;
    }
    
    String template = links[0];
    for(int i = 0; i < indexes.size(); i++) {
      String pattern = values_1[indexes.get(i)];
      template = template.replaceAll(pattern, "*");
    }
    return template;
  }
}
