/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.user;

import org.vietspider.serialize.NodeMap;


/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Feb 8, 2007
 */
@NodeMap("filter")
public class Filter {
  
  public final static int DOMAIN = 0;
  public final static int CONTENT = 1;
  
  public Filter(){}
  
  public Filter(String name, String filter, int type){
    this.name = name;
    this.filter = filter;
    this.type = type;
  }

  @NodeMap("name")
  private String name;
  public String getName(){ return name; }  
  public void setName(String value) { name=value;  }
  
  @NodeMap("filter")
  private String filter;
  public String getFilter() { return filter; }  
  public void setFilter(String value) { filter = value;  }
  
  @NodeMap("type")
  private int type = CONTENT;
  public int getType(){ return type; }  
  public void setType(int value) { type = value;  }
}
