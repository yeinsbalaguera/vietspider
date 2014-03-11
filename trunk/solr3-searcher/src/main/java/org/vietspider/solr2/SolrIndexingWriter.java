/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.solr2;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.vietspider.bean.SolrIndex;
import org.vietspider.common.io.LogService;
import org.vietspider.common.text.CalendarUtils;
import org.vietspider.db.SystemProperties;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 23, 2010  
 */
final class SolrIndexingWriter extends Thread {

  private EmbeddedSolrServer server;

  //  private EmbeddedSolrServer tempServer;

  protected volatile SolrIndexTempIndexs tempIndexs;// = new ConcurrentLinkedQueue<SolrArticle>();
  protected volatile java.util.Queue<String> tempDeletes = new ConcurrentLinkedQueue<String>();
  protected volatile int tempSize = 0;
  protected volatile long lastCommit = -1l;

  //  protected RSolrDatabaseWriter rdatabase;

  private int optimizeHour = -1;
  private int optimizeDate = -1;
  
  private int expireDate = 30;

//  protected DeletingDatabase deletingDb;

  SolrIndexingWriter(EmbeddedSolrServer server) {
    tempIndexs = new SolrIndexTempIndexs();
    //    LogService.getInstance().setMessage(null, Class.forName("org.vietspider.VietSpider").getName());
    //    LogService.getInstance().setMessage(null, Class.forName("org.apache.solr.core.SolrResourceLoader").getName());
    this.server = server;
    
    try {
      String txt = SystemProperties.getInstance().getValue("solr.expire.time");
      if(txt == null) {
        txt = "30";
        SystemProperties.getInstance().putValue("solr.expire.time", "30", false);
      }
      expireDate = Integer.parseInt(txt);
    } catch (Exception e) {
      SystemProperties.getInstance().putValue("solr.expire.time", "30", false);
    }  

    try {
      String txt = SystemProperties.getInstance().getValue("solr.optimize.hour");
      if(txt == null) {
        txt = "-1";
        SystemProperties.getInstance().putValue("solr.optimize.hour", "-1", false);
      }
      optimizeHour = Integer.parseInt(txt);
    } catch (Exception e) {
      SystemProperties.getInstance().putValue("solr.optimize.hour", "-1", false);
    }
    this.start();
  }
  
  public void run() {
    while(true) {
      try {
//        System.out.println(" truoc ========  >"+ tempIndexs.size());
        if(isCommit()) commit(true, true);
//        System.out.println(" sau ========  >"+ tempIndexs.size());
      } catch (Throwable e) {
        LogService.getInstance().setThrowable(e);
      }

      try {
        optimize();
      } catch (Throwable e) {
        LogService.getInstance().setThrowable(e);
      }
      
      try {
        Thread.sleep(10*1000l);
      } catch (Exception e) {
      }
    }
  }

//  void setDeletingDb(DeletingDatabase deletingDb) {
//    this.deletingDb = deletingDb;
//  }

  //  void setRdatabase(RSolrDatabaseWriter rdatabase) {
  //    this.rdatabase = rdatabase;
  //  }

  SolrIndexTempIndexs getTempArticles() { return tempIndexs; }

  void save(SolrIndex article) {
    if(article.getText() == null) {
      throw new NullPointerException("Text is null");
    }
//    System.out.println(" save index danh" + article.getText());
    tempIndexs.add(article);
    tempSize++;
  }
  
  void delete(String id) { 
    tempDeletes.add(id); 
  }
  
//  void index(String id) { 
//    tempIndexs.index(id); 
//  }
//  
//  void owner(String id) { 
//    tempIndexs.owner(id); 
//  }

  SolrIndex loadTemp(long id) { return tempIndexs.loadTemp(id); }

  boolean isCommit() {
    if(tempIndexs.isEmpty()) return false;
//    System.out.println(" thay temp size is  "+ tempSize);
    if(tempSize > 100) return true;
    if(tempDeletes.size() > 100) return true;
    
    if(System.currentTimeMillis() - lastCommit >= 15*60*1000l) {
      //      System.out.println("vuot qua 15 phut");
      return true;
    }
    return false;
  }

