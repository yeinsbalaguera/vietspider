/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.io;

import java.io.File;
import java.util.Properties;

import org.vietspider.common.io.LogService;
import org.vietspider.common.io.PropertiesFile;
import org.vietspider.common.io.UtilFile;
import org.vietspider.html.path2.NodePath;
import org.vietspider.model.Source;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 18, 2009  
 */
public class CrawlSourceLog {

  private volatile int totalVisitLink;
  private volatile int totalDataLink;
  
  private volatile int totalDownload;
  private volatile int totalDownloadSuccessfull;
  
  private volatile int totalExtractData;
  private volatile int totalExtractDataSuccessfull;
  
  private volatile int totalSavedData;
  private volatile int totalSavedDataSuccessfull;
  
  public int getTotalVisitLink() { return totalVisitLink; }
  public void setTotalVisitLink(int totalLink) { this.totalVisitLink = totalLink; }
  public void increaseTotalVisitLink() { this.totalVisitLink++; }
  
  public int getTotalDataLink() { return totalDataLink; }
  public void setTotalDataLink(int totalData) { this.totalDataLink = totalData; }
  public void increaseTotalDataLink() { this.totalDataLink++; }
  
  public int  getTotalDownload() { return totalDownload; }
  public void setTotalDownload(int totalDownload) { this.totalDownload = totalDownload; }
  public void increaseTotalDownload() { this.totalDownload++; }
  
  public int getTotalDownloadSuccessfull() { return totalDownloadSuccessfull; }
  public void setTotalDownloadSuccessfull(int totalDownloadSuccessfull) {
    this.totalDownloadSuccessfull = totalDownloadSuccessfull;
  }
  public void increaseTotalDownloadSuccessfull() { this.totalDownloadSuccessfull++; }
  
  public int getTotalExtractData() { return totalExtractData; }
  public void setTotalExtractData(int totalExtractData) {
    this.totalExtractData = totalExtractData;
  }
  public void increaseTotalExtractData() { this.totalExtractData++; }
  
  public int getTotalExtractDataSuccessfull() { return totalExtractDataSuccessfull; }
  public void setTotalExtractDataSuccessfull(int totalExtractDataSuccessfull) {
    this.totalExtractDataSuccessfull = totalExtractDataSuccessfull;
  }
  public void increaseTotalExtractDataSuccessfull() { this.totalExtractDataSuccessfull++; }
  
  public int getTotalSavedData() { return totalSavedData; }
  public void setTotalSavedData(int totalSavedData) { this.totalSavedData = totalSavedData; }
  public void increaseTotalSavedData() { this.totalSavedData++; }
  
  public int getTotalSavedDataSuccessfull() { return totalSavedDataSuccessfull; }
  public void setTotalSavedDataSuccessfull(int totalSavedDataSuccessfull) {
    this.totalSavedDataSuccessfull = totalSavedDataSuccessfull;
  }
  public void increaseTotalSavedDataSuccessfull() { this.totalSavedDataSuccessfull++; }
  
  
  public String getLogMessage(Source source) {
    if(source == null) return "";
    if(totalDownloadSuccessfull < 1) return "";
    int rate = 0;
    if(totalDataLink > 0) {
      rate = computeRate(totalSavedDataSuccessfull, totalDataLink);
    }
    if(rate > 70) return "";
    
    Properties properties = loadErrorMessages();
    StringBuilder builder = new StringBuilder();
    if(totalDataLink < 1) {
      if(source.getDepth() > 1) {
        if(totalVisitLink < 1) {
          buildMessage(properties, builder, "no.visit.link");
          NodePath [] nodePaths = source.getUpdatePaths();
//        System.out.println(" node path "+ nodePaths + (nodePaths != null ? nodePaths.length : " null"));
          
          if(nodePaths != null && nodePaths.length > 0) {
            buildMessage(properties, builder, "update.path.incorrect");
            return builder.toString();
          }
          
          if(source.getLinkBuilder().getVisitPatterns() != null) {
            buildMessage(properties, builder, "check.visit.pattern");
            return builder.toString();
          }
        }
      }
      buildMessage(properties, builder, "no.data.link");
      return builder.toString();
    }
    
    rate = computeRate(totalDownloadSuccessfull, totalDownload);
    if(rate < 10) {
      buildMessage(properties, builder, "extract.path.incorrect");
      return builder.toString();
    }
    
    rate = computeRate(totalExtractDataSuccessfull, totalExtractData);
    if(rate < 20) {
      buildMessage(properties, builder, "no.data.link");
      return builder.toString();
    }
    
    rate = computeRate(totalSavedDataSuccessfull, totalSavedData);
    if(rate < 10) {
      buildMessage(properties, builder, "error.save.data");
      return builder.toString();
    }
    
    return builder.toString();
  }
  
  private int computeRate(int value, int total) {
    if(total == 0) return 100;
    return (value*100)/total;
  }
  
  private void buildMessage(Properties properties, StringBuilder builder, String key) {
    if(builder.length() > 0) builder.append('\n');
    String value = properties.getProperty(key);
    if(value == null || value.trim().isEmpty()) value = key;
    builder.append(value);
  }
  
  private Properties loadErrorMessages() {
    File file = UtilFile.getFile("system", "crawl.error.properties");
    if(!file.exists())  return new Properties();
    
    PropertiesFile propertiesFile = new PropertiesFile(true);
    try {
      return propertiesFile.load(UtilFile.getFile("system", "crawl.error.properties"));
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    return new Properties();
  }
}
