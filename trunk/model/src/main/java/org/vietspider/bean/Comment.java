/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.bean;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 29, 2009  
 */
public class Comment {
  
  private String username;
  private long time;
  private String content;
  
  public Comment() {
  }
  
  public Comment(String username, String connent) {
    this.username = username;
    this.content = connent;
    this.time = System.currentTimeMillis();
  }
  
  public String getUsername() { return username; }
  public void setUsername(String username) { this.username = username; }
  
  public long getTime() { return time; }
  public void setTime(long time) { this.time = time; }
  
  public String getContent() { return content;  }
  public void setContent(String content) { this.content = content; }
  
}
