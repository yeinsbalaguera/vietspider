/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.bean;

import java.io.Serializable;

import org.vietspider.model.Group;
import org.vietspider.serialize.NodeMap;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Feb 8, 2007
 */
@NodeMap("Domain")
public class Domain implements Bean<Domain> , Serializable {
  
  private final static long serialVersionUID = -1003342175486063235l;
  
  public Domain(){}
  
  public Domain(String date, String group, String category, String name){
    this.date = date;
    this.group = group;
    this.category = category;
    this.name = name;
    
    buildId();
  }
  
  public void buildId() {
    StringBuilder builder  = new StringBuilder(date);
    if(group != null) builder.append('.').append(group);
    builder.append('.').append(category).append('.').append(name);
    id = String.valueOf(builder.toString().hashCode());
  }
  
  public String toString() {
    StringBuilder builder  = new StringBuilder(date);
    if(group != null) builder.append('.').append(group);
    if(category != null) builder.append('.').append(category);
    if(name != null) builder.append('.').append(name);
    return builder.toString(); 
  }
  
  @NodeMap("id")
  private String id;
  public String getId(){ return id; }
  public void setId(String value){ this.id  = value; }
  
  @NodeMap("data-type")
  private String group = Group.ARTICLE;
  public String getGroup(){ return group; }
  public void setGroup(String value){ this.group  = value; }
  
  @NodeMap("date")
  private String  date;
  public String getDate(){ return date; }
  public void setDate(String value){ date = value; }
  
  @NodeMap("category")
  private String category;
  public String getCategory(){    return category;  }  
  public void setCategory(String value){    category = value;  }
  
  @NodeMap("name")
  private String name;
  public String getName(){    return name;  }  
  public void setName(String value){    name=value;  }
  
  public void setField(RSField  field, Object value) throws Exception {
    switch (field) {
    case ID:
      id = value.toString();
      return;
    case DATE:
      date = value.toString();
      return;  
    case CATEGORY:
      category = (String) value;
      return;
    case NAME:
      name = (String) value;
      return;
    default:
      return;
    }
  }
  
  public RSField getField() { return RSField.DOMAIN; }
}
