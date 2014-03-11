/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.                *
 *                                                                         *
 **************************************************************************/
package org.vietspider.bean;

import java.io.Serializable;

import org.vietspider.common.Application;
import org.vietspider.serialize.NodeMap;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Feb 8, 2007
 */
@NodeMap("content")
public class Content implements Bean<Content>, Serializable {
  
  private final static long serialVersionUID = -1923342975486063230l;
  
  private final static long MAX_SIZE = 5000000;
  
  
  public Content() {
  }
  
  public Content(String metaId, String date, String content_) throws BigSizeException {
    if(content_.length() > MAX_SIZE) throw new BigSizeException(content_.length()*8);
    this.meta = metaId;
    this.date = date;
    this.content = content_;
  }

  @NodeMap(value = "meta")
  private String meta;
  public String getMeta(){ return meta; }
  public void setMeta(String id){ this.meta  = id; }
  
  @NodeMap(value = "text_content" , cdata= true)
  private String content;
  public String getContent(){    return content;  }  
  public void setContent(String value) throws BigSizeException {
    if(value.length() > MAX_SIZE) throw new BigSizeException(value.length()*8);
    content = value;  
  }
  
  @NodeMap(value = "status")
  private int status;
  public int getStatus(){    return status;  }  
  public void setStatus(int value){ status = value;  }
  
  private String  date;
  public String getDate(){ return date; }
  public void setDate(String value){ date = value; }
  
  public void setField(RSField  field, Object value) throws Exception {
    switch (field) {
    case META_ID:
      meta = value.toString();
      return;
    case META:
      meta = value.toString();
      return;
    case CONTENT:
      if(value instanceof byte[]){
        value = new String((byte[])value, Application.CHARSET);
      }
      content = (String) value;
      return;  
    case DATE:
      date = value.toString();
      return;
    case STATUS:
      if(value instanceof Integer){
        status = (Integer) value; 
      }else{
        status = Integer.valueOf(value.toString());
      }
      return;
    default:
      return;
    }
  }
  
  public RSField getField() { return RSField.CONTENT; }
  
  @SuppressWarnings("serial")
  public static class BigSizeException extends Exception {
    
    private String message;
    
    public BigSizeException(long size) {
      message = "Content is too big ("+size+" bytes). Max size is " + MAX_SIZE;
    }

    @Override
    public String getMessage() { return message; }
    
    
  }
  
}
