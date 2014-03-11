/***************************************************************************
 * Copyright 2001-2012 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db2.content;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import org.vietspider.bean.Article;
import org.vietspider.bean.Domain;
import org.vietspider.bean.Image;
import org.vietspider.bean.Meta;
import org.vietspider.db.content.ArticleDatabases;
import org.vietspider.db.content.IArticleDatabase;
import org.vietspider.db.content.IDatabases;
import org.vietspider.db.database.MetaList;
import org.vietspider.index.CommonSearchQuery;
import org.vietspider.index.SearchResponse;
import org.vietspider.index.SearchResultCollection;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 31, 2012  
 */
public class MigratedDatabases extends IDatabases {

  private ArticleBabuDatabases babu;
  private ArticleDatabases backup;

  public MigratedDatabases() {
    babu = new ArticleBabuDatabases(true);
    backup = new ArticleDatabases(true);
  }


  @Override
  public void save(Article article) {
    babu.save(article);
  }

  @Override
  public void save(Image image) {
    babu.save(image);

  }

  @Override
  public Domain loadDomain(String domainId) {
    Domain domain = babu.loadDomain(domainId);
    if(domain != null) return domain;
    return backup.loadDomain(domainId);
  }

  @Override
  public Article loadArticle(String id) {
    Article article = babu.loadArticle(id);
//    System.out.println(" da cahy vads day 1 "+ id + " : "+ article);
    if(article != null) return article;
    return backup.loadArticle(id);
  }

  @Override
  public Article loadArticle(String id, short dbtype) {
    Article article = babu.loadArticle(id, dbtype);
    if(article != null) return article;
    return backup.loadArticle(id, dbtype);
  }

  @Override
  public Image loadImage(String id) {
    Image image = babu.loadImage(id);
    if(image != null) return image;
    return backup.loadImage(id);
  }

  @Override
  public String loadIdByURL(String url) {
    String id  = babu.loadIdByURL(url);
    if(id != null) return id;
    return backup.loadIdByURL(url);
  }

  @Override
  public Meta loadMeta(String id) {
    Meta meta = babu.loadMeta(id);
    if(meta != null) return meta;
    return backup.loadMeta(id);
  }

  @Override
  public IArticleDatabase getDatabase(Date date, boolean create, boolean make) {
    return babu.getDatabase(date, create, make);
  }

  @Override
  public IArticleDatabase getDatabase(String id, boolean create, boolean make) {
    return babu.getDatabase(id, create, make);
  }

  @Override
  public void deleteExpireDate(File folder, int expire) {
    babu.deleteExpireDate(folder, expire);
  }

  @Override
  public int getMaxSync() { return babu.getMaxSync(); }

  @Override
  public File getRoot() { return babu.getRoot(); }

  @Override
  public void search(MetaList metas, CommonSearchQuery query) {
   babu.search(metas, query);
   if(metas.getData().size() >= 10) return;
   backup.search(metas, query);
  }

  @Override
  public Article loadMetaData(String id) {
    Article article = babu.loadMetaData(id);
    if(article != null) return article;
    return backup.loadMetaData(id);
  }
  
  public String loadRawText(String id)  {
    String text = babu.loadRawText(id);
    if(text != null) return text;
    return backup.loadRawText(id);
  }
  
  public String loadMetaAsRawText(String id) {
    String text = babu.loadMetaAsRawText(id);
    if(text != null) return text;
    return backup.loadMetaAsRawText(id);
  }

  @Override
  public SearchResponse search(SearchResponse searcher) {
    return null;
  }

  @Override
  public String searchForCached(SearchResponse searcher) {
    return null;
  }

  @Override
  public String loadArticleForSearch(SearchResultCollection collection) {
   return null;
  }



}
