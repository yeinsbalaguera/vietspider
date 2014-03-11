/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.tpt;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.vietspider.common.io.LogService;
import org.vietspider.common.text.CalendarUtils;
import org.vietspider.db.SystemProperties;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 23, 2010  
 */
final class TptIndexingWriter {

  private EmbeddedSolrServer server;

  protected volatile TptTempIndexs tempIndexs;
  protected volatile int tempSize = 0;
  protected volatile long lastCommit = -1l;

  private int optimizeHour = -1;
  private int optimizeDate = -1;

  private int expireDate = 30;

  TptIndexingWriter(EmbeddedSolrServer server) {
    tempIndexs = new TptTempIndexs();
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
  }

  TptTempIndexs getTempArticles() { return tempIndexs; }

  void save(TptIndex tpt) {
    tempIndexs.add(tpt);
    tempSize++;
  }

  TptIndex loadTemp(long id) { return tempIndexs.loadTemp(id); }

  boolean isCommit() {
    if(tempIndexs.isEmpty()) return false;
    //    System.out.println(" thay temp size is  "+ tempSize);
    if(tempSize > 100) return true;

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
    List<TptIndex> indexes = new ArrayList<TptIndex>();

    while(!tempIndexs.isEmpty()) {
      TptIndex index = tempIndexs.poll();

      SolrInputDocument doc = toSolrDocument(index);
      if(doc != null) {
        docs.add(doc);
        indexes.add(index);
      }

//            if(docs.size() >= 500 || deleteIds.size() >= 500) break;
    }

    tempSize = tempIndexs.size();

    //    System.out.println(" add docs "+ docs.size());

    if(docs.size() > 0) {
      try {
        server.add(docs);
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
        for(int i = 0; i < indexes.size(); i++) {
          tempIndexs.add(indexes.get(i));
        }
      }

      try {
        server.commit(waitFlush, waitSearcher);
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
        for(int i = 0; i < indexes.size(); i++) {
          tempIndexs.add(indexes.get(i));
        }
      }
    }

    lastCommit = System.currentTimeMillis();

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

    calendar = Calendar.getInstance();
    optimizeDate = calendar.get(Calendar.DAY_OF_MONTH);
    new Thread() {
      public void run() {
        deleteExpire();
        
        Calendar _calendar = Calendar.getInstance();

        SimpleDateFormat dateFormat = CalendarUtils.getDateTimeFormat();
        LogService.getInstance().setMessage(null, 
            "Start optimize database at " + dateFormat.format(_calendar.getTime()));
        try {
          server.optimize();
        } catch (Exception e) {
          LogService.getInstance().setThrowable(e);
        }
        _calendar = Calendar.getInstance();
        LogService.getInstance().setMessage(null, 
            "Finish optimize database at " + dateFormat.format(_calendar.getTime()));
      }
    }.start();
  }

  void deleteByQuery(SolrQuery query) throws Exception {
    server.deleteByQuery(query.getQuery());
  }

  protected SolrInputDocument toSolrDocument(TptIndex article) {
    SolrInputDocument document = new SolrInputDocument();
    document.addField("id", String.valueOf(article.getId()), 1.0f);

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

    list = article.getAreas();
    for(int i = 0; i < list.size(); i++) {
      document.addField("area", list.get(i));
    }


    list = article.getPrices();
    for(int i = 0; i < list.size(); i++) {
      document.addField("price", list.get(i));
    }

    document.addField("owner", article.isOwner());

    document.addField("time", article.getTime());
    document.addField("source_time", article.getSourceTime());

    document.addField("url", article.getUrl());

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
        "TptIndex start delete database at " + dateFormat.format(calendar.getTime()));

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
