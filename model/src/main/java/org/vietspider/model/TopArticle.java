/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.model;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.serialize.NodeMap;
import org.vietspider.serialize.NodesMap;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 23, 2011  
 */
@NodeMap("top-article")
public class TopArticle {

  @NodeMap("id")
  private String id;
  @NodeMap("title")
  private String title;
  @NodesMap(value = "rels", item = "item")
  private List<String> rels = new ArrayList<String>();
  
  public TopArticle() {
    
  }
  
  
  
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }
  public List<String> getRels() {
    return rels;
  }
  public void setRels(List<String> rels) {
    this.rels = rels;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }
  
  
  
  
}
