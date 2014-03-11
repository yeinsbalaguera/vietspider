/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.model;

import java.util.Properties;

import org.vietspider.serialize.GetterMap;
import org.vietspider.serialize.NodeMap;
import org.vietspider.serialize.NodesMap;
import org.vietspider.serialize.PropertiesMap;
import org.vietspider.serialize.SetterMap;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 7, 2007  
 */
@NodeMap("region")
public class DetachPathBak {
  
  @NodeMap(value = "name" , attribute = true)
  private String name;
  
  @NodesMap(value = "paths", item = "item", cdata=true)
  private String [] paths ;
  
  @PropertiesMap(value = "properties", item = "property")
  public Properties properties;
  
  public DetachPathBak() {}
  
  public DetachPathBak(String name, String [] paths) {
    this.name = name;
    this.paths = paths;
  }

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public String[] getPaths() { return paths; }
  public void setPaths(String[] paths) { this.paths = paths; }
  
  @GetterMap("properties")
  public Properties getProperties() {
    if(properties == null) properties = new Properties();
    return properties; 
  }
  @SetterMap("properties")
  public void setProperties(Properties sourceProperties) {
    this.properties = sourceProperties;
  }
  
}
