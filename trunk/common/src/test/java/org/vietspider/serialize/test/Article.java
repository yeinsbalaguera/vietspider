/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.serialize.test;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.vietspider.serialize.NodeMap;
import org.vietspider.serialize.NodesMap;
import org.vietspider.serialize.PropertiesMap;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 6, 2011  
 */
@NodeMap("article")
public class Article implements Serializable {
  
  @NodeMap("meta")
  private Meta meta;
  
  @NodeMap("title")
  private String title;
  
  @NodeMap("content")
  private String content;
  
  @NodeMap("data")
  private PrimitiveData data;
  
  @PropertiesMap(value = "map-object", item = "item")
  private Map<String, CDataAttrBean> map = new Hashtable<String, CDataAttrBean>();
  
  @PropertiesMap(value = "properties", item = "prop")
  private Properties properties = new Properties();
  
  @PropertiesMap(value = "umap", item = "item")
  private Map umap = new HashMap();
  
  @NodesMap(value = "linked", item = "lk")
  private List linked = new LinkedList();
//  private LinkedList linked = new LinkedList();
  
  @NodesMap(value = "ranges", item = "element")
  private Object [] ranges;
  
  @NodesMap(value = "tree", item = "item")
  private Set tree = new TreeSet();
  
  @NodesMap(value = "folder", item = "item")
  private Set<String> folder = new HashSet<String>();
  
  public String getTitle() {
    return title;
  }
  public void setTitle(String title) {
    this.title = title;
  }
  public String getContent() {
    return content;
  }
  public void setContent(String content) {
    this.content = content;
  }
  public Meta getMeta() {
    return meta;
  }
  public void setMeta(Meta meta) {
    this.meta = meta;
  }
  
  public PrimitiveData getData() {
    return data;
  }
  public void setData(PrimitiveData data) {
    this.data = data;
  }
  
  public Map<String, CDataAttrBean> getMap() {
    return map;
  }
  public void setMap(Map<String, CDataAttrBean> value) {
    this.map = value;
  }
  
  public Properties getProperties() {
    return properties;
  }

  public void setProperties(Properties properties) {
    this.properties = properties;
  }
  
  public Map getUmap() {
    return umap;
  }
  public void setUmap(Map umap) {
    this.umap = umap;
  }
  
  public List getLinked() {
    return linked;
  }
  public void setLinked(List linked) {
    this.linked = linked;
  }
  
  public Object[] getRanges() {
    return ranges;
  }
  public void setRanges(Object[] ranges) {
    this.ranges = ranges;
  }
  
  public Set getTree() { return tree; }
  public void setTree(Set tree) { this.tree = tree; }
  
  public Set<String> getFolder() { return folder; }
  public void setFolder(Set<String> folder) { this.folder = folder; }
  
  
//  public LinkedList getLinked() {
//    return linked;
//  }
//  public void setLinked(LinkedList linked) {
//    this.linked = linked;
//  }
  
  
}
