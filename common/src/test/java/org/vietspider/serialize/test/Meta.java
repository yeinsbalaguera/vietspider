/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.serialize.test;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.vietspider.serialize.NodeMap;
import org.vietspider.serialize.NodesMap;
import org.vietspider.serialize.PropertiesMap;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 6, 2011  
 */
@NodeMap("meta")
public class Meta implements Serializable {
  
  @NodesMap(value = "list", item  = "item")
  private List<String> item = null;
  
  @PropertiesMap(value = "properties", item = "property")
  private Map<String, String> properties = new HashMap<String, String>();
  
  @NodesMap(value = "cdata-attrs", item  = "bean")
  private CDataAttrBean [] cbeans;
  
  public List<String> getItem() {
    return item;
  }

  public void setItem(List<String> item) {
    this.item = item;
  }
  
  public void putProperty(String key, String value) {
//    if("text".equals(key.trim())) new Exception().printStackTrace();
    if(properties == null) properties = new HashMap<String, String>();
    properties.put(key, value);
  }
  
  public Iterator<String> iteratorPropertyKey() { 
    if(properties == null) properties = new HashMap<String, String>();
    return properties.keySet().iterator();
  }
  
  public String getPropertyValue(String key) {
    if(properties == null) return null;
    return properties.get(key);
  }
  public void removeProperty(String key) {
    if(properties == null) return;
    properties.remove(key);
  }
  public boolean hasProperty() {
    if(properties == null) return false;
    return !properties.isEmpty();
  }
  public Map<String, String> getProperties() { return properties; }
  public void setProperties(Map<String, String> values) { properties = values; }
  
  public CDataAttrBean[] getCbeans() { return cbeans; }
  public void setCbeans(CDataAttrBean[] cbeans) { this.cbeans = cbeans; }
  
}
