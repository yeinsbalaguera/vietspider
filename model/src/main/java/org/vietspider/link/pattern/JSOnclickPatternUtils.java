/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.link.pattern;

import org.vietspider.js.JsUtils;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 17, 2008  
 */
public class JSOnclickPatternUtils {
  
  public static String toTemplates(String value) {
    String [] elements = value.split("\n");
    StringBuilder builder = new StringBuilder();
    for(int i = 0; i < elements.length - 1; i += 2) {
      if(builder.length() > 0) builder.append('\n');
      if(elements[i].indexOf('*') > -1) {
        builder.append(elements[i]).append('\n').append(elements[i+1]);  
        continue;
      }
      String [] data = toTemplate(elements[i], elements[i+1]);
      builder.append(data[0]).append('\n').append(data[1]);
    }
    return builder.toString();
  }
  
  public static String[] toTemplate(String jsFunction, String url) {
    String [] params = JsUtils.getParams(jsFunction);
    
    String jsTemplate = jsFunctionToTemplate(jsFunction).trim();
    
    int i = 0;
    while(i < jsFunction.length()) {
      char c = jsFunction.charAt(i);
      i++;
      if(c == '(') break;
      if(c == '=') {
        if(params.length > 0) {
          url = url.replace(params[0], "{1}");
        }
       return new String[]{jsTemplate, url};
      }
    }
    
    return new String[]{jsTemplate, urlToTemplate(url, params)};
  }
  
  private static int indexOfParam(String [] params, String value) {
    if((value = value.trim()).isEmpty()) return -1;
    for(int i = 0; i < params.length; i++) {
      if(params[i].isEmpty()) continue;
      if(params[i].equals(value)) return i+1;
    }
    return -1;
  }
  
  public static String urlToTemplate(String url, String [] params) {
    int i = 0;
    int start = 0;
    
    StringBuilder builder = new StringBuilder();
    
    while(i < url.length()) {
      char c = url.charAt(i);
      if(c == '/') {
        i++;
        int startElement = i;
        while(i < url.length()) {
          c = url.charAt(i);
          if(c == '/' || c == '?') break;
          i++;
        }
        String value = url.substring(startElement, i);
        int index = indexOfParam(params, value);
        if(index > -1) {
          builder.append(url.subSequence(start, startElement));
          builder.append("{").append(index).append("}");
          start = i; 
        }
      } else if(c == '=') {
        i++;
        int startElement = i;
        while(i < url.length()) {
          c = url.charAt(i);
          if(c == '&' || c == '#') break;
          i++;
        }
        String value = url.substring(startElement, i);
        int index = indexOfParam(params, value);
        if(index > -1) {
          builder.append(url.subSequence(start, startElement));
          builder.append("{").append(index).append("}");
          start = i; 
        }
      }
      i++;
    }
    
    if(start < url.length()) {
      builder.append(url.subSequence(start, url.length()));
    }
    
    return builder.toString();
  }
  
  public static String urlToTemplate(String url, int pos) {
    int start = pos;
    int end = pos;
    while(true) {
      int length = url.length();
      if(start >= length) break;
      while(start < length) {
        char c = url.charAt(start);
        if(c == '=' || c == '/' || c == '#') {
          start++;
          break;
        }
        start++;
      }
      end  = start;
      while(end < length) {
        char c = url.charAt(end);
        if(c == '&' || c == '/' || c == '?' || c == '#') break;
        end++;
      }
      if(end >= length) break;
      if(start >= end) continue;
      url = url.substring(0, start)+"*" + url.substring(end, length);
    }
    
    if(start < url.length()) {
      url = url.substring(0, start)+"*'";
    }
    
    return url;
  }
  
  public static String jsFunctionToTemplate(String jsFunction) {
    StringBuilder builder = new StringBuilder();
    int i = 0;
    
    while(i < jsFunction.length()) {
      char c = jsFunction.charAt(i);
      i++;
      if(c == '(') break;
      if(c == '=') return urlToTemplate(jsFunction, i+1);
    }
    
    int startS = 0;
    while(i < jsFunction.length()) {
      char c = jsFunction.charAt(i);
      
      while(i < jsFunction.length() 
          && (Character.isWhitespace(c) || Character.isSpaceChar(c)) ) {
        i++;
        c = jsFunction.charAt(i);
      }
      
      if(c == ',') {
        i++;
        continue;
      } else if (c  == ')' && jsFunction.charAt(i-1) != '\\' ) {
        break;
      }
      
      while(i < jsFunction.length() 
          && (Character.isWhitespace(c) || Character.isSpaceChar(c)) ) {
        i++;
        c = jsFunction.charAt(i);
      }
      
      if(c == '\'') {
        i++;
        int start = i;
        while(i < jsFunction.length()) {
          c = jsFunction.charAt(i);
          if( (c == '\'' && jsFunction.charAt(i-1) != '\\')
              || (c  == ')' && jsFunction.charAt(i-1) != '\\')) break;
          i++;
        }
        builder.append(jsFunction.subSequence(startS, start));
        builder.append('*');
        startS = i;
        
        i++;
        continue;
      } 
      
      if(c == '"') {
        i++;
        int start = i;
        while(i < jsFunction.length()) {
          c = jsFunction.charAt(i);
          if( (c == '"' && jsFunction.charAt(i-1) != '\\')
              || (c  == ')' && jsFunction.charAt(i-1) != '\\')) break;
          i++;
        }
        builder.append(jsFunction.subSequence(startS, start));
        builder.append('*');
        startS = i;
        
        i++;
        continue;
      } 
      
      int start = i;
      i++;
      while(i < jsFunction.length()) {
        c = jsFunction.charAt(i);
        if((c == ',' && jsFunction.charAt(i-1) != '\\')
            || (c  == ')' && jsFunction.charAt(i-1) != '\\') ) break;
        i++;
      }

      builder.append(jsFunction.subSequence(startS, start));
      builder.append('*');
      startS = i;

    }
    
    if(startS < jsFunction.length()) {
      builder.append(jsFunction.subSequence(startS, jsFunction.length()));
    }
    
    return builder.toString();
  }
  

  //viewNameUI(event,'12000169763','','�?nh starf','');
  
}
