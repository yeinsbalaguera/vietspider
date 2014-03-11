/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.net.server;

import java.util.List;

import org.vietspider.bean.Image;
import org.vietspider.serialize.NodeMap;
import org.vietspider.serialize.NodesMap;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 26, 2007  
 */
@NodeMap("images")
public class Images {
  
  @NodesMap(value="list", item="item")
  private List<Image> list;

  public List<Image> getList() { return list; }

  public void setList(List<Image> list) { this.list = list; }

}
