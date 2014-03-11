/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.solr2;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.vietspider.bean.Article;
import org.vietspider.bean.Domain;
import org.vietspider.bean.Meta;
import org.vietspider.bean.MetaRelations;
import org.vietspider.bean.Relations;
import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.db.SystemProperties;
import org.vietspider.index.SearchResponse;
import org.vietspider.index.SearchResultCollection;
import org.vietspider.net.client.DataClientService;
import org.vietspider.net.server.URLPath;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.XML2Object;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 1, 2009  
 */
public class RSolrDatabaseReader implements ISolrDatabaseSearcher {

  protected String username; 
  protected String handlerPath;

  protected DataClientService[] clients;  

  public RSolrDatabaseReader(String remote) {
    String [] elements = remote.split("\\,");
    username = SystemProperties.getInstance().getValue("sync.data.username");
    if(remote == null || remote.trim().isEmpty()) {
      clients = new DataClientService[0];
      return;
    }
    
    LogService.getInstance().setMessage(null, "Load remote data from "+ remote);
    clients = new DataClientService[elements.length];
    for(int i = 0; i < elements.length; i++) {
      clients[i] = new DataClientService(elements[i]);
    }
  }

  public SearchResultCollection searchForCached(SearchResponse searcher, int index) {
    return searchForCached(index, searcher, 0);
  }
  
  private SearchResultCollection searchForCached(int index, SearchResponse searcher, int time) {
    try {
      Header [] headers = new Header[] {
          new BasicHeader("action", "solr.search.remote.for.cached")
      };

      String xml = Object2XML.getInstance().toXMLDocument(searcher).getTextValue();
      byte [] bytes = xml.getBytes(Application.CHARSET);
      bytes = clients[index].post(URLPath.SEARCH_HANDLER, bytes, headers);
      if(bytes.length < 2) return null;
      return XML2Object.getInstance().toObject(SearchResultCollection.class, bytes);
    } catch (Exception e) {
      if(time > 2) {
        StringBuilder builder = new StringBuilder();
        builder.append("search: ").append(searcher.getQuery().getPattern());
        builder.append(", time: ").append(time);
        builder.append(",  from: ").append(clients[index].getUrl());
        builder.append(",  error: ").append(e.toString());
        LogService.getInstance().setMessage(e, builder.toString());
        return null;
      } 
      return searchForCached(index, searcher, time + 1);
    }
  }
  
  public SearchResultCollection loadArticles(SearchResultCollection collection, int index) {
    return loadArticles(index, collection, 0);
  }
  
  private SearchResultCollection loadArticles( int index, SearchResultCollection collection, int time) {
    try {
      Header [] headers = new Header[] {
          new BasicHeader("action", "solr.search.remote.load.articles")
      };

      String xml = Object2XML.getInstance().toXMLDocument(collection).getTextValue();
      byte [] bytes = xml.getBytes(Application.CHARSET);
      bytes = clients[index].post(URLPath.SEARCH_HANDLER, bytes, headers);
      if(bytes.length < 2) return null;
      return XML2Object.getInstance().toObject(SearchResultCollection.class, bytes);
    } catch (Exception e) {
      if(time > 2) {
        StringBuilder builder = new StringBuilder();
        builder.append("load articles, time: ").append(time);
        builder.append(",  from: ").append(clients[index].getUrl());
        builder.append(",  error: ").append(e.toString());
        LogService.getInstance().setMessage(e, builder.toString());
        return null;
      } 
      return loadArticles(index, collection, time + 1);
    }
  }

  public SearchResponse search(SearchResponse searcher)  {
    return search(0, searcher, 0);
  }
  
  public SearchResponse search(int index, SearchResponse searcher)  {
    return search(index, searcher, 0);
  }
  
