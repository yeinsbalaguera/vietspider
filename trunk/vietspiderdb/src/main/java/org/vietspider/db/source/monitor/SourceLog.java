package org.vietspider.db.source.monitor;

import org.vietspider.serialize.NodeMap;

@NodeMap("source-log")
public class SourceLog  {
  
  private transient String date;
  
  @NodeMap(value = "name")
  private String name;
  
  @NodeMap(value = "crawl-time")
  private int crawlTime = 0;
  
//  @NodeMap(value = "total-downloaded")
//  private long totalDownload = 0;
  
  @NodeMap(value = "total-link")
  private int totalLink = 0;
  
  @NodeMap(value = "total-data")
  private int totalData = 0;
  
  @NodeMap(value = "last-access")
  private long lastAccess = -1;
  
  @NodeMap(value = "desc")
  private String desc = "";
  
  public SourceLog() { }
  
  public SourceLog(String name) {
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
  
//  public long getTotalDownload() { return totalDownload; }
//  public void setTotalDownload(long totalDownload) { this.totalDownload = totalDownload; }
  
  public long getLastAccess() { return lastAccess; }
  public void setLastAccess(long lastAccess) { this.lastAccess = lastAccess; }
  
  public String getDate() { return date; }
  public void setDate(String date) { this.date = date; }
  
  public String getDesc() { return desc; }
  public void setDesc(String desc) { this.desc = desc; }
  
  public void update(SourceLog other) {
    this.name = other.getName();
    
    this.crawlTime += other.getCrawlTime();
//    this.totalDownload += other.getTotalDownload();
    
    this.totalLink += other.getTotalLink();
    this.totalData += other.getTotalData();
    
    this.lastAccess = other.getLastAccess();
    this.desc += "\n" + other.getDesc();
  }
  

}