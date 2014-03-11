/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.net.server;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.serialize.NodeMap;
import org.vietspider.serialize.NodesMap;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 25, 2007  
 */
@NodeMap("crawler-status")
public class CrawlerStatus {
  //for value return
  public final static int NULL = -3;
  public final static int IS_END_SESSION = -2;
  public final static int STOPED = -1;
  
  //for input param
  public final static int RUNNING = 0;
  public final static int START_OR_STOP = 1;
  public final static int STOP_ITEM = 2;
//  public final static int WAKE = 2;
  public final static int GO_TO_ITEM = 3;
  public final static int GO_TO_ITEM_WITH_REDOWNLOAD = 4;
  
  @NodeMap("status")
  private int status;
  
  @NodesMap(value = "sources", item = "item")
  private List<String> sources = new ArrayList<String>();
  
  @NodesMap(value = "threadStatus", item = "item")
  private String [] threadStatus;
  
  @NodeMap("totalThread")
  private int totalThread = 1;
  
  public CrawlerStatus() {
    
  }
  
  public CrawlerStatus(int status) {
    this.status = status;
  }

  
  public int getStatus() { return status; }
  public void setStatus(int status) { this.status = status; }
  
  public String [] getThreadStatus() { return threadStatus; }

  public void setThreadStatus(String [] threadStatus) { this.threadStatus = threadStatus; }
  
  public void clearSource() { sources.clear(); }
  
//  public void addSource(Source source) {
//    if(source == null)  return;
//    addSource(source.getGroup().toString(), source.getCategory(), source.getName());
//  }
  
  public void addSource(String line) { sources.add(line); }
  
//  public void addSource(String group, String category, String name) {
//    sources.add(name+"."+group+"."+category);
//  }
  
//  public int getSourceId() {
//    if(group == null) return -1;
//    return (group+"."+category+"."+name).hashCode();
//  }

  public int getTotalThread() { return totalThread; }

  public void setTotalThread(int totalThread) { this.totalThread = totalThread; }

  public List<String> getSources() { return sources; }

  public void setSources(List<String> sources) {
    this.sources = sources;
  }
  
}
