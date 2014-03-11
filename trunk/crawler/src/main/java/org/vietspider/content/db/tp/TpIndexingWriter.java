/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.db.tp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.vietspider.bean.Domain;
import org.vietspider.bean.Meta;
import org.vietspider.common.io.LogService;
import org.vietspider.common.text.CalendarUtils;
import org.vietspider.crawl.plugin.PluginData;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 23, 2010  
 */
final public class TpIndexingWriter {
  
  private final static int EXPIRE_DATE = 15;

  private EmbeddedSolrServer server;

  public TpIndexingWriter(EmbeddedSolrServer server) {
    this.server = server;
  }

  public void commit(Collection<PluginData> list, 
      boolean waitFlush, boolean waitSearcher) throws Exception {
    //    System.out.println("chuan bi commit");
    List<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
    //    List<String> deleteIds =  new ArrayList<String>();

    Iterator<PluginData> iterator = list.iterator();
    while(iterator.hasNext()) {
      PluginData data = iterator.next();
      if(data.getTpData() == null) continue;
      SolrInputDocument doc = toSolrDocument(data);
      if(doc != null) docs.add(doc);
    }

    //    System.out.println(" add docs "+ docs.size());

    if(docs.size() > 0) {
      server.add(docs);
      server.commit(waitFlush, waitSearcher);
    }

  }

  void deleteByQuery(SolrQuery query) throws Exception {
    server.deleteByQuery(query.getQuery());
  }

  protected SolrInputDocument toSolrDocument(PluginData data) {
    Meta meta = data.getArticle().getMeta();
    SolrInputDocument document = new SolrInputDocument();
    document.addField("id", meta.getId(), 1.0f);

    Domain domain = data.getArticle().getDomain();
    String tag = domain.getCategory();
    int idx = tag.indexOf('.');
    if(idx > 0) tag = tag.substring(idx+1);
    document.addField("tag", tag);
    document.addField("group", domain.getGroup());
    document.addField("title", meta.getTitle());
    document.addField("source", domain.getName());
    
    document.addField("time", String.valueOf(meta.getCalendar().getTimeInMillis()));

    TreeSet<String> list = data.getTpData().getKeys();
    Iterator<String> iterator = list.iterator();
    while(iterator.hasNext()) {
      document.addField("key", iterator.next());
    }
    
    document.addField("word", data.getTpData().word2Text());
    document.addField("key_word", data.getTpData().key2Text());

    return document;
  }

  public void deleteExpire() {
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat dateFormat = CalendarUtils.getDateTimeFormat();
    LogService.getInstance().setMessage(null,
        "TptIndex start delete database at " + dateFormat.format(calendar.getTime()));

    calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - EXPIRE_DATE);
    long end = calendar.getTimeInMillis();

    calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 5);
    long start = calendar.getTimeInMillis();

    //        System.out.println(start + " : "+ end);
    try {
      server.deleteByQuery("time:[" + start + " TO " + end + "]");
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }


}
