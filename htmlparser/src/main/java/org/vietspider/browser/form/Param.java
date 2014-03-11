/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.browser.form;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.vietspider.serialize.NodeMap;
import org.vietspider.serialize.NodesMap;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 21, 2008  
 */
@NodeMap("param")
@SuppressWarnings("serial")
public class Param implements Serializable {
  
  public final static int FORM = 0;
  public final static int SCRIPT = 1;
  
  
  @NodeMap("name")
  private String name;
  
  @NodeMap("type")
  private String type;
  
  @NodeMap("link_type")
  private short linkType = 0;
  
  @NodeMap("from")
  private int from = FORM;
  
  @NodesMap(value = "values", item = "item")
  private Set<String> values = new HashSet<String>();
  
  public Param() {    
  }

  public Param(String name, int from_, String...value) {
    this.from = from_;
    this.name = name;
    Collections.addAll(values, value);
  }

  public String getName() { return name; }
  public void setName(String name) { this.name = name;  }

  public Set<String> getValues() { return values; }
  public void setValues(int from_, Set<String> values) {
    this.from = from_;
    this.values = values; 
  }
  public void setValues(Set<String> values) {
    this.values = values; 
  }
  public void addValue(int from_, String...items) {
    this.from = from_;
    if(values == null) values = new HashSet<String>();
    for(int i = 0; i < items.length; i++) {
      this.values.add(items[i]);
    }
  }
  public String getValue() {
    if(values.size() > 0) return values.iterator().next();
    return "";
  }

  public String getType() { return type; }
  public void setType(String type) { this.type = type; }
  
  public int getFrom() { return from;   }
  public void setFrom(int from) { this.from = from; }
  
  public short getLinkType() { return linkType; }
  public void setLinkType(short linkType) { this.linkType = linkType; }
  
  public String toString() {
    StringBuilder builder = new StringBuilder();
    Iterator<String> iterator = values.iterator();
    while(iterator.hasNext()) {
      if(builder.length() > 0) builder.append('&');
      builder.append(name).append('=').append(iterator.next());
    }
    return builder.toString();
  }
  
  public Param clone() {
    Param param = new Param(name, from);
    param.setType(type);
    
    Set<String> newValues = new HashSet<String>();
    Iterator<String> iterator = values.iterator();
    while(iterator.hasNext()) {
      newValues.add(iterator.next());
    }
    
    param.setValues(from, newValues);
    
    return param;
  }

}
