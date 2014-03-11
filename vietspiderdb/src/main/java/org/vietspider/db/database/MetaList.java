/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.db.database;

import java.io.Serializable;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.vietspider.bean.Article;
import org.vietspider.common.Application;
import org.vietspider.serialize.NodeMap;
import org.vietspider.serialize.NodesMap;
/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Dec 25, 2006
 */
@SuppressWarnings("serial")
@NodeMap("meta-list")
public class MetaList implements Serializable {

  @NodesMap(value = "data", item = "item")
  private List<Article> data;
  @NodeMap("total-page")
  private int totalPage;
  @NodeMap("title")
  private String title;
  @NodeMap("url")
  private String url;
  @NodeMap("current-page")
  private int currentPage;
  @NodeMap("action")
  private String action;
  @NodeMap("extension")
  private String extension = "";
  
  @NodeMap("page-size")
  private int pageSize = 15;
  
  @NodeMap("total-data")
  private long totalData;
  
  public MetaList(String action){
    this();
    this.action = action;
  }
  
  public MetaList(){
    data = new ArrayList<Article>(pageSize);
  } 
  
  public List<Article> getData() {
    if(data  == null) data  = new ArrayList<Article>();
    return data; 
  }
  public void setData(List<Article> list) { this.data = list; }

  public int getTotalPage() { return totalPage; }
  public void setTotalPage(int total) { this.totalPage = total; }

  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title;}

  public String getUrl() { return url; }
  
  public void setUrl(String url) { 
    try {
      this.url = URLEncoder.encode(url, Application.CHARSET);
    } catch (Exception e) {
      this.url = url;
    }
  }

  public int getCurrentPage() { return currentPage; }
  public void setCurrentPage(int currentPage) {
//    data.clear();
    this.currentPage = currentPage; 
  }

  public String getAction() {return action; }
  public void setAction(String action) {this.action = action;}

  public String getExtension() { return extension; }
  public void setExtension(String extension) { this.extension = extension; }
  
  public long getTotalData() { return totalData; }
  public void setTotalData(long totalData) { this.totalData = totalData; }

  public int getPageSize() { return pageSize; }
  public void setPageSize(int pageSize) { this.pageSize = pageSize;  }
  
}
