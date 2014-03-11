/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.webui.cms;

import java.io.File;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.vietspider.chars.TextSpliter;
import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 14, 2009  
 */
public class RequestUtils {
  
  private static List<String> ignoreKeys = new ArrayList<String>();
  private static long lastModified = -1;
  
  static {
    new Thread() {
      public void run() {
        while(true) {
          loadIgnoreKeys();
          try {
            Thread.sleep(10*1000l);
          } catch (Exception e) {
          }
        }
      }
    }.start();
  }
  
  private static void loadIgnoreKeys() {
    File file = UtilFile.getFile("system", "search.ignore.txt");
    if(file.lastModified() != lastModified) return;
    lastModified = file.lastModified();
    try {
      byte [] bytes = RWData.getInstance().load(file);
      String text = new String(bytes, Application.CHARSET);
      String [] elements = text.split("\n");
      for(int i = 0; i < elements.length; i++) {
        elements[i] = elements[i].trim().toLowerCase();
        if(elements[i].length() < 1) continue;
        ignoreKeys.add(elements[i]);
      }
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }
  
  public static List<String> getHistory(Header header, String pattern) {
    List<String> list = new ArrayList<String>();
    list.add(pattern);
    if(header == null) return list;
    String text = header.getValue();
    
    if(text == null) return list;
    int index = text.indexOf("history=");
    if(index < 0) return list;
    
    int end  = text.indexOf(';', index);
    if(end < 0) end = text.length();
    
    String history = pattern;
    try {
      history = URLDecoder.decode(text.substring(index+8, end), "utf-8");
    } catch (Exception e) {
    }
    
    TextSpliter spliter = new TextSpliter();
    list = spliter.toList(history, '|');
    for(int i = 0; i < list.size(); i++) {
      if(list.get(i).equalsIgnoreCase(pattern)) return list;
    }
    
    if(list.size() >= 10) list.remove(0);
    list.add(pattern);
    return list;
  }
  
  public static String getCookie(Header header, String name) {
    if(header == null) return null;
    String text = header.getValue();
    
    if(text == null) return null;
    int index = text.indexOf(name + "=");
    if(index < 0) return null;
    
    int end  = text.indexOf(';', index);
    if(end < 0) end = text.length();
    
    return text.substring(index  + name.length() + 1, end);
  }
  
  public static String toCookieValues(List<String> histories) {
    StringBuilder builder = new StringBuilder();
    for(int i = 0; i < histories.size(); i++) {
      if(i > 0) builder.append('|');
      try {
        builder.append(URLEncoder.encode(histories.get(i), "utf-8"));
      } catch (Exception e) {
      }
    }
    builder.insert(0, "history=");
    builder.append("; path=/;");
    
    return builder.toString();
  }
  
  public static long getSession(Header header) {
    if(header == null) return -1;
    
    String text = header.getValue();
    if(text == null) return -1;
    int index = text.indexOf("client=");
    if(index < 0) return -1;
    
    int end  = text.indexOf(';', index);
    if(end < 0) end = text.length();
    
    try {
      return Long.parseLong(text.substring(index+7, end));
    } catch (Exception e) {
      return -1;
    }
  }
  
  public static boolean isBot(Header headerAgent) {
    if(headerAgent == null 
        || headerAgent.getValue() == null) return false;
    String value = headerAgent.getValue().toLowerCase();
    return isBot(value);
  }
  
  public static boolean isBot(String value) {
    if(value.indexOf("googlebot") > -1
        || value.indexOf("msnbot") > -1
        || (value.indexOf("yahoo") > -1 && value.indexOf("slurp") > -1
        || value.indexOf("alexa") > -1)  
        || value.indexOf("bingbot") > -1        
            ) return true;
    
    return false;
  }
  
  public static boolean isInvalidBot(Header headerAgent) {
    if(headerAgent == null 
        || headerAgent.getValue() == null) return true;
    String value = headerAgent.getValue();
    return isInvalidBot(value);
  }
  
  public static boolean isInvalidBot(String value) {
    value = value.toLowerCase();
    if(value.indexOf("mj12bot") > -1
        || value.indexOf("itim") > -1
        || value.indexOf("timkimsearch") > -1
        || value.indexOf("sitebot") > -1 
        || value.indexOf("heritrix") > -1
        || value.indexOf("discobot") > -1
        || value.indexOf("yeti") > -1
        || value.indexOf("baiduspider") > -1
        || value.indexOf("vietnamese search") > -1
        || value.indexOf("ezooms.bot") > -1
        || value.indexOf("yandexbot") > -1) return true;
    return false;
  }
  
  public static boolean isInValidKeyWord(String pattern) {
    if(pattern == null) return true;
    String lpattern = pattern.toLowerCase();
    for(int i = 0; i < ignoreKeys.size(); i++) {
      if(lpattern.indexOf(ignoreKeys.get(i)) > -1) {
        return true;
      }
    }
    return false;
  }
  
  public static void main(String[] args) throws Exception {
    String text = "http://www.google.com.vn/url?sa=t&rct=j&q=&esrc=s&source=web&cd=1&ved=0CCQQFjAA&url=http%3A%2F%2Fnik.vn%2Ftin%2Ftonghoptin%2Fdetail%2F201202140835200017%2FThu-tuong-Nguyen-Tan-Dung--Day-nhanh-tien-do-xay-Khu-Cong-nghe-cao-Hoa-Lac-%2F&ei=XBp8T_zQNqWXiQemwsCOCQ&usg=AFQjCNGNcTOAVBnJ447qb2sJdgsAHNxRbg&sig2=PShxcIAtRSNrl4dAoIw1Yg";
    System.out.println(URLDecoder.decode(text, "utf8"));
  }
  
}
