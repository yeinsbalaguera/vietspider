/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db.dict.non;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.vietspider.chars.CharsUtil;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 15, 2009  
 */
public class NWordValue {  
  
  public final static short REPLACE = 0;
  public final static short APPEND = 1;
  
  private String value;
  private String [] refs = new String[0];
  private short type  = REPLACE;
  
  public NWordValue() {
  }
  
  public NWordValue(String value) {
    this.value = value;
  }
  
  public String getValue() { return value; }
  public void setValue(String value) { this.value = value; }
  
  public String[] getRefs() { return refs;  }
  public void setRefs(String[] refs) { this.refs = refs; }
  
  public short getType() { return type; }
  public void setType(short type) { this.type = type; }
  
  //[van hoa:giao duc;y te]
  public String toText() {
    StringBuilder builder = new StringBuilder();
    builder.append('[').append(value).append(':').append(type).append(':');
    for(int i = 0; i < refs.length; i++) {
      if(i > 0) builder.append(';');
      builder.append(refs[i]);
    }
    builder.append(']');
    return builder.toString();
  }
  
  public void fromText(String text) throws InvalidFormatException {
    int start = text.indexOf('[', 0);
    if(start < 0) throw new InvalidFormatException(text);
    int end = text.indexOf(':', start);
    if(end < 0) throw new InvalidFormatException(text);
    value =  text.substring(start + 1, end).trim();
    if(value.length() < 1) throw new InvalidFormatException(text);
    
    start = end;
    end = text.indexOf(':', start+1);
    try {
      type = Short.parseShort(text.substring(start + 1, end).trim());
    } catch (Exception e) {
      throw new InvalidFormatException(text);
    }
    
    List<String> list = new ArrayList<String>();
    while(true) {
      start = end + 1;    
      end = text.indexOf(';', start);
      if(end < 0) break;
      String ref = text.substring(start, end).trim();
      if(ref.length() > 0) list.add(ref);
    }
    
    if(start < text.length()) {
      end = text.indexOf(']', start);
      if(end < 0) throw new InvalidFormatException(text);
      String ref = text.substring(start, end).trim();
      if(ref.length() > 0) list.add(ref);
    }
    refs = list.toArray(new String[list.size()]);
  }
  
  public void merge(NWordValue nWordValue) {
    type = nWordValue.getType();
    List<String> list = new ArrayList<String>();
    Collections.addAll(list, refs);
    String [] newRefs = nWordValue.getRefs();
    for(int i = 0; i < newRefs.length; i++) {
      String ref = newRefs[i];
      boolean add = true;
      for(int k = 0;  k < list.size(); k++) {
        if(CharsUtil.equals(list.get(k), ref)) {
          add = false;
          break;
        }
      }
      if(add) list.add(ref);
    }
    refs = list.toArray(new String[list.size()]);
  }
  
  
}
