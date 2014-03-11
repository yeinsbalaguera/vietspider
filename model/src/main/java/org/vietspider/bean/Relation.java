/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.bean;

import java.io.Serializable;

import org.vietspider.serialize.NodeMap;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Feb 8, 2007
 */
@NodeMap(value = "relation")
public class Relation implements Bean<Relation>, Serializable {
  
  private final static long serialVersionUID = -1723342905186063235l;
  
  @NodeMap(value = "meta")
  private String meta;
  public String getMeta(){ return meta; }
  public void setMeta(String value){ this.meta  = value; }
  
  @NodeMap(value = "relation")
  private String relation; //similarity
  public String getRelation(){ return relation; }
  public void setRelation(String relation_){ this.relation = relation_; }
  
  @NodeMap(value = "percent")
  private int percent;
  public int getPercent() { return percent; }
  public void setPercent(int percent) {  this.percent = percent; } 
  
  public void setField(RSField  field, Object value) throws Exception {
    switch (field) {   
    case META:
    case META_ID:
      meta = value.toString();
      return;
    case RELATION:
    case RELATION_ID:
      relation = value.toString();
      return;     
    case PERCENT:
      if(value instanceof Integer){
        percent = (Integer) value; 
      }else{
        percent = Integer.valueOf(value.toString());
      }
      return;
    default:
      return;
    }
  }
  
  public RSField getField() { return RSField.RELATION; }
  
}
