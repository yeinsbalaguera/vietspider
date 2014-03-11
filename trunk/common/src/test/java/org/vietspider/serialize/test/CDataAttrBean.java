/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.serialize.test;

import java.io.Serializable;

import org.vietspider.serialize.NodeMap;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 8, 2011  
 */
@NodeMap("cdata-attr")
public class CDataAttrBean implements Serializable {
  
  @NodeMap(value = "number", attribute = true)
  private short number = 0;
  
  @NodeMap(value = "number2", attribute = true)
  private short number2 = 13;
  
  @NodeMap(value = "page", cdata = true)
  private String page;
  
  public CDataAttrBean() {
    
  }
  
  public CDataAttrBean(short num, short num2, String _page) {
    this.number = num;
    this.number2 = num;
    this.page = _page;
  }

  public short getNumber() {
    return number;
  }

  public void setNumber(short number) {
    this.number = number;
  }
  
  public short getNumber2() {
    return number2;
  }

  public void setNumber2(short number2) {
    this.number2 = number2;
  }

  public String getPage() {
    return page;
  }

  public void setPage(String page) {
    this.page = page;
  }

  
}
