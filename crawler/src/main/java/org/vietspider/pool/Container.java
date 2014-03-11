/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.pool;

import java.util.Map;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 23, 2008  
 */
public abstract class Container<C> {
  
  protected volatile int id;
  
  protected volatile C value;
  
  protected Map<String, Object> resources;
  
  public Container(int id) {
    this.id = id;
  }
  
  public int getId() { return id; }
  
//***************************************** resources **********************************************
  
  @SuppressWarnings("unchecked")
  public <R> R getResource(Class<R> clazz) { return (R)resources.get(clazz.getName()); }
  
  @SuppressWarnings("unchecked")
  public <R> R getResource(String key) { return (R)resources.get(key); }
  
  public void putResource(Object resource) {
    resources.put(resource.getClass().getName(), resource);
  }
  
  public void removeResource(String key) { resources.remove(key); }
  
  public void removeResource(Class<?> clazz) { resources.remove(clazz.getName()); }
  
  public void putResource(String key, Object resource) { resources.put(key, resource); }
  
  public void clearResources() { resources.clear(); }
  
  public C getValue() { return value; }

}
