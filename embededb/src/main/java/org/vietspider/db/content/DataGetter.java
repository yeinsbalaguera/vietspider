/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.db.content;

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
import org.vietspider.db.database.DatabaseReader;
import org.vietspider.db.database.MetaList;
import org.vietspider.db.idm2.EntryReader;
import org.vietspider.db.idm2.IEntryDomain;
import org.vietspider.db.idm2.SimpleEntryDomain;
import org.vietspider.index.CommonSearchQuery;
import org.vietspider.index.SearchResponse;
import org.vietspider.index.SearchResultCollection;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Feb 8, 2007
 */
public class DataGetter implements DatabaseReader {
  
  public DataGetter() {
    IDatabases.getInstance();
  }

  @Override
  public Article loadArticle(String id) throws Exception {
    return IDatabases.getInstance().loadArticle(id, Article.NORMAL);
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
    Article article = IDatabases.getInstance().loadArticle(metaId, Article.NORMAL);
    return article.getContent();
  }

  @Override
  public List<Content> loadContentForMining() throws Exception {
    return new ArrayList<Content>();
  }

  @Override
  public Image loadImage(String id) throws Exception {
    return IDatabases.getInstance().loadImage(id);
  }

  @Override
  public List<Image> loadImages(String metaId) throws Exception {
    List<Image> images = new ArrayList<Image>();
    int counter = 1;
    while(counter < 500) {
      String id = metaId + "." + String.valueOf(counter);
      Image image = IDatabases.getInstance().loadImage(id);
      if(image == null) break;
      images.add(image);
      counter++;
    }
    return images;
  }

  @Override
  public Meta loadMeta(String id) throws Exception {
    Article article = IDatabases.getInstance().loadArticle(id, Article.META);
    if(article == null) return null;
    return article.getMeta();
  }

  @Override
  public void loadMetaFromDomain(Domain domain, MetaList list) throws Exception {
  }

  @Override
  public List<Meta> loadMetaFromDomain(String id) throws Exception {
    Domain domain = IDatabases.getInstance().loadDomain(id);
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

  public List<Relation> loadRelation(String metaId) throws Exception {
    Article article = IDatabases.getInstance().loadArticle(metaId, Article.NORMAL);
    return article.getRelations();
  }


  public List<String> loadDateFromDomain() throws Exception {
    return new ArrayList<String>();
  }

  public Domain loadDomainById(String id) throws Exception {
    return IDatabases.getInstance().loadDomain(id);
  }

  public IArticleIterator getIterator(Date date, boolean create) {
    return IDatabases.getInstance().getDatabase(date, create, false).getIterator();
  }

  @Override
  public Article loadArticle(String id, short mode) throws Exception {
    return IDatabases.getInstance().loadArticle(id, mode);
  }

  @Override
  public String loadIdByURL(String url) {
    return IDatabases.getInstance().loadIdByURL(url);
  }

  @Override
  public SearchResponse search(SearchResponse searcher) {
    return IDatabases.getInstance().search(searcher);
  }

  @Override
  public void search(MetaList metas, CommonSearchQuery query) {
    IDatabases.getInstance().search(metas, query);
  }

  @Override
  public String searchIdByURL(String dateValue, String url) {
    try {
      return ArticleDatabaseUtils.searchIdByURL(dateValue, url);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
      return null;
    }
  }
  
  public String searchForCached(SearchResponse searcher) {
    return IDatabases.getInstance().searchForCached(searcher);
  }

  public String loadArticleForSearch(SearchResultCollection collection) { 
    return IDatabases.getInstance().loadArticleForSearch(collection); 
  }

  public String loadRawText(String id) throws Exception {
    return "not support";
  }
  
  public String loadMetaAsRawText(String id) throws Exception {
    return "not support";
  }
}
