/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.vietspider.locale.vn.VietnameseConverter;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 28, 2009  
 */
public class SolrIndex implements Serializable {
  
  private final static long serialVersionUID = -1l;
  
  private long id;
  private String title;
  private String titleNoMark;
  private String text;
  private String textNoMark;
  
  private List<String> regions = new ArrayList<String>();
  private List<String> action_objects = new ArrayList<String>();
  private List<String> emails = new ArrayList<String>();
  private List<String> phones = new ArrayList<String>();
  private List<String> addresses = new ArrayList<String>();
  private List<Float> areas = new ArrayList<Float>();
  private List<Double> priceTotal = new ArrayList<Double>();
  private List<Double> priceM2 = new ArrayList<Double>();
  private List<Double> priceMonth = new ArrayList<Double>();
  
  private boolean owner = false;
  
  private long time = -1;
  private long sourceTime = -1;
  
  private String url;
  private String urlCode;
  
  private List<String> comments = new ArrayList<String>();
  
  
  public SolrIndex(){
    
  }
  
  public SolrIndex(long id) {
    this.id = id;
  }
  
  public SolrIndex(String id) {
    this.id = Long.parseLong(id);
  }
  
  public long getId() { return id; }
  public void setId(long id) { this.id = id; }
  
  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }
  
  public String getTitleNoMark() {
    if(titleNoMark != null) return titleNoMark;
    if(title == null) return null;
    titleNoMark = VietnameseConverter.toTextNotMarked(title);
    return titleNoMark; 
  }
  public void setTitleNoMark(String titleNoMark) { this.titleNoMark = titleNoMark; }

  public String getText() { return text; }
  public void setText(String text) { this.text = text; }
  
  public String getTextNoMark() {
    if(textNoMark != null) return textNoMark;
    if(text == null) return null;
    textNoMark = VietnameseConverter.toTextNotMarked(text);
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
  
  public List<String> getComments() { return comments; }
  public void setComments(List<String> comments) { this.comments = comments; }
  public void addComment(String value) {
    for(int i = 0; i < comments.size(); i++) {
      if(comments.get(i).equals(value)) return;
    }
    comments.add(value);
  }
  
  public List<String> getRegions() { return regions; }
  public void setRegions(List<String> regions) { this.regions = regions; }

  public List<String> getAction_objects() { return action_objects; }
  public void setAction_objects(List<String> action_objects) { 
    this.action_objects = action_objects;
  }

  public List<String> getEmails() { return emails; }
  public void setEmails(List<String> emails) { this.emails = emails; }

  public List<String> getPhones() { return phones; }
  public void setPhones(List<String> phones) { this.phones = phones; }

  public List<String> getAddresses() { return addresses; }
  public void setAddresses(List<String> addresses) { this.addresses = addresses; }

  public List<Float> getAreas() { return areas; }
  public void setAreas(List<Float> areas) { this.areas = areas; }
  
  public List<Double> getPriceTotal() { return priceTotal; }
  public void setPriceTotal(List<Double> priceTotal) { this.priceTotal = priceTotal; }

  public List<Double> getPriceM2() { return priceM2; }
  public void setPriceM2(List<Double> priceM2) { this.priceM2 = priceM2; }

  public List<Double> getPriceMonth() { return priceMonth; }
  public void setPriceMonth(List<Double> priceMonth) { this.priceMonth = priceMonth; }

  public boolean isOwner() { return owner; }
  public void setOwner(boolean owner) { this.owner = owner; }

  public void update(SolrIndex article) {
    if(article.getTitle() != null)  this.title = article.getTitle();
    if(article.getText() != null) this.text = article.getText();
    
    if(article.regions.size() > 0) this.regions = article.regions;
    
    this.owner = article.isOwner();
    
    if(article.getTime() != -1) this.time = article.getTime();
    if(article.getSourceTime() != -1) this.time = article.getSourceTime();
    
    if(article.getUrl() != null) this.url = article.getUrl();
    if(article.getUrlCode() != null) this.url = article.getUrlCode();
    
    for(int i = 0; i < article.getComments().size(); i++) {
      String value = article.getComments().get(i);
      if(comments.contains(value)) continue;
      comments.add(value);
    }
     
  }
  
}
