/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.vietspider.serialize.GetterMap;
import org.vietspider.serialize.NodeMap;
import org.vietspider.serialize.NodesMap;
import org.vietspider.serialize.PropertiesMap;
import org.vietspider.serialize.SetterMap;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 26, 2010  
 */
@NodeMap("article-collection")
public class ArticleCollection implements DataCollection<Article>, Serializable {
  
  @PropertiesMap(value = "properties", item = "item")
  private Properties properties = new Properties();
  
  public ArticleCollection() {
  }
  
  @NodesMap(value = "list", item = "item")
  private List<Article>  list = new ArrayList<Article>();

  @GetterMap("list")
  public List<Article> get() { return list; }

  @SetterMap("list")
  public void set(List<Article> list) { this.list = list; }

 
  @GetterMap("properties")
  public Properties getProperties() { return properties; }

  @SetterMap("properties")
  public void setProperties(Properties properties) {
    this.properties = properties;
  }
  

}
