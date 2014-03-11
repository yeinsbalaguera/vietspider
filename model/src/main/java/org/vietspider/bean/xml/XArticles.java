/***************************************************************************
 * Copyright 2003-2012 by VietSpider - All rights reserved.                *    
 **************************************************************************/
package org.vietspider.bean.xml;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.vietspider.bean.DataCollection;
import org.vietspider.serialize.GetterMap;
import org.vietspider.serialize.NodeMap;
import org.vietspider.serialize.NodesMap;
import org.vietspider.serialize.PropertiesMap;
import org.vietspider.serialize.SetterMap;

/**
 *  Author : Nhu Dinh Thuan
 *  Email:nhudinhthuan@yahoo.com
 *  Website: vietspider.org       
 * Jan 15, 2012
 */
@NodeMap("xarticle")
public class XArticles implements DataCollection<XArticle>, Serializable {
  
  @PropertiesMap(value = "properties", item = "item")
  private Properties properties = new Properties();
  
  public XArticles() {
  }
  
  @NodesMap(value = "list", item = "item")
  private List<XArticle>  list = new ArrayList<XArticle>();

  @GetterMap("list")
  public List<XArticle> get() { return list; }

  @SetterMap("list")
  public void set(List<XArticle> list) { this.list = list; }

 
  @GetterMap("properties")
  public Properties getProperties() { return properties; }

  @SetterMap("properties")
  public void setProperties(Properties properties) {
    this.properties = properties;
  }
  

}