  synchronized void commit(boolean waitFlush, boolean waitSearcher) {
    //    System.out.println("chuan bi commit");
    List<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
//    List<String> deleteIds =  new ArrayList<String>();
    List<SolrIndex> wrappers = new ArrayList<SolrIndex>();
    
    
//    Iterator<SolrIndexWrapper> iterator = tempIndexs.iterator();
//    while(iterator.hasNext()) {
//      SolrIndexWrapper wrapper = iterator.next();
//      if(!wrapper.isOk()) continue;
//      iterator.remove();
//      SolrInputDocument doc = toSolrDocument(wrapper.get());
//      if(doc == null) continue;
//      docs.add(doc);
//      wrappers.add(wrapper);
//    }

    while(!tempIndexs.isEmpty()) {
      SolrIndex index = tempIndexs.poll();
      
      //verified id for test
//      IDVerifiedStorages.save("solr_index", String.valueOf(index.getId()));
//      if(docs.size() >= 500 || deleteIds.size() >= 500) break;
      SolrInputDocument doc = toSolrDocument(index);
      docs.add(doc);
      wrappers.add(index);
    }
    
    tempSize = tempIndexs.size();
    
//    System.out.println(" add docs "+ docs.size());

    if(docs.size() > 0) {
      try {
        server.add(docs);
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
        for(int i = 0; i < wrappers.size(); i++) {
          tempIndexs.add(wrappers.get(i));
        }
      }

      try {
        server.commit(waitFlush, waitSearcher);
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
        for(int i = 0; i < wrappers.size(); i++) {
          tempIndexs.add(wrappers.get(i));
        }
      }
    }
    
    List<String> deletes = new ArrayList<String>();

    while(!tempDeletes.isEmpty()) {
      deletes.add(tempDeletes.poll());
    }
    
//    for(String id : deletes) {
//      System.out.println(" delete "+ id);
//    }

    if(deletes.size() > 0) {
      try {
        server.deleteById(deletes);
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }

      try {
        server.commit(waitFlush, waitSearcher);
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
    
    lastCommit = System.currentTimeMillis();

//    optimize();
  }

  void optimize() {
    Calendar calendar = Calendar.getInstance();
    if(calendar.get(Calendar.HOUR_OF_DAY) != optimizeHour) return;
    if(calendar.get(Calendar.DAY_OF_MONTH) == optimizeDate) return;

//    new Thread() {
//      public void run() {
//        NlpTptService.getInstance().deleteExpire(expireDate);
//      }
//    }.start();
    
    deleteExpire();
    
    optimizeDate = calendar.get(Calendar.DAY_OF_MONTH);

    SimpleDateFormat dateFormat = CalendarUtils.getDateTimeFormat();
    LogService.getInstance().setMessage(null, 
        "Start optimize database at " + dateFormat.format(calendar.getTime()));
    try {
      server.optimize();
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    calendar = Calendar.getInstance();
    LogService.getInstance().setMessage(null, 
        "Finish optimize database at " + dateFormat.format(calendar.getTime()));
  }

  void deleteByQuery(SolrQuery query) throws Exception {
    server.deleteByQuery(query.getQuery());
  }

  protected SolrInputDocument toSolrDocument(SolrIndex article) {
    SolrInputDocument document = new SolrInputDocument();
    document.addField("id", String.valueOf(article.getId()), 1.0f);

    document.addField("title", article.getTitle().toLowerCase(), 3.0f);
//    System.out.println(" index title "+ article.getTitle());
    document.addField("title_no_mark",  article.getTitleNoMark(), 3.0f);
    //    System.out.println("vao day co text "+article.getId()+ " : " + text);
    document.addField("text", article.getText(), 1.0f);
    document.addField("text_no_mark",  article.getTextNoMark(), 1.0f);
    
    List<String> list = article.getRegions();
//    System.out.println(" add region "+ list.size());
    for(int i = 0; i < list.size(); i++) {
//      System.out.println("====== > "+ list.get(i));
      document.addField("region", list.get(i));
    }
    
    list = article.getAction_objects();
    for(int i = 0; i < list.size(); i++) {
      document.addField("action_object", list.get(i));
    }
    
    list = article.getEmails();
    for(int i = 0; i < list.size(); i++) {
      document.addField("email", list.get(i));
    }
    
    list = article.getPhones();
    for(int i = 0; i < list.size(); i++) {
      document.addField("phone", list.get(i));
    }
    
    list = article.getAddresses();
    for(int i = 0; i < list.size(); i++) {
      document.addField("address", list.get(i));
    }
    
    List<Float> areas = article.getAreas();
    for(int i = 0; i < areas.size(); i++) {
      document.addField("area", areas.get(i));
    }
    
    
    List<Double> prices = article.getPriceTotal();
    if(prices != null) {
      for(int i = 0; i < prices.size(); i++) {
        document.addField("price_total", prices.get(i));
      }
    }
    
    prices = article.getPriceM2();
    if(prices != null) {
      for(int i = 0; i < prices.size(); i++) {
        document.addField("price_m2", prices.get(i));
      }
    }
    
    prices = article.getPriceMonth();
    if(prices != null) {
      for(int i = 0; i < prices.size(); i++) {
        document.addField("price_month", prices.get(i));
      }
    }

    document.addField("owner", article.isOwner());
    
    document.addField("time", article.getTime());
    document.addField("source_time", article.getSourceTime());

    document.addField("url", article.getUrl());
    document.addField("url_code", article.getUrlCode());

    list = article.getComments();
    for(int i = 0; i < list.size(); i++) {
      document.addField("comment", list.get(i));
    }

    return document;
  }
  
  private void deleteExpire() {
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat dateFormat = CalendarUtils.getDateTimeFormat();
    LogService.getInstance().setMessage(null,
        "Start delete database at " + dateFormat.format(calendar.getTime()));

    calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - expireDate);
    long end = calendar.getTimeInMillis();

    calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 5);
    long start = calendar.getTimeInMillis();

    //        System.out.println(start + " : "+ end);

    try {
//      System.out.println("source_time:[" + start + " TO " + end + "]");
      server.deleteByQuery("source_time:[" + start + " TO " + end + "]");
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }

    calendar = Calendar.getInstance();
    calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - expireDate);

    end  = calendar.getTimeInMillis();

    calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 5);
    start = calendar.getTimeInMillis();

    //        System.out.println(start + " : "+ end);
    try {
      server.deleteByQuery("time:[" + start + " TO " + end + "]");
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }


}
