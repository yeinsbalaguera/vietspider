/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.article.index;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.vietspider.bean.ArticleIndex;
import org.vietspider.common.io.LogService;
import org.vietspider.common.text.CalendarUtils;
import org.vietspider.db.SystemProperties;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 23, 2010  
 */
final class ArticleIndexWriter extends Thread {

  private EmbeddedSolrServer server;

  //  private EmbeddedSolrServer tempServer;

  protected volatile ArticleIndexTempIndexs tempIndexs;// = new ConcurrentLinkedQueue<SolrArticle>();
  protected volatile java.util.Queue<String> tempDeletes = new ConcurrentLinkedQueue<String>();
  protected volatile int tempSize = 0;
  protected volatile long lastCommit = -1l;

  //  protected RSolrDatabaseWriter rdatabase;

  private int optimizeHour = -1;
  private int optimizeDate = -1;
  
  private int expireDate = 30;

//  protected DeletingDatabase deletingDb;

  ArticleIndexWriter(EmbeddedSolrServer server) {
    tempIndexs = new ArticleIndexTempIndexs();
    //    LogService.getInstance().setMessage(null, Class.forName("org.vietspider.VietSpider").getName());
    //    LogService.getInstance().setMessage(null, Class.forName("org.apache.solr.core.SolrResourceLoader").getName());
    this.server = server;
    
    try {
      String txt = SystemProperties.getInstance().getValue("expire.date");
      if(txt == null) {
        txt = "30";
        SystemProperties.getInstance().putValue("expire.date", "30", false);
      }
      expireDate = Integer.parseInt(txt);
    } catch (Exception e) {
      SystemProperties.getInstance().putValue("expire.date", "30", false);
    }  

    try {
      String txt = SystemProperties.getInstance().getValue("schedule.clean.data");
      if(txt == null) {
        txt = "-1";
        SystemProperties.getInstance().putValue("schedule.clean.data", "-1", false);
      }
      optimizeHour = Integer.parseInt(txt);
    } catch (Exception e) {
      SystemProperties.getInstance().putValue("schedule.clean.data", "-1", false);
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
  
  ArticleIndexTempIndexs getTempArticles() { return tempIndexs; }

  void save(ArticleIndex article) {
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
  
  ArticleIndex loadTemp(long id) { return tempIndexs.loadTemp(id); }

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
    commitDelete(waitFlush, waitSearcher);    
    commitInsert(waitFlush, waitSearcher);
    lastCommit = System.currentTimeMillis();
    //    optimize();
  }
  
  private void commitInsert(boolean waitFlush, boolean waitSearcher) {
    //    System.out.println("chuan bi commit");
    List<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
//    List<String> deleteIds =  new ArrayList<String>();
    List<ArticleIndex> wrappers = new ArrayList<ArticleIndex>();
    
    while(!tempIndexs.isEmpty()) {
      ArticleIndex index = tempIndexs.poll();
      
//      System.out.println(" thuannd insert article index "+ index.getId());
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
  }
  
  private void commitDelete(boolean waitFlush, boolean waitSearcher) {
    List<String> deletes = new ArrayList<String>();

    while(!tempDeletes.isEmpty()) {
      deletes.add(tempDeletes.poll());
    }
    
//    for(String id : deletes) {
//      System.out.println(" thuannd delete article index "+ id);
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
    LogService.getInstance().setMessage("Article Index",  null, 
        "Start optimize database at " + dateFormat.format(calendar.getTime()));
    try {
      server.optimize();
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    calendar = Calendar.getInstance();
    LogService.getInstance().setMessage("Article Index",  null, 
        "Finish optimize database at " + dateFormat.format(calendar.getTime()));
  }

  void deleteByQuery(SolrQuery query) throws Exception {
    server.deleteByQuery(query.getQuery());
  }

  protected SolrInputDocument toSolrDocument(ArticleIndex article) {
    SolrInputDocument document = new SolrInputDocument();
    document.addField("id", String.valueOf(article.getId()), 1.0f);

    document.addField("title", article.getTitle().toLowerCase(), 3.0f);
    document.addField("title_no_mark",  article.getTitleNoMark(), 3.0f);
    
    document.addField("text", article.getText(), 1.0f);
    document.addField("text_no_mark",  article.getTextNoMark(), 1.0f);
    
    document.addField("time", article.getTime());
    document.addField("source_time", article.getSourceTime());

    document.addField("url", article.getUrl());
    document.addField("url_code", article.getUrlCode());

    List<String> list = article.getTags();
    for(int i = 0; i < list.size(); i++) {
      document.addField("tag", list.get(i));
    }
    
    document.addField("category", article.getCategory());
    document.addField("source", article.getSource());

//    System.out.println(" vietspider index "+ article.getId());
    return document;
  }
  
  private void deleteExpire() {
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat dateFormat = CalendarUtils.getDateTimeFormat();
    LogService.getInstance().setMessage("Article Index", null, 
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
