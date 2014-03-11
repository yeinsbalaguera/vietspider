/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.bean;

import java.io.Serializable;

import org.vietspider.locale.vn.VietnameseConverter;
import org.vietspider.serialize.NodeMap;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 5, 2007  
 */
@NodeMap("meta_relation")
public class MetaRelation implements Bean<MetaRelation>, Serializable {
  
  private final static long serialVersionUID = -23042975486063235l;

  public MetaRelation(){}

  public MetaRelation newInstance(){ return new MetaRelation(); }

  @NodeMap("id")
  private String id;
  @NodeMap("title")
  private String title;
  @NodeMap("desc")
  private String des;
  @NodeMap("image")
  private String image;
  @NodeMap("percent")
  private int percent;
  @NodeMap("name")
  private String name;
  
  @NodeMap("source")
  private String source;

  @NodeMap("date")
  private String date;
  @NodeMap("time")
  private String time;

  public String getId() { return id; }
  public void setId(String id) { this.id = id; }

  public int getPercent() { return percent; }
  public void setPercent(int percent) { this.percent = percent; }

  public String getName() { return name; }
  public void setName(String source) { this.name= source; }

  public String getTime() { return time; }
  public void setTime(String time) { this.time = time; }

  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }
  
  private transient String alias;
  public String getAlias(){
    if(alias == null) {
      alias = title != null ? VietnameseConverter.toAlias(title) : "no_alias";
    }
    return alias; 
  }

  public String getDate() { return date; }
  public void setDate(String date) { this.date = date; }

  public String getDes() { return des; }
  public void setDes(String des) { this.des = des; } 

  public String getImage() { return image; }
  public void setImage(String img) { this.image = img; }
  
  public String getSource() { return source; }
  public void setSource(String source) { this.source = source; }

  public void setField(RSField  field, Object value) throws Exception {
    switch (field) {
    case ID:
      id = value.toString();
      return;
    case TITLE:
      title = (String) value;
      return;  
    case DES:
      des = (String) value;
      return;
    case IMAGE:
      image = (String) value;
      return;  
    case PERCENT:
      if(value instanceof Integer){
        percent = (Integer) value; 
      }else{
        percent = Integer.valueOf(value.toString());
      }
      return;
    case NAME:
      name = (String) value;
      return;
    case DATE:
      date = value.toString();
      return;
    case TIME:
      time = value.toString();
      return;
    default:
      return;
    }
  }

  public RSField getField() { return RSField.META_RELATION; }

}
