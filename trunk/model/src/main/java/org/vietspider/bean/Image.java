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
@NodeMap("image")
public class Image implements Bean<Image>, Serializable {
  
  private final static long serialVersionUID = -6063236l;
  
  public Image() {}
  
  public Image(String metaId, int counter){
    StringBuilder builder  = new StringBuilder(metaId).append('.').append(counter);
    id  = builder.toString();
    this.meta = metaId;
  }

  @NodeMap("id")
  private String id;
  public String getId(){ return id; }
  public void setId(String value){ this.id  = value; }
  
  @NodeMap("meta")
  private String meta;
  public String getMeta(){ return meta; }
  public void setMeta(String value){ this.meta  = value; }
  
  @NodeMap("type")
  private String type;
  public String getType() {    return type;  }  
  public void setType(String value){    type = value;  }
    
  @NodeMap("name")
  private String name;
  public String getName(){    return name;  }  
  public void setName(String value){    name=value;  }
  
  private byte[]  image;
  public byte[] getImage(){ return image; }
  public void setImage(byte[] value){  image = value; }
  
  public void setField(RSField  field, Object value) throws Exception {
    switch (field) {
    case ID:
    case META_ID:
      id = String.valueOf(value);
      return;
    case META:
      meta = value.toString();
      return;  
    case TYPE:
    case CONTENT_TYPE:
      type = (String) value;
      return;
    case NAME:
      name = (String) value;
      return;
    case IMAGE:
      image = (byte[]) value;
      return;
    default:
      return;
    }
  }
  
  public RSField getField() { return RSField.IMAGE; }
}
