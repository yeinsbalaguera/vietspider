/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.js;

import java.util.ArrayList;
import java.util.List;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 8, 2008  
 */
public class JsParser {
  
  public static  List<JsFunction> parse(String value) {
    List<JsFunction> jsFunctions = new ArrayList<JsFunction>();
    JsFunction jsFunction = null;
    
    int start = 0;
    int end = value.indexOf('(');
    if(end < 0) return jsFunctions;
    int index = end+1;
    while(index < value.length()) {
      char c = value.charAt(index);
      if (Character.isWhitespace(c) 
          || Character.isSpaceChar(c)) {
        index++;
        continue;
      }
      
      if(c == '\'' || c == '\"') {
        jsFunction = new JsFunction(value.substring(start, end));
        index = searchEnd(value, index+1, index, c, jsFunction);
        if(jsFunction.getValue() != null) jsFunctions.add(jsFunction);
      } else {
        jsFunction = new JsFunction(value.substring(start, end));
        index = searchEnd(value, index+1, index, jsFunction);
        if(jsFunction.getValue() != null) jsFunctions.add(jsFunction);
      }
      
      //go to ';'
      while(index < value.length()) {
        c = value.charAt(index);
        if (Character.isWhitespace(c) 
            || Character.isSpaceChar(c)) {
          index++;
          continue;
        }
        if(c == ';') index++;
        break;
      }
      
      if(index < 0 || index >= value.length()) break;
      
      start = index;
      end = value.indexOf('(', start);
      if(end < start) return jsFunctions;
      index = end+1;
    }
    
    return jsFunctions;
  }
  
  private static int searchEnd(String value, 
      int startValue, int start, char quote, JsFunction jsFunction) {
    int end = value.indexOf(')', start);
    
    if(end > start && value.charAt(end-1) == '\\') {
      return searchEnd(value, startValue, end+1, quote, jsFunction);
    }
    
    if(end <= startValue) {
      jsFunction.setValue(cutValue(value, startValue, value.length()));
      return value.length();
    }
    
    int index = end-1;
    while(index > start) {
      char c = value.charAt(index);
      if (Character.isWhitespace(c) 
          || Character.isSpaceChar(c)) {
        index--;
        continue;
      }
      
      if(c == quote) {
        if(index > start && value.charAt(index-1) == '\\') break;
        jsFunction.setValue(cutValue(value, startValue, index));
        return end+1;
      }
      break;
    }
    
    return searchEnd(value, startValue, end+1, quote, jsFunction);
  }
  
  private static int searchEnd(String value, 
      int startValue, int start, JsFunction  jsFunction) {
    int end = value.indexOf(')', start);
    
    if(end > start && value.charAt(end-1) == '\\') {
      return searchEnd(value, startValue, end+1, jsFunction);
    }
    
    if(end <= startValue) {
      jsFunction.setValue(cutValue(value, startValue, value.length()));
      return value.length();
    }

    jsFunction.setValue(cutValue(value, startValue, end-1));
    return end+1;
  }
  
  private static String cutValue(String value, int start, int end) {
    int index = start;
    StringBuilder builder = new StringBuilder();
    while(index < end) {
      char c  = value.charAt(index);
      index++;
      if(c == '\\') continue;
      builder.append(c);
    }
    
    return builder.toString();
  }
  
//  public static void main(String[] args) throws Exception {
//    String function = "show_postcontent(  adasd'd \\(sfsdf \\)' as );\n";
//    function += " show_postcontent2('dasdasaew') ";
//    
//    List<JsFunction> list = parse(function);
//    System.out.println(list.size());
//    for(JsFunction js : list) {
//      System.out.println("============================================================");
//      System.out.println(js.getName());
//      System.out.println("------------------------------------------------------------");
//      System.out.println(js.getValue());
//      System.out.println("============================================================");
//    }
//  }
  
}
