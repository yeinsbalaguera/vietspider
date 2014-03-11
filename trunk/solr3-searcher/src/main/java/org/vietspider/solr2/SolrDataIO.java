/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.solr2;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.core.CoreContainer;
import org.vietspider.bean.Article;
import org.vietspider.bean.Content;
import org.vietspider.bean.Meta;
import org.vietspider.bean.NLPData;
import org.vietspider.bean.NLPRecord;
import org.vietspider.bean.SolrIndex;
import org.vietspider.chars.TextSpliter;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.UtilFile;
import org.vietspider.common.text.CalendarUtils;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.parser.HTMLParser2;
import org.vietspider.html.renderer.NLPRenderer;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 28, 2009  
 */
class SolrDataIO  {

  private CoreContainer coreContainer;
  private EmbeddedSolrServer server;

  private SolrIndexingWriter writer;
  private SolrIndexingReader reader;
  
  private HTMLParser2 parser;
//  private SolrIndexStorage storage;

  public SolrDataIO(SolrIndexStorage storage) throws Exception  {
    File confgFile  = UtilFile.getFolder("system/solr2");  
    System.setProperty("solr.solr.home", confgFile.getAbsolutePath());
//    File dataFile  = UtilFile.getFolder("content/solr2/data");  
//    System.setProperty("solr.data.dir", dataFile.getAbsolutePath());
    
//    this.storage = storage;

    //    LogService.getInstance().setMessage(null, Class.forName("org.vietspider.VietSpider").getName());
    //    LogService.getInstance().setMessage(null, Class.forName("org.apache.solr.core.SolrResourceLoader").getName());

    CoreContainer.Initializer initializer = new CoreContainer.Initializer();
    coreContainer = initializer.initialize();
    server = new EmbeddedSolrServer(coreContainer, "vsearch");
    
    parser = new HTMLParser2();

    this.writer = new SolrIndexingWriter(server);
    this.reader = new SolrIndexingReader(server, storage, writer);
  }
  
  public SolrIndex createSolrIndex(Article article) throws Exception {
    SolrIndex bean = new SolrIndex(article.getId());
    
    Meta meta = article.getMeta();
    Content content = article.getContent();
    NLPRecord nlp = article.getNlpRecord();
    
    if(meta == null) return null;
    
    bean.setTitle(meta.getTitle());
    
    StringBuilder builder = new StringBuilder();
    builder.append(meta.getDesc());
    HTMLDocument doc = parser.createDocument(content.getContent());
    NLPRenderer textRenderer = new NLPRenderer(doc.getRoot());
    builder.append('\n').append('\n').append(textRenderer.getTextValue());
    bean.setText(builder.toString());
    
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
    
    List<Float> areas = new ArrayList<Float>();
    List<String> temp  = nlp.getData(NLPData.AREA);
    TextSpliter spliter = new TextSpliter();
    for(int i = 0; i < temp.size(); i++) {
      String value = temp.get(i);
//      System.out.println("====================="+ value);
      List<String> elements = spliter.toList(value, '-');
      for(String ele : elements) {
        if(ele.endsWith("m2")) {
          ele = ele.substring(0, ele.length()-2);
        }
//        System.out.println(" ==== > " + ele);
        areas.add(Float.valueOf(ele));
      }
    }
    bean.getAreas().addAll(areas);
    
    List<Double> prices = new ArrayList<Double>();
    temp  = nlp.getData(NLPData.PRICE_TOTAL);
    for(int i = 0; i < temp.size(); i++) {
      prices.add(Double.valueOf(temp.get(i)));
    }
    bean.getPriceTotal().addAll(prices);
    
    prices = new ArrayList<Double>();
    temp  = nlp.getData(NLPData.PRICE_UNIT_M2);
    for(int i = 0; i < temp.size(); i++) {
      prices.add(Double.valueOf(temp.get(i)));
    }
    bean.getPriceM2().addAll(prices);
    
    prices = new ArrayList<Double>();
    temp  = nlp.getData(NLPData.PRICE_MONTH);
    for(int i = 0; i < temp.size(); i++) {
      prices.add(Double.valueOf(temp.get(i)));
    }
    bean.getPriceMonth().addAll(prices);
    
    String owner = meta.getPropertyValue("owner");
    if(owner != null) {
//      System.out.println(" received=========== > "+ article.getId());
      bean.setOwner(Boolean.valueOf(owner));
    }
    
    meta.removeProperty("temp.text");
    meta.removeProperty("temp.price");
    meta.removeProperty("temp.url.code");
    
    return bean;
  }

  public SolrIndexingWriter getWriter() { return writer; }
  
  public SolrIndexingReader getReader() { return reader; }

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

