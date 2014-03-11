/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.tpt;

import java.io.File;
import java.text.SimpleDateFormat;

import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.core.CoreContainer;
import org.vietspider.bean.Article;
import org.vietspider.bean.Meta;
import org.vietspider.bean.NLPData;
import org.vietspider.bean.NLPRecord;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.UtilFile;
import org.vietspider.common.text.CalendarUtils;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 28, 2009  
 */
class TptSolrDataIO  {

  private CoreContainer coreContainer;
  private EmbeddedSolrServer server;

  TptIndexingWriter writer;
  TptIndexingReader reader;
  
  public TptSolrDataIO() throws Exception  {
    File confgFile  = UtilFile.getFolder("system/solr2");  
    System.setProperty("solr.solr.home", confgFile.getAbsolutePath());
//    File dataFile  = UtilFile.getFolder("content/solr2/tpt_index");  
//    System.setProperty("solr.data.dir", dataFile.getAbsolutePath());
    
    CoreContainer.Initializer initializer = new CoreContainer.Initializer();
    coreContainer = initializer.initialize();
    server = new EmbeddedSolrServer(coreContainer, "tpt");
    
    this.writer = new TptIndexingWriter(server);
    this.reader = new TptIndexingReader(server);
//    LogService.getInstance().setMessage(null, "tpt index in "+ dataFile.getAbsolutePath() + " : "+ writer);
  }
  
  public TptIndex createSolrIndex(Article article) throws Exception {
    TptIndex bean = new TptIndex(article.getId());
    
    Meta meta = article.getMeta();
    NLPRecord nlp = article.getNlpRecord();
    
    String time  = meta.getTime();
    SimpleDateFormat dateFormat = CalendarUtils.getDateTimeFormat();
    bean.setTime(dateFormat.parse(time).getTime());
   
    time  = meta.getSourceTime();
    if(time != null && time.length() > 0) {
      bean.setSourceTime(dateFormat.parse(time).getTime());
    }
    
    bean.setUrl(meta.getSource());
    
    bean.getRegions().addAll(nlp.getData(NLPData.CITY));
    bean.getAction_objects().addAll(nlp.getData(NLPData.ACTION_OBJECT));
    bean.getEmails().addAll(nlp.getData(NLPData.EMAIL));
//    bean.getPhones().addAll(nlp.getData(NLPData.PHONE));
    bean.getPhones().addAll(nlp.getData(NLPData.TELEPHONE));
    bean.getPhones().addAll(nlp.getData(NLPData.MOBILE));
    bean.getAddresses().addAll(nlp.getData(NLPData.ADDRESS));
    bean.getAreas().addAll(nlp.getData(NLPData.AREA));
    bean.getPrices().addAll(nlp.getData(NLPData.PRICE));
    
    return bean;
  }

  void close() {
    writer.commit(false, false);
    try {
      server.commit(true, false);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    coreContainer.shutdown();
  }
  
}

