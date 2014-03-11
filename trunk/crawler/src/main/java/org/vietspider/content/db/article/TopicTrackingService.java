/***************************************************************************
 * Copyright 2003-2007 by VietSpider - All rights reserved.                *    
 **************************************************************************/
package org.vietspider.content.db.article;

import java.io.File;
import java.util.Calendar;
import java.util.Queue;

import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.core.CoreContainer;
import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.UtilFile;
import org.vietspider.content.db.tp.PluginData2TpDocument;
import org.vietspider.content.db.tp.TpComputor;
import org.vietspider.content.db.tp.TpIndexingReader;
import org.vietspider.content.db.tp.TpIndexingWriter;
import org.vietspider.content.tp.TpWorkingData;
import org.vietspider.crawl.plugin.PluginData;
import org.vietspider.db.CrawlerConfig;
import org.vietspider.db.SystemProperties;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Dec 8, 2011
 */
class TopicTrackingService {
  
  private final static long TIMEOUT = 5*60*1000l;
  
  private CoreContainer solrCoreContainer;
  private EmbeddedSolrServer solrServer;

  private TpIndexingWriter indexingWriter;
  private TpIndexingReader indexingReader;
  
  private boolean isTp = false;
  
  private PluginData2TpDocument converter;
  
  private int deleteDate = -1;
  
  private long lastAccess = -1;
  
  TopicTrackingService() {
    SystemProperties prop = SystemProperties.getInstance();
    String startMiningValue = prop.getValue(Application.START_MINING_INDEX_SERVICE);
    startMiningValue = startMiningValue != null ? startMiningValue.trim() : null;
    isTp = "true".equals(startMiningValue);
    
    converter = new PluginData2TpDocument();
  }
  
  void add(PluginData data) {
    if(!isTp) return;
    lastAccess = System.currentTimeMillis();
    try {
      data.setTpData(converter.convert(data));
    } catch (Exception e) {
      LogService.getInstance().setMessage(data.getLink().getSourceFullName(), e, e.toString());
    }
  }
  
  void compute(PluginData data) {
    if(!isTp || solrServer == null) return;
    TpWorkingData tpData = data.getTpData();
    if(tpData != null) {
      TpComputor indexComputor = new TpComputor(indexingReader, data.getGroup());
      try {
        indexComputor.compute(tpData);
      } catch (Throwable e) {
        LogService.getInstance().setThrowable(e);
      }
      lastAccess = System.currentTimeMillis();
//      saveTpData(tpData, data.getGroup().getType());
    }
  }
  
  void write(Queue<PluginData> working) {
    if(!isTp || solrServer == null) return;
    try {
      indexingWriter.commit(working, false, false);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }
  
  void deleteExpire() {
    if(!isTp || solrServer == null) return;
    if((System.currentTimeMillis() - lastAccess) >= TIMEOUT) {
//      System.out.println(" da chuan bi time out roi nhe ");
      shutdown();
      solrCoreContainer = null;
      solrServer = null;
      indexingWriter = null;
      indexingReader = null;
      System.gc();
//      Runtime.getRuntime().gc();
      return;
    }
    if (CrawlerConfig.CLEAN_DATA_HOUR < 0) return;
    Calendar calendar = Calendar.getInstance();
    if(solrServer == null) return;
//      if (calendar.get(Calendar.HOUR_OF_DAY) == CrawlerConfig.CLEAN_DATA_HOUR) {
//        File file  = UtilFile.getFolder("content/tp/ARTICLE/database3/");
//        if(file.exists() && file.isDirectory()) {
//          deleteExpireDate1(file, CrawlerConfig.EXPIRE_DATE);
//        }
//      }
    if(calendar.get(Calendar.DATE) != deleteDate) {
      deleteDate = calendar.get(Calendar.DATE);
      indexingWriter.deleteExpire();
    }
  }
  
  synchronized void init() {
    if(!isTp || solrServer != null) return;
    if((System.currentTimeMillis() - lastAccess) >= TIMEOUT) return;
    try {
      File confgFile  = UtilFile.getFolder("system/solr2");  
      System.setProperty("solr.solr.home", confgFile.getAbsolutePath());
      CoreContainer.Initializer initializer = new CoreContainer.Initializer();
      solrCoreContainer = initializer.initialize();
      solrServer = new EmbeddedSolrServer(solrCoreContainer, "ctpt");
      this.indexingWriter = new TpIndexingWriter(solrServer);
      this.indexingReader = new TpIndexingReader(solrServer/*, tpDatabases*/);
      
      this.indexingReader.setWordSplitter(converter.getWordSplitter());
      this.indexingReader.setWordStore(converter.getWordStore());
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
      isTp = false;
    }
  }
  
  void shutdown() {
    if(!isTp || solrServer == null) return;
    try {
      solrServer.commit(true, false);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    solrCoreContainer.shutdown();
  }
}
