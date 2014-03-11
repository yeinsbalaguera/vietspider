/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.vietspider.serialize.NodeMap;
import org.vietspider.serialize.NodesMap;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 20, 2009  
 */
@NodeMap("nld-data")
public class NLPData implements Serializable {
  
  private final static long serialVersionUID = -1239863235l;
  
  public final static short NORMAL_TEXT = -1;
  public final static short EMAIL = 0;
  public final static short PHONE = 1;
  public final static short TELEPHONE = 10;
  public final static short MOBILE = 11;
  public final static short ADDRESS = 2;
  public final static short CITY = 20;
  public final static short AREA = 3;
  public final static short AREA_SHORT = 30;
  public final static short PRICE = 4;
  public final static short PRICE_TOTAL = 41;
  public final static short PRICE_UNIT_M2 = 42;
  public final static short PRICE_MONTH = 43;
  public final static short ACTION_OBJECT = 5;
  public final static short ACTION = 51;
  public final static short OBJECT = 52;
  public final static short OWNER = 6;
  
  public final static short ACTION_SELL = 1;
  public final static short ACTION_FOR_RENT = 2;
  public final static short ACTION_RENT = 3;
  public final static short ACTION_ASSIGNMENT = 4;
  public final static short ACTION_BUY = 5;
  public final static short ACTION_TRAINING = 6;
  public final static short ACTION_CONSULTING = 7;
  public final static short ACTION_LOAN = 8;
  public final static short ACTION_INTRO = 9;
  
  public final static short OBJECT_HOUSE = 1;
  public final static short OBJECT_LAND = 2;
  public final static short OBJECT_OFFICE = 3;
  public final static short OBJECT_APARTMENT = 4;
  public final static short OBJECT_VILLA = 5;
  public final static short OBJECT_ROOM = 6;
  public final static short OBJECT_PROJECT = 7;
  public final static short OBJECT_ARCHITECTURE = 9;
  public final static short OBJECT_BUSINESS = 10;
  public final static short OBJECT_OTHER = 0;
  

  @NodeMap("type")
  private short type;
  
  @NodesMap(value = "values", item = "value")
  private List<String> values = new ArrayList<String>();
  
  public NLPData() {
    
  }
  
  public NLPData(short type, String[] _values) {
    this.type = type;
    Collections.addAll(values, _values);
//    System.out.println(type + "= " + values);
  }
  
  public NLPData(short type, Collection<?> _values) {
    this.type = type;
    for(Object ele : _values) {
      values.add(ele.toString());
    }
  }

  public short getType() { return type;  }
  public void setType(short type) { this.type = type; }

  public List<String> getValues() { return values; }
  public void setValues(List<String> values) { this.values = values; }
  
  public void addValue(String value) { values.add(value); }
  
  public final static String action_object(String value) {
    int index = value.indexOf(',');
    StringBuilder builder = new StringBuilder();
    String action = action(Short.valueOf(value.substring(0, index)));
    String object = object(Short.valueOf(value.substring(index+1)));
    if(action.length() > 0) builder.append(action);
    if(object.length() > 0) {
      builder/*.append(',')*/.append(' ');
      builder.append(object);
    }
    return builder.toString();
  }
  
  public final static String action(short value) {
    switch (value) {
    case ACTION_SELL:
      return "Bán";
    case ACTION_FOR_RENT:
      return "Cho thuê";
    case ACTION_RENT:
      return "Cần thuê";
    case ACTION_ASSIGNMENT:
      return "Nhượng";
    case ACTION_BUY:
      return "Mua";
    case ACTION_TRAINING:
      return "Đào tạo";
    case ACTION_CONSULTING:
      return "Tư vấn";
    case ACTION_LOAN:
      return "Cho vay";
    case ACTION_INTRO:
      return "Giới thiệu";
    default:
      return "";
    } 
  }
  
  public final static String object(short value) {
    switch (value) {
    case OBJECT_HOUSE:
      return "nhà";
    case OBJECT_LAND:
      return "đất";
    case OBJECT_APARTMENT:
      return "căn hộ";
    case OBJECT_OFFICE:
      return "văn phòng";
    case OBJECT_ROOM:
      return "phòng";
    case OBJECT_VILLA:
      return "biệt thự";
    case OBJECT_PROJECT:
      return "dự án";
    case OBJECT_ARCHITECTURE:
      return "thiết kế";
    case OBJECT_BUSINESS:
      return "bất động sản để kinh doanh";
    case OBJECT_OTHER:
      return "bất động sản";
    default:
      return "";
    } 
  }
  
}
