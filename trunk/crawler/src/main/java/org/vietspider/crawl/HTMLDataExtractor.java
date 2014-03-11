/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl;

import java.util.Properties;

import org.vietspider.db.link.track.LinkLogStorages;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.path2.HTMLExtractor;
import org.vietspider.html.path2.NodePath;
import org.vietspider.html.path2.NodePathParser;
import org.vietspider.model.Region;
import org.vietspider.model.Source;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 16, 2008  
 */
public final class HTMLDataExtractor extends HTMLExtractor {

  private volatile NodePath [] extractPaths;
  
  private volatile NodePath [] removePaths;
  private volatile boolean isRemoveFrom = false;
  
//  private volatile String cacheHtmlData;
  private volatile boolean notExtract;
  
  HTMLDocument [] extractRow(HTMLDocument document) {
    return super.extractRow(document, extractPaths);
  }
  
  HTMLDocument extract(HTMLDocument document) {
    if(extractPaths == null ||extractPaths.length < 1) return document;
    return super.extract(document, extractPaths);
  }
  
  public void remove(HTMLNode root) {
    super.remove(root, isRemoveFrom, removePaths);
  }
  
  boolean isNotExtract() { return notExtract; }
  
//  public String getCacheHtmlData() { return cacheHtmlData;  }
//  public void setCacheHtmlData(String cacheHtmlData) { this.cacheHtmlData = cacheHtmlData; }
  
  void newSession(Source value) {
    notExtract = false;
    
    Region [] regions = value.getExtractRegion();
    NodePathParser pathParser = new NodePathParser();
    
    extractPaths = null;
    if(regions != null && regions.length > 0 
        && regions[0] != null && regions[0].hasData())  {
      try {
        extractPaths = pathParser.toNodePath(regions[0].getPaths());
      } catch (Exception e) {
        LinkLogStorages.getInstance().save(value, e);
//        LogWebsite.getInstance().setThrowable(value, e);
        extractPaths = null;
      }
    }
    
    removePaths = null;
    isRemoveFrom = false;
    
    if(regions != null && regions.length > 1 
        && regions[1] != null && regions[0].getPaths() != null) {
      try {
        removePaths = pathParser.toNodePath(regions[1].getPaths());
      } catch (Exception e) {
        LinkLogStorages.getInstance().save(value, e);
//        LogWebsite.getInstance().setThrowable(value, e);
        removePaths = null;
      }

      Properties properties = regions[1].getProperties();
      String property = properties.getProperty(Region.CLEAN_FROM);
      if(property != null) isRemoveFrom = Boolean.valueOf(property); 
    }

    if((extractPaths != null && extractPaths.length > 0)
        || (removePaths != null && removePaths.length > 0)) return;
    notExtract = true;
  }

}
