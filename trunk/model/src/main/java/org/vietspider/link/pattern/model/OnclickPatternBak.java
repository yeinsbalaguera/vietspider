/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.link.pattern.model;

import java.util.ArrayList;
import java.util.List;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 22, 2008  
 */
public class OnclickPatternBak extends JSPattern {
  
  protected  String template;
  
  public OnclickPatternBak() {
  }
  
  public void setValue(String...values) {
    if(values.length < 2) return;
    super.setValue(values[0]);
    this.template = values[1];
    if(template == null) return; 
    template = template.trim();
    if(template.isEmpty()) template = null;
  }
  
//  @TODO replace all function
  public String create(String value) {
    if(template == null || !match(value)) return null;
    String [] params = getParams(value, '\'');
    if(params.length < 1) params = getParams(value, '\"');
    if(params.length < 1) params = getParams(value, '(', ',', ')');
    if(params.length < 1) return null;
    String url  = template;
    for(int i = 0; i < params.length; i++) {
      url = url.replaceAll("\\{"+String.valueOf(i+1)+"\\}", params[i]);
    }
//    System.out.println(" thanh ----->" + url);
    return url;
  }
  
  private String [] getParams(String text, char...chars) {
    int i = 0;
    int start = -1;
    List<String> list = new ArrayList<String>();
    while(i < text.length()) {
      char c = text.charAt(i);
      if(c == chars[0]) {
        if(start < 0) {
          start = i;
        } else {
          start = start+1;
          if(start < i && i < text.length()) {
            list.add(text.substring(start, i));
          }
          start = -1;
        }
      } else if(start > -1) {
        boolean isEnd = false;
        for(int j = 1; j < chars.length; j++) {
          if(c != chars[j]) continue;
          isEnd = true;
          break;
        }
        
        if(isEnd) {
          start++;
          if(start < i && i < text.length()) {
            String v = text.substring(start, i);
            list.add(v);
            start += v.length()+1;
          }
        }
      }
      i++;
    }
    return list.toArray(new String[list.size()]);
  }
}
