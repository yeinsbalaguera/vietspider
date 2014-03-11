/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.article.index;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.vietspider.bean.Article;
import org.vietspider.bean.ArticleIndex;
import org.vietspider.bean.Domain;
import org.vietspider.bean.Image;
import org.vietspider.bean.Meta;
import org.vietspider.bean.MetaRelation;
import org.vietspider.bean.Relation;
import org.vietspider.bean.Relations;
import org.vietspider.common.io.LogService;
import org.vietspider.db.SystemProperties;
import org.vietspider.db.content.ArticleDatabases;
import org.vietspider.db.database.DatabaseService;
import org.vietspider.db.database.MetaList;
import org.vietspider.index.CommonSearchQuery;
import org.vietspider.index.SearchResponse;
import org.vietspider.index.SearchResultCollection;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 10, 2011  
 */
public class ArticleIndexStorage extends ArticleIndexer {
  
  private volatile static ArticleIndexStorage INSTANCE;
  
  public final static synchronized ArticleIndexStorage getInstance() {
    if(INSTANCE != null) return INSTANCE;
    try {
      INSTANCE = new ArticleIndexStorage();
      return INSTANCE;
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
      return null;
    }

  }

  private ArticleIndexIO solr;
  private boolean enable = false;
  //  private SolrNlpComputor2 nlpComputor;

  protected volatile java.util.Queue<Article> tempArticles = new ConcurrentLinkedQueue<Article>();

  public ArticleIndexStorage() throws Exception {
    super();

    solr = new ArticleIndexIO(this);
    SystemProperties system = SystemProperties.getInstance();
    enable = !"false".equalsIgnoreCase(system.getValue("article.index"));
    if(!enable) return;

    this.start();
  }

  public ArticleIndexIO getSolr() { return solr; }

  public ArticleDatabases getDatabases() { return null; }
  @SuppressWarnings("unused")
  public void deleteByQuery(SolrQuery query) throws Exception {
  }

  public void commit() {
    try {
      while(!tempArticles.isEmpty()) {
        Article article = tempArticles.poll();

        if(article.getStatus() == Article.DELETE) {
          solr.getWriter().delete(article.getId());
          //          System.out.println(" chuan bi delete "+ article.getId());
          continue;
        }
        try{
          ArticleIndex index = solr.createSolrIndex(article);
          if(index != null) solr.getWriter().save(index);
        } catch (Exception e) {
          LogService.getInstance().setThrowable(e);
        }
      }

    } catch (Throwable e) {
      LogService.getInstance().setThrowable(e);
    }
    //    System.out.println(" temp "+ tempArticles.size() + " : "+ tempImages.size());
  }

  public void exit() { solr.close(); }

  @SuppressWarnings("unused")
  public boolean contains(long id) { return false; }
  public void delete(long id) { 
    solr.getWriter().delete(String.valueOf(id));
  }
  public void delete(String id) { 
    solr.getWriter().delete(id);
  }
  public void add(ArticleIndex index) { 
    solr.getWriter().save(index);
  }
  @SuppressWarnings("unused")
  public boolean isDeleting(long id) { return false; }
  @SuppressWarnings("unused")
  public boolean isDelete(long id) throws Throwable { return false; }
  
  public ArticleIndex loadArticleIndex(String metaId) {
    try {
      Article article = DatabaseService.getLoader().loadArticle(metaId);
      if(article == null) return null;
      return solr.createSolrIndex(article);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
      return null;
    }
  }

  public Article loadArticle(String metaId) {
    try {
      return DatabaseService.getLoader().loadArticle(metaId);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
      return null;
    }
  }

  public List<Article> loadArticles(List<String> ids) {
    try {
      return DatabaseService.getLoader().loadArticles(ids.toArray(new String[0]));
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
      return null;
    }
  }

  public Domain loadDomain(String id) { 
    try {
      return DatabaseService.getLoader().loadDomainById(id);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
      return null;
    }
  }
  public Domain loadDomain(long id) {
    try {
      return DatabaseService.getLoader().loadDomainById(String.valueOf(id));
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
      return null;
    }
  }
  @SuppressWarnings("unused")
  public Image loadImage(String id) { return null; }

  public Meta loadMeta(String metaId) {
    try {
      return DatabaseService.getLoader().loadMeta(metaId);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
      return null;
    }
  }

  @SuppressWarnings("unused")
  public List<MetaRelation> loadMetaRelation(long id) throws Throwable {
    //    try {
    //      return DatabaseService.getLoader().load
    //    } catch (Exception e) {
    //      LogService.getInstance().setThrowable(e);
    //    }
    return new ArrayList<MetaRelation>();
  }

  public Meta loadRelMeta(long relId)  {
    try {
      return DatabaseService.getLoader().loadMeta(String.valueOf(relId));
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
      return null;
    }
  }

  @Override
  public Relations loadRelations(String metaId) throws Throwable {
    Relations relations = new Relations(metaId);
    try {
      relations.setRelations(DatabaseService.getLoader().loadRelation(metaId));
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    return relations;
  }

  @SuppressWarnings("unused")
  public void save(List<Relation> relations) {
  }
  @SuppressWarnings("unused")
  public String loadIdByURL(String url) { return null; }

  public void search(MetaList metas, CommonSearchQuery query) {
    int loadPage  = metas.getCurrentPage();
    int pageSize = metas.getPageSize();

    //    System.out.println("==== >"+query.getPattern());

    int start = (loadPage - 1)*pageSize;

    SearchResponse searcher = new SearchResponse();
    searcher.setSize(metas.getPageSize());
    searcher.setQuery(query);
    searcher.setStart(start);

    //    String pattern = query.getPattern();
    //    System.out.println(" ===  >"+ pattern +  " : "+ pattern.startsWith("field:phone:"));
    //    if(pattern.startsWith("field:phone:")) {
    //      nlpComputor.searchMobile(searcher);
    //    } else {
    searcher = solr.getReader().search(searcher);
    //    }

    query.setTime(searcher.getTime());

    long total  = searcher.getTotal();
    metas.setTotalData(total);
    if(total%metas.getPageSize() == 0) {
      metas.setTotalPage((int)total/metas.getPageSize());
    } else {
      metas.setTotalPage((int)total/metas.getPageSize() + 1);
    }

    metas.getData().addAll(searcher.getArticles());
  }
  @SuppressWarnings("unused")
  public QueryResponse search(SolrQuery query) {
    return null;
  }
  @SuppressWarnings("unused")
  public SearchResponse search(SearchResponse searchResponse) {
    return null;
  }
  @SuppressWarnings("unused")
  public String searchForCached(SearchResponse searchResponse) {
    return null;
  }
  @SuppressWarnings("unused")
  public String loadArticleForSearch(SearchResultCollection collection) {
    return null;
  }

  public void save(Article article) {
    if(article == null || !enable) return;
    //    if(article.getStatus() == Article.USER) {
    //      UserDatabases.getInstance().save(article);
    //    }
    //    System.out.println(" chuan bi save "+ article.getContent().getContent());
    //    IDVerifiedStorages.save("solr_add", article);
    tempArticles.add(article);
  }

  @SuppressWarnings("unused")
  public void save(Image image) {  
  }

  public Article loadMetaData(String id) { return null; }

}
