/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db.source.monitor;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.serialize.NodeMap;
import org.vietspider.serialize.NodesMap;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 27, 2007  
 */
@NodeMap("category-info")
public class CategoryInfo {
  
  @NodeMap("category") 
  private String category ;
  
  @NodesMap(value="sources", item="item")
  private List<SourceInfo> sources = new ArrayList<SourceInfo>();
  
  public CategoryInfo() {
  }
  
  public CategoryInfo(String category){
    this.category = category;
  }
  
  public void add(String name, int visit, int data, long link, /*long downloaded,*/ long lastAccess){
    for(SourceInfo source : sources) {
      if(source.getName().equals(name)) return ;
    }
    SourceInfo sourceInfo = new SourceInfo(name);
    sourceInfo.setVisit(visit);
    sourceInfo.setData(data);
    sourceInfo.setLink(link);
//    sourceInfo.setDownloaded(downloaded);
    sourceInfo.setLastAccess(lastAccess);
    sources.add(sourceInfo);
  }
  
  public String getCategory() { return category; }
  public void setCategory(String category) { this.category = category; }    
  
  public List<SourceInfo> getSources() { return sources; }
  public void setSources(List<SourceInfo> sources) { this.sources = sources; }

  public SourceInfo getSource(String name) {
    for(SourceInfo source : sources) {
      if(source.getName().equals(name)) return source;
    }
    return null;
  }
  
  public int getTotalData() {
    int total = 0; 
    for(SourceInfo source : sources) {
      total += source.getData();
    }
    return total;
  }
  
  public CategoryInfo clone() {
    CategoryInfo categoryInfo = new CategoryInfo(category);
    List<SourceInfo> list = new ArrayList<SourceInfo>();
    for(int i = 0; i < sources.size(); i++) {
      list.add(sources.get(i).clone());
    }
    categoryInfo.setSources(list);
    return categoryInfo;
  }
}
