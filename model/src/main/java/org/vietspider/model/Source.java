/***************************************************************************
 * Copyright 2003-2007 by VietSpider - All rights reserved.                *    
 **************************************************************************/
package org.vietspider.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.vietspider.html.path2.NodePath;
import org.vietspider.link.ContentFilters;
import org.vietspider.link.IPageChecker;
import org.vietspider.serialize.GetterMap;
import org.vietspider.serialize.NodeMap;
import org.vietspider.serialize.NodesMap;
import org.vietspider.serialize.PropertiesMap;
import org.vietspider.serialize.SetterMap;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Apr 22, 2007
 */
@NodeMap("source")
public class Source implements Serializable {
  
  private static final long serialVersionUID = 1L;
  
  @NodeMap(value = "category")
  protected String category;
  public void setCategory(String d){ this.category = d; }
  public String getCategory(){ return this.category; }

  @NodeMap(value = "name")
  protected String name ;
  public void setName(String n){ this.name = n; } 
  public String getName(){ return this.name; }  

  @NodesMap(value = "home", item = "url")
  protected String [] home;
  public void setHome(String [] value){ home = value; }
  public String [] getHome(){ return this.home; }
  
  @NodeMap("update-regions")
  protected Region updateRegion = new Region();
  @SetterMap("update-regions")
  public void setUpdateRegion(Region value){ this.updateRegion = value; }
  @GetterMap("update-regions")
  public Region getUpdateRegion(){ return this.updateRegion; }
  
  @NodeMap(value = "pattern")
  protected String pattern;
  public void setPattern(String p) {
    if(p == null) return;
    this.pattern = p.trim(); 
  }
//.replaceAll( "\\&amp;", "\\&").trim();    
  public String getPattern(){ return this.pattern; }  

  @NodeMap(value = "encoding", attribute = true)
  protected String encoding;
  public void setEncoding( String c){ this.encoding = c; }
  public String getEncoding(){ return this.encoding; }
  
  @NodeMap(value = "depth" , attribute = true)
  protected int depth = 1 ;
  public void setDepth(int d){ this.depth = d; }
  public int getDepth(){ return this.depth; }
  
  @NodesMap(value = "crawl-times", item = "time")
  protected String [] crawlTimes;
  public void setCrawlTimes(String [] values){ crawlTimes = values; }
  public String [] getCrawlTimes(){ return this.crawlTimes; }
  
  @NodeMap(value = "priority", attribute = true)
  protected int priority = 1;
  public int getPriority() { return priority; }
  public void setPriority(int priority) { this.priority = priority; }
  
  @NodeMap(value = "source-type", attribute = true)
  protected String group =  Group.ARTICLE;;
  @GetterMap("source-type")
  public String getGroup() { return group;  }
  @SetterMap("source-type")
  public void setGroup(String sourceType) { this.group = sourceType; }
  
  @NodeMap(value = "extract-type", attribute = true)
  protected ExtractType extractType = ExtractType.NORMAL;
  public ExtractType getExtractType() { return extractType; }
  public void setExtractType(ExtractType detachType) { this.extractType = detachType; }
  
  @NodesMap("extract-regions")
  protected Region [] extractRegion;
  @SetterMap("extract-regions")
  public void setExtractRegion(Region [] value){ this.extractRegion = value; }
  @GetterMap("extract-regions")
  public Region[] getExtractRegion(){ return this.extractRegion; }
  
  @NodesMap("process-regions")
  protected Region [] processRegion;
  @SetterMap("process-regions")
  public void setProcessRegion(Region [] value){ this.processRegion = value; }
  @GetterMap("process-regions")
  public Region[] getProcessRegion(){ return this.processRegion; }
  
  @PropertiesMap(value = "properties", item = "property")
  public Properties properties;
  @GetterMap("properties")
  public Properties getProperties() {
    if(properties == null) properties = new Properties();
    return properties; 
  }
  
  @SetterMap("properties")
  public void setProperties(Properties sourceProperties) { 
    this.properties = sourceProperties; 
  }
  
  @NodeMap(value = "lastModified", attribute = true)
  private long lastModified = -1;
  @GetterMap("lastModified")
  public long getLastModified() { return lastModified; }
  @SetterMap("lastModified")
  public void setLastModified(long lastModified) { this.lastModified = lastModified;}
  
  @NodeMap(value = "lastCrawledTime", attribute = true)
  private long lastCrawledTime = -1;
  @GetterMap("lastCrawledTime")
  public long getLastCrawledTime() { return lastCrawledTime; }
  @SetterMap("lastCrawledTime")
  public void setLastCrawledTime(long lastCrawledTime) { this.lastCrawledTime = lastCrawledTime;}
  
