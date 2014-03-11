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
//a:[van hoa:giao duc;y te] [thay ma:quan trong]
public class NWord {

  private String key;
  
  private NWordValue [] values;

  public String getKey() { return key; }
  public void setKey(String key) { this.key = key.toLowerCase(); }

  public NWordValue[] getValues() { return values; }
  public void setValues(NWordValue[] values) { this.values = values; }
  
  public String toText() {
    StringBuilder builder = new StringBuilder();
    builder.append(key).append(':');
    for(int i = 0; i < values.length; i++) {
      builder.append(values[i].toText());
    }
    return builder.toString();
  }
  
  public void fromText(String text) throws InvalidFormatException {
    int start = 0;
    int end = text.indexOf(':', start);
    if(end < 0) throw new InvalidFormatException(text);
    key =  text.substring(start, end).trim();
    if(key.length() < 1) throw new InvalidFormatException(text);
    key = key.toLowerCase();
    
    List<NWordValue> list = new ArrayList<NWordValue>();
    start = text.indexOf('[', end);
    end = text.indexOf(']', start);
    while(start > 0 && end > 0) {
      String nvalue = text.substring(start, end+1).trim();
      NWordValue nWordValue = new NWordValue();
      nWordValue.fromText(nvalue);
      list.add(nWordValue);
      
      start = text.indexOf('[', end);
      end = text.indexOf(']', start);
    }
    
    values = list.toArray(new NWordValue[list.size()]);
  }
  
  public void merge(NWord word) {
    List<NWordValue> list = new ArrayList<NWordValue>();
    Collections.addAll(list, values);
    NWordValue [] newValues = word.getValues();
    for(int i = 0; i < newValues.length; i++) {
      NWordValue value = newValues[i];
      boolean add = true;
      for(int k = 0;  k < list.size(); k++) {
        if(CharsUtil.equals(list.get(k).getValue(), value.getValue())) {
          list.get(k).merge(value);
          add = false;
          break;
        }
      }
      if(add) list.add(value);
    }
    values = list.toArray(new NWordValue[list.size()]);
  }
  
}
