/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.bean.xml;

import org.vietspider.bean.Article;
import org.vietspider.bean.Meta;
import org.vietspider.serialize.GetterMap;
import org.vietspider.serialize.NodeMap;
import org.vietspider.serialize.SetterMap;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 25, 2008  
 */
@NodeMap("article")
public class XArticle {
  
  public XArticle() {
    
  }
  
  public XArticle(Article article) {
    Meta meta = article.getMeta();
    this.id = meta.getId();
    this.time = meta.getTime();
    this.title = meta.getTitle();
    this.desc = meta.getDesc();
    this.image = meta.getImage();
    this.domain = meta.getDomain();
    this.sourceTime = meta.getSourceTime();
    this.url = meta.getSource();
    this.content = article.getContent().getContent();
  }
  
  @NodeMap(value = "id")
  private String id;
  public String getId(){ return id; }
  public void setId(String value){ this.id  = value; }
  
  @NodeMap(value = "title")
  private String title = "";
  public String getTitle(){ return title; }
  public void setTitle(String value){ title = value; }
  
  @NodeMap(value = "desc")
  private String desc = "";
  public String getDesc(){
    return (desc == null || desc.trim().equals("null")) ? "" :desc;
  }
  public void setDesc(String value){ desc = value; }
  
  @NodeMap(value = "content")
  private String  content = "";
  public String getContent(){
    return (content == null || content.trim().equals("null")) ? "" : content;
  }
  public void setContent(String value){ content = value; }
    
  @NodeMap(value = "image")
  private String  image;
  public String getImage(){ return image; }
  public void setImage(String value){  image = value; }
  
  @NodeMap(value = "time")
  private String  time;
  public String getTime(){ return time; }
  public void setTime(String value){ time = value; }
  
  @NodeMap(value = "source_time")
  private String  sourceTime;
  public String getSourceTime(){ return sourceTime; }
  public void setSourceTime(String value){ sourceTime = value; }
  
  @NodeMap(value = "domain")
  private String domain;
  public String getDomain(){   return domain;  }  
  public void setDomain(String value){ domain = value;  }
  
  @NodeMap(value = "url")
  private String url;
  @GetterMap("url")
  public String getSource(){ return url; }
  @SetterMap("url")
  public void setSource(String value){ url = value; }
 
}
