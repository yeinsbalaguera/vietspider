/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.link.pattern.model;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.chars.URLUtils;
import org.vietspider.common.text.SWProtocol;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 22, 2008  
 */
public class URIPattern2 implements IPattern {

  protected Component [] components ;

  protected List<URIPattern2> nPatterns = new ArrayList<URIPattern2>();

  public URIPattern2() {
  }

  public URIPattern2(String pattern) {
    URLUtils urlUtils = new URLUtils();
    String nPattern = urlUtils.getCanonical(pattern);
    if(nPattern.length() != pattern.length()) {
      nPatterns.add(new URIPattern2(nPattern));
    }
      
    this.components = split2Pattern(pattern.toLowerCase());
//    System.out.println(" total "+ components.length);
//    for(Component ele : components) {
//      System.out.println("==va >"+ ele.text + " : "+ele.type);
//    }
    if(components.length < 1) return;
    int start = SWProtocol.lastRealValueURL(components[0].text, false);
    if(start > 0) components[0].text = components[0].text.substring(start);
  }

  public void setValue(String...values) {
    if(values.length < 1) return;
    this.components = split2Pattern(values[0].toLowerCase());

    URLUtils urlUtils = new URLUtils();
    String nPattern = urlUtils.getCanonical(values[0]);
    if(nPattern.length() != values[0].length()) {
      nPatterns.add(new URIPattern2(nPattern));
    }

    if(components.length < 1) return;
    int start = SWProtocol.lastRealValueURL(components[0].text, false);
    if(start > 0) components[0].text = components[0].text.substring(start);
  }

  public boolean match(String value) {
    if(components.length < 1) return true;
    value = value.toLowerCase();
    if(internalMatch(value)) return true;
    for(int i = 0; i < nPatterns.size(); i++) {
      if(nPatterns.get(i).match(value)) return true; 
    }
    return false;
  }

  private boolean internalMatch(String value) {
    int start = SWProtocol.lastRealValueURL(value, components[0].text.length() < 1);
    char [] separators = {'/'};
    if(start < 1) {
      separators = new char[]{','};
    } else {
      separators = new char[]{'/', '?', '&', '#'};
    }
    value = value.substring(start).trim();
//    System.out.println(" chuan bi khoi chuoi "+ value);
    
    char type  = '*';

    for(int i = 0; i < components.length; i++) {
      System.out.println(" \n\n == elements >"+ components[i].text);
      if(components[i].text.length() < 1) {
        type  = components[i].type;
        continue;
      }
      
      int idx = value.indexOf(components[i].text);
      if(idx < 0) return false;

//      System.out.println(components[i].text + " : "+ components[i].type);
      String data  = value.substring(0, idx);
      if(data.length() > 0) { 
//        System.out.println(" co |"+ data + "| ==  > "+ type);
        for(int k = 0; k < separators.length; k++) {
          if(data.indexOf(separators[k]) > -1) return false;
        }
      }
      
      if(type != '*' && !checkType(data, type)) return false;
      
      System.out.println(" dau tien " + value);
      value = value.substring(components[i].text.length());
      System.out.println(" step 1 " + value);
      System.out.println(data+ "===="+ data.length());
      value = value.substring(data.length());
      System.out.println(" step 2 " + value);
      type  = components[i].type;
            System.out.println(" ==  value >"+ value +"\n\n");
    }

//        System.out.println(" ==  con lai value |"+ value + "| : "+ type);

    int idx = value.indexOf('/');
    if(idx > -1 && idx < value.length() - 1) return false;

    idx = value.indexOf('?');
    if(idx > -1 && idx < value.length() - 1) return false;

    idx = value.indexOf('&');
    if(idx > -1 && idx < value.length() - 1) return false;
    
    idx = value.indexOf('#');
    if(idx > -1 && idx < value.length() - 1) return false;
    
    if(type != '*' && !checkType(value, type)) return false;
    
    String last = components[components.length - 1].text;
    System.out.println(" === last ==> |"+ last+"|");
    if(last.length() == 0) return true;
    if(last.length() == 1){
      if(last.charAt(last.length() - 1) == '*')  return true;
      return checkType(value, type);
    }
    
    System.out.println(" hihihihihisdasd |"+ value+"|");

    return false;
  }

  protected int match(String value, String pattern, int start) {
    if(start + pattern.length() > value.length()) return -1;
    //    System.err.println(value.substring(start) + " : "+ pattern);
    int i = 0; 

    while(i < pattern.length()) {
      char c1 =  pattern.charAt(i);
      char c2 = value.charAt(start+i);
      if(c1 != c2  
          && Character.toLowerCase(c1) != Character.toLowerCase(c2)) return -1;
      i++;
    }

    return start+i;
    //  return i > 0 ? start+i : -2;
  }

  private boolean checkType(String text, char type) {
    int index = 0;
    while(index < text.length()) {
      char c = text.charAt(index);
      if(type == '$' && Character.isLetter(c)) return false;
      else if(type == '@' && Character.isDigit(c)) return false;
      index++;
    }
    return true;
  }

  public String[] getPattern() {
    String [] elements = new String[components.length];
    for(int i = 0; i < elements.length; i++) {
      elements[i] = components[i].text;
    }
    return elements; 
  }

  public String toString() {
    StringBuilder builder = new StringBuilder();
    for(int i = 0; i < components.length; i++) {
      if(i > 0) builder.append('*');
      builder.append(components[i]);
    }
    return builder.toString();
  }
  
  private Component[] split2Pattern(String value) {
    int start  = 0;
    int index  = 0;
    List<Component> list = new ArrayList<Component>();
    char c = ' ';
    while(index < value.length()) {
      c = value.charAt(index);
      if(c == '*' || c == '@' || c == '$') {
        String text = value.substring(start, index);
        list.add(new Component(text, c));
        start = index+1;
      }
      index++;
    }
    
    if(c == '*'  || c == '@' || c == '$') {
      list.add(new Component("", c));
    } else if(start < value.length()) {
      String text = value.substring(start, value.length());
      list.add(new Component(text, '*'));
    }
    
    return list.toArray(new Component[list.size()]);
  }
  
  protected class Component {
    
    protected String text;
    protected char type = '*';
    
    protected Component(String txt, char c) {
      this.text = txt;
      this.type = c;
    }
    
  }
}
