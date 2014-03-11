/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.link.pattern.model;

import java.net.URLDecoder;
import java.net.URLEncoder;

import org.vietspider.js.JsUtils;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 22, 2008  
 */
public class JSOnclickPattern extends JSPattern {

  protected String template;
  
  public JSOnclickPattern() {
  }

  public void setValue(String...values) {
    if(values.length < 2) return;
    super.setValue(values[0]);
    this.template = values[1];
    if(template == null) return; 
    template = template.trim();
    if(template.isEmpty()) template = null;
  }

  public String create(String value) {
    if(template == null) return null;
    value = clearFunctions(value);
    if(value == null) return null;
   /* for(String js : jses) {
      int index = value.indexOf(js);
      System.out.println(" js "+ js+ " value "+ value + " inde x "+ index);
      while(index > -1) {
        value = value.substring(index);
        if(!isFunction(value, js.length())) {
          index = value.indexOf(js, js.length());
          continue;
        }
        break;
      }
      if(index < 0) return null;
    }*/
    
    String [] params = JsUtils.getParams(value);
    if(params.length < 1) return null;
    String url  = template;
    for(int i = 0; i < params.length; i++) {
      try {
        params[i] = URLEncoder.encode(params[i], "utf-8");
        url = url.replaceAll("\\{"+String.valueOf(i+1)+"\\}", params[i]);
        url = URLDecoder.decode(url, "utf-8");
      } catch (Exception e) {
        url = url.replaceAll("\\{"+String.valueOf(i+1)+"\\}", params[i]);
      }
    }
//    System.out.println(" thanh ----->" + url);
    return url;
  }
  
  public String clearFunctions(String value) {
    for(int i = 0; i < jses.size(); i++) {
      if(value == null) return null;
      int index = value.indexOf(jses.get(i));
      if(index < 0) return null;
      index = index + jses.get(i).length();
      value = trimValue(value.substring(index));
    }
    return value;
  }
  
  private String trimValue(String value) {
    int idx = value.length() - 1;
    while(idx > -1) {
      char c = value.charAt(idx);
      if(Character.isWhitespace(c) || Character.isSpaceChar(c)) {
        idx--;
        continue;
      }
     
      if(c == ';' || c == ')') return value.substring(0, idx);
      break;
    }
    if(idx < 1) return value;
    return value.substring(0, idx+1);
  }
  
 /* public static void main(String[] args) {
    JSOnclickPattern paatern  = new JSOnclickPattern();
    String value  = "abc";
    System.out.println(paatern.trimValue(value)+"|");
  }*/
  
  /*private boolean isFunction(String value, int index) {
    int idx = value.indexOf(')', index);
    if(idx < 0) return false;
    idx++;
    
    while(idx < value.length()) {
      char c = value.charAt(idx);
      if(Character.isWhitespace(c) || Character.isSpaceChar(c)) {
        idx++;
        continue;
      }
     
      if(c == ';') return true;
      return false;
    }
    return true;
  }*/

}
