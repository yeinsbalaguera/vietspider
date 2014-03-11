/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.solr2;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.vietspider.bean.Article;
import org.vietspider.bean.Domain;
import org.vietspider.bean.Image;
import org.vietspider.bean.Meta;
import org.vietspider.bean.MetaRelation;
import org.vietspider.bean.Relation;
import org.vietspider.bean.Relations;
import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.text.CalendarUtils;
import org.vietspider.db.CrawlerConfig;
import org.vietspider.db.SystemProperties;
import org.vietspider.db.content.ArticleDatabases;
import org.vietspider.db.content.IArticleDatabase;
import org.vietspider.db.content.IDatabases;
import org.vietspider.db.database.DatabaseService;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 29, 2009  
 */
public abstract class SolrStorage extends IDatabases {

  private volatile boolean execute = true; 

  protected ArticleDatabases backup;
  
  protected RemoteCrawlerLoader rcrawler;
  
  protected RSolrDatabaseReader rsolrDatabase;

  public SolrStorage() throws Exception {
    super(DatabaseService.SEARCH);

    Application.addShutdown(new Application.IShutdown() {
      public String getMessage() { return "Close Solr Storage Database";}
      public void execute() {
        execute = false; 
        exit();
      }
    });

    CrawlerConfig.INDEX_CONTENT = false;
    
    if("true".equals(SystemProperties.getInstance().getValue("database.backup"))) {
      backup = new ArticleDatabases(true);
    }
    
    rcrawler = new RemoteCrawlerLoader();
    
    String remote = SystemProperties.getInstance().getValue("solr.remote.database");
    if(remote != null && !(remote = remote.trim()).isEmpty()) {
      rsolrDatabase = new RSolrDatabaseReader(remote);
      //      database.setRdatabase(rsolrDatabase);
    }
  }

  public abstract void deleteByQuery(SolrQuery query) throws Exception ;

  public void run() {
    while(execute) {
      commit() ;
      try {
        Thread.sleep(SLEEP);
      } catch (Exception e) {
      }
    }
  }
  
  public abstract void commit() ;
  public abstract void exit();

  public abstract boolean contains(long id) ;

  public abstract void delete(long id) ;
  
  public abstract boolean isDeleting(long id) ;

  public abstract boolean isDelete(long id) throws Throwable;
  
  public abstract Article loadArticle(String metaId);
  
  public abstract Domain loadDomain(String id);
  
  public abstract Domain loadDomain(long id);
  
  public Domain loadRemoteDomain(long id) {
    try {
      if(rsolrDatabase == null) return null;
      return rsolrDatabase.loadDomain(String.valueOf(id));
    } catch (Throwable e) {
      LogService.getInstance().setThrowable(e);
    }
    return null;
  }
  
  public abstract Image loadImage(String id) ;
  public abstract Meta loadMeta(String metaId);

  public abstract List<MetaRelation> loadMetaRelation(long id) throws Throwable ;

  public abstract Relations loadRelations(String metaId) throws Throwable;

  @SuppressWarnings("unused")
  public void deleteExpireDate(File folder, int expire) {
  }

  @SuppressWarnings("unused")
  public IArticleDatabase getDatabase(Date date, boolean create, boolean make) { return null; }
  @SuppressWarnings("unused")
  public IArticleDatabase getDatabase(String id, boolean create, boolean make) { return null; }
  @SuppressWarnings("unused")
  public IArticleDatabase getDatabase(File folder, boolean create) { return null; }
  @SuppressWarnings("unused")
  public IArticleDatabase getDatabase(String id, boolean create) { return null;  }
  public int getMaxSync() { return 0; }
  public File getRoot() { return null; }

  @SuppressWarnings("unused")
  public Article loadArticle(String id, short dbtype) { return loadArticle(id); }
  
  public abstract String loadIdByURL(String url) ;

  public abstract QueryResponse search(SolrQuery query) ;
  
  public boolean isSearcher() { return true; }

  public long getLastAccess() { return System.currentTimeMillis(); }
  
  public boolean isClose() { return false; }
  
  public abstract void save(Article article);
  
  protected List<MetaRelation> toMetaRelations(Relations relations) {
    List<MetaRelation> values = new ArrayList<MetaRelation>();
    List<Relation> list = relations.getRelations();
    for(int i = 0; i < list.size(); i++) {
      long relId = Long.parseLong(list.get(i).getRelation());
      Meta meta = loadRelMeta(relId);
      if(meta  == null) continue;
      //      try {
      //        article = database.load(relId);
      //      } catch (Exception e) {
      //        LogService.getInstance().setMessage(e, e.toString());
      //      }
      //      if(article == null) continue;
      //      Meta meta = SolrArticleUtils.toMeta(article);

      MetaRelation metaRel = new MetaRelation();
      metaRel.setId(list.get(i).getRelation());
      metaRel.setTitle(meta.getTitle());
      metaRel.setDes(meta.getDesc());
      metaRel.setImage(meta.getImage());
      metaRel.setTime(meta.getTime());
      metaRel.setSource(meta.getSource());
      try {
        SimpleDateFormat dateTimeFormat = CalendarUtils.getDateTimeFormat();
        SimpleDateFormat dateFormat = CalendarUtils.getDateFormat();
        metaRel.setDate(dateFormat.format(dateTimeFormat.parse(meta.getTime())));
      } catch (Exception e) {
      }

      try {
        Domain domain = loadDomain(Long.parseLong(meta.getDomain()));
        if(domain == null) {
          domain = loadRemoteDomain(Long.parseLong(meta.getDomain()));
        }
        metaRel.setName(domain.getName());
      } catch (Exception e) {
      }
      metaRel.setPercent(list.get(i).getPercent());
      values.add(metaRel);
    }
    return values;
  }
  
  public abstract Meta loadRelMeta(long relId);
}
