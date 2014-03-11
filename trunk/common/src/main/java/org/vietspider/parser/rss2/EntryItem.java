/*
 * Copyright 2004-2006 The VietSpider        All rights reserved.
 *
 * Created on January 24, 2007, 8:58 PM
 */
package org.vietspider.parser.rss2;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.GetterMap;
import org.vietspider.serialize.NodeMap;
import org.vietspider.serialize.SetterMap;


/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 13, 2006
 */
@NodeMap("entry")
public class EntryItem implements IMetaItem {  
  
  @NodeMap("id")
  private String id;
  
  @NodeMap("title")
  private String title = "";
  
  @NodeMap("summary")
  private String desc =  "";
  
  @NodeMap("content")
  private String content =  "";
  
  @NodeMap("image")
  private String image = "";
  
  @NodeMap("published")
  private String time = "";
  
  private List<MetaLink> links = new ArrayList<MetaLink>(5);
  
  private XMLNode node;
  
  public EntryItem() {} 
  
  public String getId() { return id; }
  public void setId(String id) { this.id = id; }
   
  public void setTitle(String title){ this.title = title; }  
  public String getTitle(){ return title; }
  
  @SetterMap("description")
  public void setDesc(String desc){ this.desc = desc; }
  @GetterMap("description")
  public String getDesc(){ return desc; }
  
  public void setImage(String image){ this.image = image; }  
  public String getImage(){ return image; }  
  
  @SetterMap("pubDate")
  public void setTime(String time){ this.time = time; }
  @GetterMap("pubDate")
  public String getTime(){ return time;  }
  
  public void addLink(MetaLink link){ this.links.add(link); }
  public MetaLink getLink(int index) {
    return (index < 0 || index >= links.size()) ? null : links.get(index);
  }
  public List<MetaLink> getLinks(){ return links; }
  
  public void setNode(XMLNode node){ this.node = node; }  
  public XMLNode getNode(){ return node; }
  
  public XMLNode getItem(String name){
    List<XMLNode> children = node.getChildren();
    for(XMLNode ele : children){
      if(ele.isNode(name)) return ele;
    }
    return null;
  }
  
  public String getValueItem(String name){
    XMLNode n = getItem(name);
    if(n == null || n.getTotalChildren() < 1) return "";
    return n.getChild(0).getNodeValue();
  }

  public String getContent() { return content; }
  public void setContent(String content) { this.content = content; }

}