  @Override
  public String toString() {
    return new StringBuilder("SOURCE: ").append(getFullName()).toString();
  }
  
//  private transient volatile boolean save = false;
//  public boolean isSave() { return save; }
//  public void setSave(boolean value) { this.save = value; }
  
  private transient volatile String fullName = null;
  public String getFullName() {
    if(fullName != null) return fullName;
    StringBuilder builder = new StringBuilder(100);
    builder.append(group).append('.').append(category).append('.').append(name);
    this.fullName = builder.toString();
    return fullName;
  }
  
  /*private transient volatile int homepageCode = -1;
  public int getHomepageCode() {
    if(homepageCode != -1) return homepageCode;
    StringBuilder builder = new StringBuilder();
    for(int i = 0; i < home.length; i++) {
      if(home[i] == null) continue;
      if(builder.length() > 0) builder.append('\n');
      builder.append(home[i]);
    }
    homepageCode = builder.toString().hashCode();
    return homepageCode;
  }*/
  
  
  private transient volatile int codeName = -1;
  public int getCodeName() {
    if(codeName == -1) {
      codeName = getFullName().hashCode(); 
    }
    return codeName; 
  }
  
  private transient volatile String codeSite = null;
  public String getCodeSite() {  return codeSite; }
  public void setCodeSite(String code) { this.codeSite = code; }
  
  private transient volatile boolean redown = false;
  public boolean isRedown() { return redown; }
  public void setRedown(boolean redown) { this.redown = redown; }
  
  private transient volatile long createdTime = System.currentTimeMillis();
  public long getCreatedTime() { return createdTime; }
  public void setCreatedTime(long createdTime) { this.createdTime = createdTime; }
  
  protected transient volatile NodePath [] updatePaths;
  public NodePath[] getUpdatePaths() { return updatePaths; }
  public void setUpdatePaths(NodePath [] _paths) { updatePaths = _paths; }
  
  private transient volatile boolean decode = false;
  public boolean isDecode() { return decode; }
  public void setDecode(boolean value) {decode = value; }
  
  protected transient  volatile long startCrawling = System.currentTimeMillis();
  public long getStartCrawling() { return startCrawling; }
  public void resetSession() { startCrawling = System.currentTimeMillis(); }
  private transient volatile long expireCrawling = 60*60*1000l;
  
  public boolean isTimeout() {
    return System.currentTimeMillis() - startCrawling >= expireCrawling;
  }
  public void setExpireCrawling(long value) { expireCrawling = value; }
  
  private transient volatile List<Object> linkGenerators = new ArrayList<Object>();
  public List<Object> getLinkGenerators() { return linkGenerators; }
  public void setLinkGenerators(List<Object> generators) { linkGenerators = generators; } 
  
  private transient volatile List<String> jsDocWriters;
  public void setJsDocWriters(List<String> jsDocWriters) {
    this.jsDocWriters = jsDocWriters;
  }
  public List<String> getJsDocWriters() { return jsDocWriters; }
  
  private transient volatile long minSizeOfPage = -1;
  public long getMinSizeOfPage() { return minSizeOfPage; }
  public void setMinSizeOfPage(long minSizeOfPage) { this.minSizeOfPage = minSizeOfPage; }
  
//  private transient volatile int debug = 0;
//  public boolean isDebug() { return debug != 0; }
//  public int getDebug() { return debug; }
//  public void setDebug(int value) { this.debug = value; }
  
  private transient volatile IPageChecker pageChecker;
  public IPageChecker getPageChecker() { return pageChecker; }
  public void setPageChecker(IPageChecker pageChecker) { this.pageChecker = pageChecker;  }

  private transient volatile LinkBuilder<?> linkBuilder;
  public LinkBuilder<?> getLinkBuilder() { return linkBuilder; }
  public void setLinkBuilder(LinkBuilder<?> linkBuilder) { this.linkBuilder = linkBuilder; }
  
  private transient volatile ContentFilters contentFilters; 
  public ContentFilters getContentFilters() { return contentFilters; }
  public void setContentFilters(ContentFilters contentFilters) { 
    this.contentFilters = contentFilters;
  }
  
  public Source clone() {
    Source source = new Source();
    
    source.setHome(getHome());
    source.setUpdateRegion(getUpdateRegion());
    
    source.setName(getName());
    source.setCategory(getCategory());
    source.setGroup(getGroup());
    
    source.setDepth(getDepth());
    source.setPriority(getPriority());
    source.setEncoding(getEncoding());
    
    source.setPattern(pattern);
    
    source.setExtractRegion(extractRegion);
    source.setExtractType(extractType);
    source.setProcessRegion(processRegion);
    
    source.setProperties(properties);
    
    source.setLastModified(lastModified);
    
    return source;
  }
  
}