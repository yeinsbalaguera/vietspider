/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db.source.monitor;

import org.vietspider.serialize.NodeMap;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 27, 2007  
 */
@NodeMap("source-info")
public class SourceInfo {

  @NodeMap("name")
  private String name;
  @NodeMap("visit")
  private int visit;
  @NodeMap("data")
  private int data;
  @NodeMap("link")
  private long link;
  @NodeMap("lastAccess")
  private long lastAccess;

  public SourceInfo() {
  }

  public SourceInfo(String name) {
    this.name = name;
  }

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public int getVisit() { return visit; }
  public void setVisit(int visit) { this.visit = visit; }

  public int getData() { return data; }
  public void setData(int data) { this.data = data; }

  public long getLink() { return link; }
  public void setLink(long link) { this.link = link; }
  
  public long getLastAccess() { return lastAccess; }
  public void setLastAccess(long lastAccess) { this.lastAccess = lastAccess; }
  
//  public long getDownloaded() { return downloaded; }
//  public void setDownloaded(long downloaded) { this.downloaded = downloaded; }
  
  public SourceInfo clone() {
    SourceInfo sourceInfo = new SourceInfo();
    sourceInfo.setName(name);
    sourceInfo.setVisit(visit);
    sourceInfo.setData(data);
    sourceInfo.setLink(link);
//    sourceInfo.setDownloaded(downloaded);
    sourceInfo.setLastAccess(lastAccess);
    return sourceInfo;
  }

}
