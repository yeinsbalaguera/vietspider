/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.model;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.serialize.GetterMap;
import org.vietspider.serialize.NodeMap;
import org.vietspider.serialize.NodesMap;
import org.vietspider.serialize.SetterMap;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 18, 2007  
 */
@NodeMap("group")
public class Group {
  
//  public final static String DEFAULT = "DEFAULT";
  public final static String ARTICLE = "ARTICLE";
  public final static String JOB = "JOB";
  public final static String PRODUCT = "PRODUCT";
  public final static String CLASSIFIED = "CLASSIFIED";
  public final static String FORUM = "FORUM";
  public final static String PROFILE = "PROFILE";
  public final static String BLOG = "BLOG";
  public final static String SITE = "SITE";
  public final static String POETRY = "POETRY";
  public final static String LYRIC = "LYRIC";
  public final static String LITERATURE = "LITERATURE";
  public final static String SEARCHTIONARY = "SEARCHTIONARY";
  public final static String XML = "XML";
  public final static String STORY = "STORY";
//  public final static String SITE = "SITE";
//  public final static String SITE = "SITE";
  
  public final static String DUSTBIN = "DUSTBIN";
  
  @NodeMap("group-type")
  private String type = ARTICLE;
  
  @NodeMap("percent-relation")
  private int percentRelation = 0;
  
  @NodeMap("date-range-relation")
  private int dateRangeRelation = 3;
  
  @NodeMap("download-image")
  private boolean downloadImage = false;
  
  @NodeMap("force-download-image")
  private boolean forceDownloadImage = false;
  
  @NodeMap("set-image-to-meta")
  private boolean setImageToMeta = true;
  
//  @NodeMap("auto-detect-image")
//  private boolean autoDetectImage = true;
  
  @NodeMap("start-time")
  private int startTime = 0;
  
  @NodeMap("end-time")
  private int endTime = 23;
  
  @NodeMap("download-in-range-time")
  private boolean downloadInRangeTime = true;
  
  @NodeMap("check-title")
  private boolean checkTitle = true;
  
  @NodeMap("max-priority")
  private int maxPriority = 720;
  
  @NodeMap("min-priority")
  private int minPriority = -10;
  
  @NodesMap("process-regions")
  private List<Region> processRegions = new ArrayList<Region>();
  
//  @NodeMap("remove-desc")
//  private boolean desc = true;
  
  @NodeMap("remote")
  private String remote = "";
  
  public Group() {
//    Region region = new Region(Region.ARTICLE_TITLE);
//    region.setLocal(true);
//    processRegions.add(region);
//     processRegions.add(new Region(Region.ARTICLE_DESCRIPTION));
  }
  
  public String getType() { return type; }
  public void setType(String sourceType) { this.type = sourceType; }

  public Region getProcessRegion(int index) {
    if(index < 0 || index >= processRegions.size()) return null;
    return processRegions.get(index); 
  }
  
  public List<Region> getProcessRegions() { return processRegions; }
  /*public Region getProcessRegion(String name) {
    for(int i = 0; i < processRegions.size(); i++) {
     if(processRegions.get(i).getName().equals(name)) {
       return processRegions.get(i); 
     }
    }
    return null; 
  }  */
  public void setProcessRegions(List<Region> processRegions) { this.processRegions = processRegions; }
  
  @GetterMap("percent-relation")
  public int getMinPercentRelation() { return percentRelation; }
  @SetterMap("percent-relation")
  public void setMinPercentRelation(int percentRelation) { 
    this.percentRelation = percentRelation; 
  }
  
  @GetterMap("download-image")
  public boolean isDownloadImage() { return downloadImage; }
  public void setDownloadImage(boolean downloadImage) { this.downloadImage = downloadImage; }

  @GetterMap("force-download-image")
  public boolean isForceDownloadImage() { return forceDownloadImage; }
  public void setForceDownloadImage(boolean forceDownloadImage) { this.forceDownloadImage = forceDownloadImage; }
  
  public int getDateRangeRelation() { return dateRangeRelation; }
  public void setDateRangeRelation(int dateRangeRelation) { 
    this.dateRangeRelation = dateRangeRelation;
  }
  
 /* @GetterMap("remove-desc")
  public boolean isDesc() { return desc; }
  @SetterMap("remove-desc")
  public void setDesc(boolean desc) { this.desc = desc; }*/
  
  @GetterMap("remote")
  public String getRemote() { return remote; }
  @SetterMap("remote")
  public void setRemote(String value) { this.remote = value; }
  
  /*public String [] getProcessRegionNames() {
    String [] names = new String[processRegions.size()];
    for(int i = 0; i < names.length; i++) {
      names[i] = processRegions.get(i).getName();
    }
    return names;
  }*/
  
 /* public Region [] toRegions() {
    List<Region> list = new ArrayList<Region>();
    for(Region region : processRegions) {
      if(region.getPaths() == null) continue;
      list.add(new Region(region.getName(), region.getPaths()));
    }
    return list.toArray(new Region[list.size()]); 
  }
  
  public void setRegionValues(Region[] Regions) {
    if(Regions == null) return;
    for(Region Region : Regions) {
      if(Region == null) continue;
      Region region = searchRegion(Region.getName().trim());
      if(region == null) {
        region = new Region(Region.getName().trim());
        region.setLocal(true);
        processRegions.add(region);
      } 
      region.setPaths(Region.getPaths());
    }
  }
  */
 /* public void reset() {
    Iterator<Region> iterator = processRegions.iterator();
    while(iterator.hasNext()) {
      Region region = iterator.next();
      region.setPaths(null);
      if(region.isLocal()) iterator.remove();
    }
  }*/
  
  /*private Region searchRegion(String name) {
    for(Region region : processRegions) {
      if(region.getName().equals(name)) return region;
    }
    return null;
  }*/

  public int getStartTime() { return startTime; }
  public void setStartTime(int startTime) { 
    this.startTime = startTime;
  }

  public int getEndTime() { return endTime; }
  public void setEndTime(int endTime) {
    this.endTime = endTime;
  }

  public boolean isDownloadInRangeTime() { return downloadInRangeTime; }
  public void setDownloadInRangeTime(boolean downloadInRangeTime) { 
    this.downloadInRangeTime = downloadInRangeTime;
  }
  
  public boolean isCheckTitle() { return checkTitle; }
  public void setCheckTitle(boolean checkTitle) { this.checkTitle = checkTitle; }

  public boolean isSetImageToMeta() { return setImageToMeta; }
  public void setSetImageToMeta(boolean setImageToMeta) { 
    this.setImageToMeta = setImageToMeta; 
  }

//  public boolean isAutoDetectImage() { return autoDetectImage; }
//  public void setAutoDetectImage(boolean autoDetectImage) { this.autoDetectImage = autoDetectImage; }

  public int getMaxPriority() { return maxPriority; }
  public void setMaxPriority(int maxPriority) { this.maxPriority = maxPriority; }

  public int getMinPriority() { return minPriority; }
  public void setMinPriority(int minPriority) { this.minPriority = minPriority; }
  
}