  private SearchResponse search(int index, SearchResponse searcher, int time) {
    try {
      Header [] headers = new Header[] {
          new BasicHeader("action", "solr.search.remote")
      };

      String xml = Object2XML.getInstance().toXMLDocument(searcher).getTextValue();
      byte [] bytes = xml.getBytes(Application.CHARSET);
      bytes = clients[index].post(URLPath.SEARCH_HANDLER, bytes, headers);
      if(bytes.length < 2) return searcher;
      return XML2Object.getInstance().toObject(SearchResponse.class, bytes);
    } catch (Exception e) {
      if(time > 2) {
        StringBuilder builder = new StringBuilder();
        builder.append("search: ").append(searcher.getQuery().getPattern());
        builder.append(", time: ").append(time);
        builder.append(",  from: ").append(clients[index].getUrl());
        builder.append(",  error: ").append(e.toString());
        LogService.getInstance().setMessage(e, builder.toString());
        return null;
      } 
      return search(index, searcher, time + 1);
    }
  }

  public Article loadArticle(String metaId) {
    try {
      return load(Article.class, "solr.load.article.remote", metaId);
    } catch (Exception e) {
      LogService.getInstance().setMessage(e, e.toString());
    }
    return null;
  }

  public Meta loadMeta(String metaId) {
    try {
      return load(Meta.class, "solr.load.meta.remote", metaId);
    } catch (Exception e) {
      LogService.getInstance().setMessage(e, e.toString());
    }
    return null;
  }

  public Relations loadRelations(String metaId) {
    try {
      return load(Relations.class, "solr.load.relations.remote", metaId);
    } catch (Exception e) {
      LogService.getInstance().setMessage(e, e.toString());
    }
    return null;
  }

  public MetaRelations loadMetaRelations(String metaId) {
    try {
      return load(MetaRelations.class, "solr.load.meta.relations.remote", metaId);
    } catch (Exception e) {
      LogService.getInstance().setMessage(e, e.toString());
    }
    return new MetaRelations(metaId);
  }

  public <T> T load(Class<T> clazz, String action, String metaId) throws Exception {
    return load(0, clazz, action, metaId);
  }

  private <T> T load(int time, Class<T> clazz, String action, String metaId) throws Exception {
    try {
      Header [] headers = new Header[] {new BasicHeader("action", action)};
      byte [] bytes = metaId.getBytes();

      final CountDownLatch doneSignal = new CountDownLatch(clients.length);
      ExecutorService e = Executors.newFixedThreadPool(clients.length);
      WorkerRunnable workers [] = new WorkerRunnable[clients.length];
      for(int i = 0; i < workers.length; i++) {
        workers[i] = new WorkerRunnable(doneSignal, clients[i], headers, metaId);
        e.execute(workers[i]);
      }

      try {
        doneSignal.await(); // wait for all to finish
      } catch (InterruptedException ie) {
      }
      e.shutdown();

      for(int i = 0; i < workers.length; i++) {
        bytes = workers[i].getResponse();
        if(bytes == null || bytes.length < 2) continue;
        return XML2Object.getInstance().toObject(clazz, bytes);
      }
      return null;
    } catch (Exception e) {
      if(time >= 2) throw e;
      return load(time + 1, clazz, action, metaId);
    }
  }

  public Domain loadDomain(String id) {
    try {
      return load(Domain.class, "solr.load.domain.remote", id);
    } catch (Exception e) {
      LogService.getInstance().setMessage(e, e.toString());
    }
    return null;
  }

  class WorkerRunnable implements Runnable {

    private Header [] headers;
    private String metaId;
    private CountDownLatch doneSignal;
    private byte[] bytes;
    private DataClientService client;

    WorkerRunnable(CountDownLatch doneSignal, 
        DataClientService client, Header [] headers, String metaId) {
      this.doneSignal = doneSignal;
      this.metaId = metaId;
      this.headers = headers;
      this.client = client;
    }

    public void run() {
      try {
        bytes = client.post(URLPath.SEARCH_HANDLER, metaId.getBytes(), headers);
      } catch (Exception e) {
        LogService.getInstance().setMessage(e, e.toString());
      }
      doneSignal.countDown();
    }
    
    public byte[] getResponse() { return bytes; }
  }

  public DataClientService[] getClients() { return clients; }

  
}
