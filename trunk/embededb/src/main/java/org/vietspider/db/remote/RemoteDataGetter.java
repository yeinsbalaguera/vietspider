/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.db.remote;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.vietspider.bean.Article;
import org.vietspider.bean.Content;
import org.vietspider.bean.Domain;
import org.vietspider.bean.Image;
import org.vietspider.bean.Meta;
import org.vietspider.bean.Relation;
import org.vietspider.common.io.LogService;
import org.vietspider.db.SystemProperties;
import org.vietspider.db.content.ArticleDatabases;
import org.vietspider.db.content.BackupDatabase;
import org.vietspider.db.database.DatabaseReader;
import org.vietspider.db.database.DatabaseService;
import org.vietspider.db.database.MetaList;
import org.vietspider.db.idm2.EntryReader;
import org.vietspider.db.idm2.IEntryDomain;
import org.vietspider.db.idm2.SimpleEntryDomain;
import org.vietspider.db.sync.SyncService;
import org.vietspider.index.CommonSearchQuery;
import org.vietspider.index.SearchResponse;
import org.vietspider.index.SearchResultCollection;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Feb 8, 2007
 */
public class RemoteDataGetter implements DatabaseReader {

  public RemoteDataGetter() {
    SystemProperties.getInstance().putValue("database.backup", "true", false);
    DatabaseService.setMode(DatabaseService.REMOTE);
    try {
      SyncService.getInstance().createHandler(SyncArticleData.class);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
      System.exit(0);
    }
  }

  @Override
  public Article loadArticle(String id) throws Exception {
    ArticleDatabases backup = BackupDatabase.getInstance().getDatabase();
//    if(backup == null) {
//      return SyncService.getInstance().load(SyncArticleData.class, id, "load.article");
//    }
    return backup.loadArticle(id, Article.SEARCH);
  }

  @Override
  public List<Article> loadArticles(String[] metaIds) throws Exception {
    List<Article> articles = new ArrayList<Article>();
    for(int i = 0; i < metaIds.length; i++) {
      articles.add(loadArticle(metaIds[i]));
    }
    return articles;
  }

  public List<Article> loadArticles(String [] metaIds, short mode) throws Exception {
    List<Article> articles = new ArrayList<Article>();
    for(int i = 0; i < metaIds.length; i++) {
      articles.add(loadArticle(metaIds[i], mode));
    }
    return articles;
  }

  @Override
  public Content loadContent(String metaId) throws Exception {
    Article article = loadArticle(metaId);
    return article != null ? article.getContent() : null;
  }

  @Override
  public List<Content> loadContentForMining() throws Exception {
    return new ArrayList<Content>();
  }

  @Override
  public Image loadImage(String id) throws Exception {
    ArticleDatabases backup = BackupDatabase.getInstance().getDatabase();
//    if(backup == null) {
//      return SyncService.getInstance().load(SyncImageData.class, id, "load.image");
//    } 
    return backup.loadImage(id);
  }

  @Override
  public List<Image> loadImages(String metaId) throws Exception {
    List<Image> images = new ArrayList<Image>();
    int counter = 1;
    while(counter < 500) {
      String id = metaId + "." + String.valueOf(counter);
      Image image = loadImage(id);
      if(image == null) break;
      images.add(image);
      counter++;
    }
    return images;
  }

  @Override
  public Meta loadMeta(String id) throws Exception {
    ArticleDatabases backup = BackupDatabase.getInstance().getDatabase();
//    if(backup == null) {
//      return SyncService.getInstance().load(SyncArticleData.class, id, "load.meta");
//    }
    return backup.loadMeta(id);
    //    RemoteDataSetter setter = (RemoteDataSetter)DatabaseService.getSaver();
    //    if(setter.getBackup() != null) {
    //      Meta meta = setter.getBackup().loadMeta(id);
    //      if(meta != null) return meta;
    //    }

  }

  @Override
  public void loadMetaFromDomain(Domain domain, MetaList list) throws Exception {
  }

  @Override
  public List<Meta> loadMetaFromDomain(String id) throws Exception {
    Domain domain =  loadDomainById(id);
    List<Meta> metas = new ArrayList<Meta>();
    if(domain == null) return metas;

    IEntryDomain entryDomain = new SimpleEntryDomain(domain);

    MetaList metaList = new MetaList("vietspider");
    for(int page = 1; page <= 50; page++) {
      metaList.setCurrentPage(page);
      EntryReader entryReader = new EntryReader();
      entryReader.read(entryDomain, metaList, -1);
      List<Article> articles = metaList.getData();
      if(articles.size() < 1) break;
      for(int i = 0; i < articles.size(); i++) {
        if(articles.get(i) == null) continue;
        metas.add(articles.get(i).getMeta());
      }
    }
    return metas;
  }

  @SuppressWarnings("unchecked")
  public List<Relation> loadRelation(String metaId) throws Exception {
    return (List<Relation>)SyncService.getInstance().load(SyncArticleData.class, metaId, "load.relations");
//    ArticleDatabases backup = BackupDatabase.getInstance().getDatabase();
////    if(backup == null) {
////      return (List<Relation>)SyncService.getInstance().load(SyncArticleData.class, metaId, "load.relations");
////    }
//    return backup.loadArticle(metaId).getRelations();
  }


  public List<String> loadDateFromDomain() throws Exception {
    return new ArrayList<String>();
  }

  public Domain loadDomainById(String id) throws Exception {
    ArticleDatabases backup = BackupDatabase.getInstance().getDatabase();
//    if(backup == null) {
//      return SyncService.getInstance().load(SyncArticleData.class, id, "load.domain");
//    }
    return backup.loadDomain(id);

  }

  @Override
  public IArticleIterator getIterator(Date date, boolean create) {
    ArticleDatabases backup = BackupDatabase.getInstance().getDatabase();
    return backup.getDatabase(date, create, false).getIterator();
  }

  @Override
  public Article loadArticle(String id, short mode) throws Exception {
    //    RemoteDataSetter setter = (RemoteDataSetter)DatabaseService.getSaver();
    //    ArticleDatabases bak = setter.getBackup(); 
    //    if(bak != null) {
    //      if(mode == Article.META
    //          || mode == Article.SIMPLE) return bak.loadArticle(id);
    //    }
    ArticleDatabases backup = BackupDatabase.getInstance().getDatabase();
//    if(backup == null) {
//      Header header = new BasicHeader("mode", String.valueOf(mode));
//      return SyncService.getInstance().load(SyncArticleData.class, id, "load.article", header);
//    }
    return backup.loadArticle(id);
  }
  
  
  public String loadRawText(String id) throws Exception {
    ArticleDatabases backup = BackupDatabase.getInstance().getDatabase();
    return backup.loadRawText(id);
  }
  
  public String loadMetaAsRawText(String id) throws Exception {
    ArticleDatabases backup = BackupDatabase.getInstance().getDatabase();
    return backup.loadMetaAsRawText(id);
  }

  @Override
  public String loadIdByURL(String url) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SearchResponse search(SearchResponse searcher) {
    // TODO Auto-generated method stub
    return null;
  }

  public String searchForCached(SearchResponse searcher) { return null; }

  @Override
  public void search(MetaList metas, CommonSearchQuery query) {
    // TODO Auto-generated method stub
  }

  @Override
  public String searchIdByURL(String dateValue, String url) {
    // TODO Auto-generated method stub
    return null;
  }

  public String loadArticleForSearch(SearchResultCollection collection) { 
    return null; 
  }

}
