/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.handler;

import org.vietspider.serialize.NodeMap;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 30, 2009  
 */
@NodeMap("resource")
public class XMLResource {
  
  @NodeMap("id")
  private String id;
  
  @NodeMap(value = "link", cdata = true)
  private String link;
  
  @NodeMap("name")
  private String name;
  
  public XMLResource() {
  }
  
  public XMLResource(String id, String name, String link) {
    this.id = id;
    this.link = link;
    this.name = name;
  }
  
  public String getId() { return id; }
  public void setId(String id) { this.id = id; }

  public String getLink() { return link; }
  public void setLink(String link) { this.link = link; }

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

}
