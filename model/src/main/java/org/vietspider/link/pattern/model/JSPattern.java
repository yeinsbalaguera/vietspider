/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.link.pattern.model;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.js.JsUtils;
import org.vietspider.link.regex.URIElement;
import org.vietspider.link.regex.URIParser;
/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 22, 2008  
 */
public class JSPattern implements IPattern {
  
  public final static short LINK  = 0;
  public final static short DATA  = 1;
  public final static short ALL  = 2;
  
  protected List<String> jses = new ArrayList<String>();
  protected URIElement [] jsParams;
  
  protected short type = LINK;
  
  public JSPattern() {
  }
  
  public JSPattern(String jsFunction) {
    setValue(jsFunction);
  }
  
  public void setValue(String...values) {
    if(values.length < 1) return;
    if(values[0] == null) return;
    values[0] = values[0].trim();
    if(values[0].endsWith("#link")) {
      values[0] = values[0].substring(0, values[0].length() - "#link".length());
      type = LINK;
    } else  if(values[0].endsWith("#data")) {
      values[0] = values[0].substring(0, values[0].length() - "#data".length());
      type = DATA;
    } else if(values[0].endsWith("#all")) {
      values[0] = values[0].substring(0, values[0].length() - "#all".length());
      type = ALL;
    }
    jses =  getJsName(values[0]);
    String [] params = JsUtils.getParams(values[0]);
    if(params == null) return;
    jsParams = new URIElement[params.length];
    for(int i = 0; i < params.length; i++) {
      jsParams[i] = URIParser.parse(params[i]);
    }
  }

  public boolean match(String value) {
    if(jses.size() < 1) return true;
    if(value == null) return false;
    List<String> funcJses = getJsName(value);
    if(funcJses.size() != jses.size()) return false;
    for(int i = 0; i < jses.size(); i++) {
      if(!jses.get(i).equalsIgnoreCase(funcJses.get(i))) return false;
    }
    if(jsParams == null) return true;
    
//    System.out.println(jsName+ " : "+ js);
    String [] params = JsUtils.getParams(value);
    if(params == null || params.length != jsParams.length) return false;
    for(int i = 0; i < params.length; i++) {
      if(!jsParams[i].match(params[i])) return false;
    }
    return true;
  }
  
  protected List<String> getJsName(String value) {
    List<String> list = new ArrayList<String>();
//    System.out.println(value.charAt(value.indexOf(js) + js.length() + 1));
    int index = 0;
    String js  = getJsName(value, index, '(');
    while(js != null) {
      list.add(js);
      index += value.indexOf(js) + js.length() + 1;
      js  = getJsName(value, index, '(');
    }
    
    if(list.size() < 1) {
      index = 0; 
      js  = getJsName(value, index, '=');
      while(js != null) {
        list.add(js);
        index += value.indexOf(js) + js.length() + 1;
        js  = getJsName(value, index, '=');
      }
    }
    return list;
  }
  
  protected String getJsName(String value, int index, char ch) {
    int start = index;
    while(index < value.length()) {
      char c = value.charAt(index);
//      if(c == '{') return null;
      if(c == ch) return value.substring(start, index);
      index++;
    }
    return null;
  }

  public short getType() { return type; }

  public void setType(short type) { this.type = type; }
  
}
