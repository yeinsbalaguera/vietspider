/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.bean;

import java.io.Serializable;
import java.util.Map;

import org.vietspider.serialize.NodeMap;
import org.vietspider.serialize.PropertiesMap;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 20, 2009  
 */
@NodeMap("nld-record-item")
public class NLPRecordItem implements Serializable {
  
  private final static long serialVersionUID = -1239863235l;

  @NodeMap("type")
  private String type;
  @NodeMap("name")
  private String name;
  
  @PropertiesMap(value = "values", item = "value")
  private Map<String, String> values;

  public String getType() { return type;  }
  public void setType(String type) { this.type = type; }

  public String getName() { return name; }
  public void setName(String name) { this.name = name;  }

  public Map<String, String> getValues() { return values; }
  public void setValues(Map<String, String> values) { this.values = values; }
  
  public String getData(String _type, String item) {
    if(!this.type.equals(_type)) return null;
    return values.get(item);
  }
  
  public boolean setData(String _type, String item, String data) {
    if(!this.type.equals(_type)) return false;
    values.put(item, data);
    return true;
  }
  
}
