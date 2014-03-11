/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.bean;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.vietspider.common.text.CalendarUtils;
import org.vietspider.locale.vn.VietnameseConverter;
import org.vietspider.serialize.GetterMap;
import org.vietspider.serialize.NodeMap;
import org.vietspider.serialize.PropertiesMap;
import org.vietspider.serialize.SetterMap;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Feb 8, 2007
 */
@NodeMap(value = "meta")
public class Meta implements Bean<Meta>, Serializable {

  private final static long serialVersionUID = -1723342975486063235l;
  
  @NodeMap(value = "id")
  private String id;
  public String getId(){ return id; }
  public void setId(String value){ this.id  = value; }

  @NodeMap(value = "title", cdata = true)
  private String title = "";
  public String getTitle(){ return title; }
  public void setTitle(String value) { title = value; }

  @NodeMap(value = "des", cdata = true)
  private String  des = "";
  @GetterMap("des")
  public String getDesc(){
    return ( des == null || des.trim().equals("null")) ? "" :des;
  }
  @SetterMap("des")
  public void setDesc(String value){ des = value; }

  @NodeMap(value = "image")
  private String  image;
  public String getImage() { return image; }
  public void setImage(String value){  image = value; }

  @NodeMap(value = "time")
  private String  time;
  public String getTime(){ return time; }
  public void setTime(String value){ time = value; }
  
  private transient String alias;
  public String getAlias(){
    if(alias == null) {
      alias = title != null ? VietnameseConverter.toAlias(title) : "no_alias";
    }
    return alias; 
  }

  @PropertiesMap(value = "properties", item = "property")
  private Map<String, String> properties = new HashMap<String, String>();
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

  public Map<String, String> getProperties() {
    return properties;
  }
  public void setProperties(Map<String, String> properties) {
    this.properties = properties;
  }
  public boolean hasNlp() {
    if(properties == null) return false;
    Iterator<String> iterator = properties.keySet().iterator();
    while(iterator.hasNext()) {
      String key = iterator.next();
      if(key.startsWith("nlp.")) return true;
    }
    return false;
  }

  //  public Map<String, Object> getProperties() {
  //    if(properties == null) properties = new HashMap<String, Object>(); 
  //    return properties; 
  //  }

  private Calendar calendar;
  public Calendar getCalendar(){
    if(calendar != null) return calendar;
    calendar = Calendar.getInstance();
    if(time != null) {
      try {
        calendar.setTime(CalendarUtils.getDateTimeFormat().parse(time));
      } catch (Exception e) {
      }
    } 
    return calendar; 
  }
  public void setCalendar(Calendar value){ calendar  = value; }

  @NodeMap(value = "source_time")
  private String  sourceTime;
  public String getSourceTime(){ return sourceTime; }
  public void setSourceTime(String value){ sourceTime = value; }

  @NodeMap(value = "domain")
  private String domain;
  public String getDomain(){    return domain;  }  
  public void setDomain(String value){
    //  	System.out.println(" domain:"+value);
    domain = value;  }

  @NodeMap(value = "url")
  private String url;
  @GetterMap("url")
  public String getSource(){ return url; }
  @SetterMap("url")
  public void setSource(String value){ url = value; }

  public void setField(RSField  field, Object value) throws Exception {
    switch (field) {
    case ID:
      id = value.toString();
      return;
    case TITLE:
      title = (String) value;
      return;  
    case DES:
      des = (String) value;
      return;
    case IMAGE:
      image = (String) value;
      return;
    case DOMAIN_ID:
      domain = value.toString();
      return;
    case URL:
      url = (String) value;
      return;
    case TIME:
      time = value.toString();
      return;
    case SOURCE_TIME:
      if(value == null) return; 
      sourceTime = value.toString();
      return;  
    default:
      return;
    }
  }

  public RSField getField() { return RSField.META; }

}
