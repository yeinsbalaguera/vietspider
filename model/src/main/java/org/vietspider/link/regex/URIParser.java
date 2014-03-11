package org.vietspider.link.regex;

import org.vietspider.chars.URLUtils;
import org.vietspider.link.regex.Element.ElementType;


public class URIParser {

  public static URIElement parse(String url) {
    url = url.toLowerCase();
//    System.out.println(" parse url "+ url);
    
//    if(url.indexOf("/..") > -1) {
//      try {
//        url = new URI(url).normalize().toString();
//      } catch (Exception e) {
//      }
//    }
    int index = url.indexOf('(');
    int index2 = url.indexOf("://");
    
//    System.out.println(" index " + index + " : "+ index2 + " : " +
//        (index > 0 && (index2 < 0 || index2 > index)));
//    if(index2 > index) System.out.println(url.substring(index2));
    
    if(index > 0 
        && (index2 < 0 || index2 > index)) {
      URIElement elements = new URIElement();
      parseFunction(elements, url, index);
      return elements;
      //parse funtion;
    }
    
    
    URLUtils urlUtils = new URLUtils();
    url = urlUtils.getCanonical(url, false);
//    System.out.println("==============  >"+ url);

    index = url.indexOf("//");
    if(index < 0) {
      index = 0;
    } else {
      index += 2;
    }

    URIElement elements = new URIElement();
    parseHost(elements, url, index);
    return elements;
  }

  private static void parseFunction(
      URIElement elements, String function, int index) {
    String name = function.substring(0, index);
    elements.addElement(name, ElementType.FUNCTION);

    int max = function.lastIndexOf(')');
    if (max < 0) max = function.length();
    function = function.substring(index+1, max);

    int start = 0;
    index = 0;
    while(index < function.length()) {
      char c = function.charAt(index);
      while(Character.isWhitespace(c) 
          || Character.isSpaceChar(c)) {
        index++;
        c = function.charAt(index);
      }

      if(c == '\'') {
        int end = searchEnd(function, index);
        String value = function.substring(start, end);
//        System.out.println("value: " + function.substring(end));
        int idx = value.lastIndexOf('\'');
//        System.out.println(value + " : "+ idx);
        if(idx > 0) value = value.substring(0, idx);
        if(value.charAt(0) == '\'') value = value.substring(1);
        elements.addElement(value, ElementType.ELEMENT);
        index = end + 1;
        start = end + 2;
        continue;
      } 
      
      int end = searchEnd2(function, index);
      String value = function.substring(start, end);
      elements.addElement(value, ElementType.ELEMENT);
      index = end + 1;
      start = end + 2;
    }
    
    if(start < function.length()) {
      String value = function.substring(start);
      elements.addElement(value, ElementType.ELEMENT);
    }
  }

  private static int searchEnd(String function, int index) {
    while(index < function.length()) {
      char c = function.charAt(index);
      if(c == '\'') {
        if(index > 0 
            && function.charAt(index-1) == '\\') {
          index++;
          continue;
        }
        int end = nextChar(function, index+1);
        if(end == index) {
          index++;
          continue;
        }
        return end;
      }
      index++;
    }
    return function.length(); 
  }
  
  private static int searchEnd2(String function, int index) {
    while(index < function.length()) {
      char c = function.charAt(index);
      if(c == ',') return index;
      index++;
    }
    return function.length(); 
  }

  private static int nextChar(String function, int start) {
    int index = start;
    while(index < function.length()) {
      char c = function.charAt(index);
      if(Character.isWhitespace(c)
          || Character.isSpaceChar(c)) {
        index++;
        continue;
      }
      if(c == ',') return index;
      return start - 1;
    }
    return index;
  }

  private static void parseHost(
      URIElement elements, String url, int index) {
    int start = index;
    while(index < url.length()) {
      char c = url.charAt(index);
      if(c == '/') {
        String value = url.substring(start, index);
        elements.addElement(value, ElementType.HOST);
        parseElement(elements, url, index+1);
        return;
      }
      if(c == '.') {
        String value = url.substring(start, index);
        elements.addElement(value, ElementType.HOST);
        start = index+1;
      }
      index++;
    }

    if(start >= url.length()) return;
    String value = url.substring(start);
    elements.addElement(value, ElementType.HOST);
  }

  private static void parseElement(
      URIElement elements, String url, int index) {
    int start = index;
    while(index < url.length()) {
      char c = url.charAt(index);
      if(c == '?') {
        String value = url.substring(start, index);
        elements.addElement(value, ElementType.ELEMENT);
        parseParam(elements, url, index+1);
        return;
      }
      if(c == '/') {
        String value = url.substring(start, index);
        elements.addElement(value, ElementType.ELEMENT);
        start = index+1;
      }
      index++;
    }

    if(start >= url.length()) return;
    String value = url.substring(start);
    elements.addElement(value, ElementType.ELEMENT);
  }

  private static void parseParam(
      URIElement elements, String url, int index) {
    int start = index;
    while(index < url.length()) {
      char c = url.charAt(index);
      if(c == '#') {
        String value = url.substring(start, index);
        elements.addElement(value, ElementType.PARAM);
        parseReference(elements, url, index+1);
        return;
      }
      if(c == '&') {
        String value = url.substring(start, index);
        start = index+1;
        elements.addElement(value, ElementType.PARAM);
      }
      index++;
    }

    if(start >= url.length()) return;
    String value = url.substring(start);
    elements.addElement(value, ElementType.PARAM);
  }

  private static void parseReference(
      URIElement elements, String url, int index) {
    String value = url.substring(index+1);
    elements.addElement(value, ElementType.REFERENCE);
  }




}
