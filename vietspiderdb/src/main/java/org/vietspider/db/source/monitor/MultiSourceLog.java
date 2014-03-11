/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db.source.monitor;

import java.io.Serializable;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 22, 2010  
 */
@SuppressWarnings("serial")
public class MultiSourceLog  implements Serializable {

  private String name;
  
  private int crawlTime = 0;
  private long totalDownload = 0;
  
  private int totalLink = 0;
  private int totalData = 0;
  
  private long lastAccess = -1;
  
  private String desc = "";
  
  public MultiSourceLog() { }
  
  public MultiSourceLog(String name) {
    this.name = name;
  }
  
  public String getName() { return name; }
  public void setName(String name) { 
    this.name = name;
  }

  public int getCrawlTime() { return crawlTime; }
  public void setCrawlTime(int crawlTime) { this.crawlTime = crawlTime; }

  public int getTotalLink() { return totalLink; }
  public void setTotalLink(int totalLink) { this.totalLink = totalLink; }

  public int getTotalData() { return totalData; }
  public void setTotalData(int totalData) { this.totalData = totalData; }
  
  public long getTotalDownload() { return totalDownload; }
  public void setTotalDownload(long totalDownload) { this.totalDownload = totalDownload; }
  
  public long getLastAccess() { return lastAccess; }
  public void setLastAccess(long lastAccess) { this.lastAccess = lastAccess; }
  
  public String getDesc() { return desc; }
  public void setDesc(String desc) { this.desc = desc; }
  
  public void update(SourceLog other) {
    this.name = other.getName();
    
    this.crawlTime += other.getCrawlTime();
//    this.totalDownload += other.getTotalDownload();
    
    this.totalLink += other.getTotalLink();
    this.totalData += other.getTotalData();
    
    if(lastAccess < other.getLastAccess()) {
      this.lastAccess = other.getLastAccess();
    }
    
    StringBuilder builder = new StringBuilder(desc);
    if(builder.length() > 0 
        && other.getDesc().length() > 0) builder.append('\n');
    builder.append(other.getDesc());
    
    this.desc = builder.toString();
  }
  
}
