/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.net.server;

import java.util.List;

import org.vietspider.bean.Meta;
import org.vietspider.serialize.NodeMap;
import org.vietspider.serialize.NodesMap;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 25, 2007  
 */
@NodeMap("Metas")
public class Metas {
  
  @NodesMap(value = "list", item="item")
  private List<Meta> list ;

  public List<Meta> getList() { return list; }
  public void setList(List<Meta> list) { this.list = list; }
}
