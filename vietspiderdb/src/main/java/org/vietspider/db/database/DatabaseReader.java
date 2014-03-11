/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.db.database;

import java.util.Date;
import java.util.List;

import org.vietspider.bean.Article;
import org.vietspider.bean.Content;
import org.vietspider.bean.Domain;
import org.vietspider.bean.Image;
import org.vietspider.bean.Meta;
import org.vietspider.bean.Relation;
import org.vietspider.index.CommonSearchQuery;
import org.vietspider.index.SearchResponse;
import org.vietspider.index.SearchResultCollection;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Feb 8, 2007
 */
public interface DatabaseReader {
  
  public List<String> loadDateFromDomain() throws Exception ;  
  
  public Domain loadDomainById(String id) throws Exception ;
  
  public Article loadArticle(String id) throws Exception;
  
  public String loadRawText(String id) throws Exception;
  
  public Article loadArticle(String id, short mode) throws Exception;
  
//  public Article loadShortArticle(String id) throws Exception;
  public String loadMetaAsRawText(String id) throws Exception;
  
  public List<Article> loadArticles(String [] metaIds) throws Exception;
  
  public List<Article> loadArticles(String [] metaIds, short mode) throws Exception;
  
  public void loadMetaFromDomain(Domain domain, MetaList list) throws Exception ;
  
  public List<Meta> loadMetaFromDomain(String id) throws Exception ;
  
  public List<Content> loadContentForMining() throws Exception;
  
  public Content loadContent(String metaId) throws Exception;
  
//  public int countMetaFromDomain(Domain domain) throws Exception ;
  
  public List<Relation> loadRelation(String metaId) throws Exception;
  
  public Image loadImage(String id) throws Exception ;
  
  public Meta loadMeta(String id) throws Exception;
  
  public List<Image> loadImages(String metaId) throws Exception ;
  
  public IArticleIterator getIterator(Date date, boolean create) ;
  
  public interface IArticleIterator {

    public boolean hasNext() ;

    public Article next(short type) throws Throwable ;
    
    public void remove() ;
    
  }
  
  public void search(MetaList metas, CommonSearchQuery query);
  
  public SearchResponse search(SearchResponse searcher);
  
  public String searchForCached(SearchResponse searcher);
  
  public String loadArticleForSearch(SearchResultCollection collection);
  
  public String loadIdByURL(String url) ;
  
  public String searchIdByURL(String dateValue, String url) ;
  
}
