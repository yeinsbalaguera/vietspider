/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.bean;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 26, 2007  
 */
public class SourceMonitor {
  
  private String date;

  private String source;
  
  private long linkCounter = 0;
  private int dataCounter = 0;
  private int crawlTime = 0;
//  private long downloaded = 0;
  private long lastAccess = 0;
  
  public SourceMonitor(){
  }
  
  /* public SourceMonitor(Domain domain, int data){
    this.date = domain.getDate();
    StringBuilder builder  = new StringBuilder(domain.getGroup());
    builder.append('.').append(domain.getCategory());
    this.source = builder.append('.').append(domain.getName()).toString();
    this.dataCounter = data;
  }
  
  public SourceMonitor(Source source_, int visit, int link, int data) {
    this.date = CommonDateTimeFormat.getDateFormat().format(Calendar.getInstance().getTime());
    StringBuilder builder  = new StringBuilder(source_.getGroup());
    builder.append('.').append(source_.getCategory());
    this.source = builder.append('.').append(source_.getName()).toString();
    this.visitCounter = visit;
    this.linkCounter = link;
    this.dataCounter = data;
  }*/
  
  public SourceMonitor(String sourceName,
      String date_, int crawlTime, int link, int data, long lastAccess) {
    this.source = sourceName;
    this.date = date_;
    this.crawlTime = crawlTime;
    this.linkCounter = link;
    this.dataCounter = data;
//    this.downloaded = downloaded;
    this.lastAccess = lastAccess;
  }
  
  public String getDate() { return date; }

  public void setDate(String date) { this.date = date; }

  public String getSource() { return source; }
  public void setSource(String source) { this.source = source; }

  public long getLinkCounter() { return linkCounter; }
  public void setLinkCounter(long link) { this.linkCounter = link; }

  public int getDataCounter() { return dataCounter; }
  public void setDataCounter(int data) { this.dataCounter = data; }

  public int getCrawlTime() { return crawlTime; }
  public void setCrawlTime(int visit) { this.crawlTime = visit; }

//  public long getDownloaded() { return downloaded; }
//  public void setDownloaded(long downloaded) { this.downloaded = downloaded; }

  public long getLastAccess() { return lastAccess; }
  public void setLastAccess(long lastAccess) { this.lastAccess = lastAccess; }
}
