/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.tpt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 28, 2009  
 */
public class TptIndex implements Serializable {
  
  private final static long serialVersionUID = -1l;
  
  private long id;
  
  private List<String> regions = new ArrayList<String>();
  private List<String> action_objects = new ArrayList<String>();
  private List<String> emails = new ArrayList<String>();
  private List<String> phones = new ArrayList<String>();
  private List<String> addresses = new ArrayList<String>();
  private List<String> areas = new ArrayList<String>();
  private List<String> prices = new ArrayList<String>();
  
  private boolean owner;
  
  private long time = -1;
  private long sourceTime = -1;
  
  private String url;
  
  private List<String> comments = new ArrayList<String>();
  
  public TptIndex(){
    
  }
  
  public TptIndex(long id) {
    this.id = id;
  }
  
  public TptIndex(String id) {
    this.id = Long.parseLong(id);
  }
  
  public long getId() { return id; }
  public void setId(long id) { this.id = id; }
  
  public long getTime() { return time; }
  public void setTime(long time) { this.time = time; }
  
  public long getSourceTime() { return sourceTime; }
  public void setSourceTime(long sourceTime) { this.sourceTime = sourceTime; }
  
  public String getUrl() { return url; }
  public void setUrl(String url) { this.url = url; }
  
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

  public List<String> getAreas() { return areas; }
  public void setAreas(List<String> areas) { this.areas = areas; }

  public List<String> getPrices() { return prices; }
  public void setPrices(List<String> prices) { this.prices = prices; }

  public boolean isOwner() { return owner; }
  public void setOwner(boolean owner) { this.owner = owner; }

  public void update(TptIndex article) {
    if(article.regions.size() > 0) this.regions = article.regions;
    
    this.owner = article.isOwner();
    
    if(article.getTime() != -1) this.time = article.getTime();
    if(article.getSourceTime() != -1) this.time = article.getSourceTime();
    
    if(article.getUrl() != null) this.url = article.getUrl();
    
    for(int i = 0; i < article.getComments().size(); i++) {
      String value = article.getComments().get(i);
      if(comments.contains(value)) continue;
      comments.add(value);
    }
     
  }
  
}
