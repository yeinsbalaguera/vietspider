/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.link;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.vietspider.common.io.LogService;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.model.Source;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 3, 2008  
 */
public final  class ContentFilter {

  private Pattern [] patterns;
  private boolean [] contains;

  public ContentFilter(Source source, String [] elements) {
    List<Pattern> patternList = new ArrayList<Pattern>();
    List<Boolean> containsList = new ArrayList<Boolean>();

    int style = Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE;
    for(int i = 0; i < elements.length; i++) {
      elements[i] = elements[i].trim();
      if(elements[i].length() < 1) continue;
      elements[i] = elements[i].toLowerCase();
      if(elements[i].charAt(0) == '^') {
        containsList.add(false);
        elements[i] = elements[i].substring(1);
      } else {
        containsList.add(true);
        //        contains[i] = true;
      }
      try {
//        System.out.println(encode(elements[i]));
        patternList.add(Pattern.compile(encode(elements[i]), style));
      } catch (Exception e) {
        containsList.remove(containsList.size() - 1);
        LogService.getInstance().setThrowable(source, e, "CONTENT_FILTER: Invalid pattern!");
      }
    }

    patterns = patternList.toArray(new Pattern[0]);
    contains = new boolean[containsList.size()];
    for(int i = 0; i < contains.length; i++) {
      contains[i] = containsList.get(i);
    }
  }

  private String encode(String value) {
    StringBuilder builder = new StringBuilder();
    for(int i = 0; i < value.length(); i++) {
      char c = value.charAt(i);
      switch (c) {
      case '(': 
      case ')': 
      case '.': 
      case ':': 
      case '?': 
      case '=': 
      case '!': 
      case '<': 
      case '>': 
      case '|': 
      case '{':
      case '}':
      case '+':
      case '^':
      case '$':
      case '[':
      case ']':
      case '&':
        //      case '^':
      case '-':
        //      case '':
        builder.append('\\').append(c);
        break;
      default:
        builder.append(c);
        break;
      }
    }

    return builder.toString();

  }


  //Thuannd update code
  public int check(List<HTMLNode> nodes) {
    for(int i = 0; i < contains.length; i++) {
      if(contains[i]) continue;
      if(check(patterns[i], nodes)) {
        LogService.getInstance().setMessage("CONTENT FILTER",  null, "Ignore by '"+ patterns[i]+"'");
        return -1;
      }
    }
    return 0;
    /*for(int i = 0; i < patterns.length; i++) {
//      System.out.println(" chuan bi check "+ patterns[i].pattern());
      if(check(patterns[i], nodes)) {
        if(!contains[i]) return -1;
        }
      } else {
        if(contains[i]) {
          System.out.println(" thay tu  khoa "+ patterns[i]);
          return false;
        }
      }
    }
    return true;*/
  }

  private boolean check(Pattern pattern, List<HTMLNode> nodes) {
    if(pattern == null) return true;
    for(int i = 0; i < nodes.size(); i++) {
      HTMLNode node = nodes.get(i);
      String value  = new String(node.getValue());
      if(isLinkNode(node, 0)) continue;
      Matcher matcher = pattern.matcher(value);
      if(matcher.find()) return true;
    }
    return false;
  }

  private boolean isLinkNode(HTMLNode node, int time) {
    if(time  == 3 || node == null) return false;
    if(node.isNode(Name.A)) return true;
    return isLinkNode(node.getParent(), time+1);
  }

  boolean mark(List<HTMLNode> nodes) {
    boolean _found = false;
    for(int i = 0; i < nodes.size(); i++) {
      HTMLNode node = nodes.get(i);
      char [] values = node.getValue();
      char [] newValues = mark(values);
      if(newValues.length != values.length) {
        _found = true;
      }
      if(values.length < newValues.length) node.setValue(newValues);
    }
    return _found;
  }

  private char[] mark(char [] chars)  {
    StringBuilder value  = new StringBuilder(chars.length + 10);
    value.append(chars);
    for(int i = 0; i < patterns.length; i++) {
      if(patterns[i] == null || !contains[i]) continue;
      value = mark(patterns[i], value);
    }
    return value.toString().toCharArray();
  }

  private StringBuilder mark(Pattern pattern, StringBuilder builder) {
    StringBuilder value = new StringBuilder();
    Matcher matcher = pattern.matcher(builder);
    int from = 0;
    while(matcher.find(from)) {
      int start = matcher.start();
      int end = matcher.end();
      value.append(builder.subSequence(from, start));
      value.append("<b style=\"background-color: rgb(255, 255, 94);\">");
      value.append(builder.subSequence(start, end));
      value.append("</b>");
      from = end;
    }

    if(from > 0 && from < builder.length()) {
      value.append(builder.subSequence(from, builder.length()));  
    }

    if(value.length() < 1) return builder; 
    return value;
  }

}
