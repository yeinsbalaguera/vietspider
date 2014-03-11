/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 28, 2009  
 */
public class ArticleIndex implements Serializable {
  
  private final static long serialVersionUID = -1l;
  
  public final static short SAVE = 1;
  public final static short REMOVE = -1;
  
  private long id;
  
  private short action = SAVE;
  
  private String title;
  private String titleNoMark;
  private String text;
  private String textNoMark;
  
  private String source;
  private String category;
  
  private long time = -1;
  private long sourceTime = -1;
  
  private String url;
  private String urlCode;
  
  private List<String> tags = new ArrayList<String>();
  
  private int rate = 0;
  private int status = 0;
  
  public ArticleIndex(){
    
  }
  
  public ArticleIndex(long id) {
    this.id = id;
  }
  
  public ArticleIndex(String id) {
    this.id = Long.parseLong(id);
  }
  
  public long getId() { return id; }
  public void setId(long id) { this.id = id; }
  
  public short getAction() { return action; }
  public void setAction(short action) { this.action = action;  }
  
  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }
  
  public String getTitleNoMark() {
   /* if(titleNoMark != null) return titleNoMark;
    if(title == null) return null;
    titleNoMark = VietnameseConverter.toTextNotMarked(title);*/
    return titleNoMark; 
  }
  public void setTitleNoMark(String titleNoMark) { this.titleNoMark = titleNoMark; }

  public String getText() { return text; }
  public void setText(String text) { this.text = text; }
  
  public String getTextNoMark() {
    /*if(textNoMark != null) return textNoMark;
    if(text == null) return null;
    textNoMark = VietnameseConverter.toTextNotMarked(text);*/
    return textNoMark; 
  }
  public void setTextNoMark(String textNoMark) { this.textNoMark = textNoMark; }
  
  public long getTime() { return time; }
  public void setTime(long time) { this.time = time; }
  
  public long getSourceTime() { return sourceTime; }
  public void setSourceTime(long sourceTime) { this.sourceTime = sourceTime; }
  
  public String getUrl() { return url; }
  public void setUrl(String url) { this.url = url; }
  
  public String getUrlCode() { return urlCode; }
  public void setUrlCode(String urlCode) { this.urlCode = urlCode; }
  
  public List<String> getTags() { return tags; }
  public void setTags(List<String> tags) { this.tags = tags; }
  public void addTag(String value) {
    if(tags == null) tags = new ArrayList<String>();
    for(int i = 0; i < tags.size(); i++) {
      if(tags.get(i).equals(value)) return;
    }
    tags.add(value);
  }
  
  public int getRate() { return rate;  }
  public void setRate(int rate) { this.rate = rate; }
  
  public int getStatus() { return status; }
  public void setStatus(int status) { this.status = status; }
  
  public String getSource() { return source; }
  public void setSource(String value) { this.source = value; }
  
  public String getCategory() { return category; }
  public void setCategory(String value) { this.category = value; }

  public void update(ArticleIndex article) {
    if(article.getTitle() != null)  this.title = article.getTitle();
    if(article.getText() != null) this.text = article.getText();
    
    if(article.getTime() != -1) this.time = article.getTime();
    if(article.getSourceTime() != -1) this.time = article.getSourceTime();
    
    if(article.getUrl() != null) this.url = article.getUrl();
    if(article.getUrlCode() != null) this.url = article.getUrlCode();
    
    if(article.getSource() != null) this.source = article.getSource();
    if(article.getCategory() != null) this.category = article.getCategory();
    
    if(article.getRate()!= 0) this.rate = article.getRate();
    
    if(article.getTags() != null) {
      if(tags == null) tags = new ArrayList<String>();
      for(int i = 0; i < article.getTags().size(); i++) {
        String tag = article.getTags().get(i).toLowerCase();
        if(tags.contains(tag)) continue;
        tags.add(tag);
      }
    }
  }
  
  
}
