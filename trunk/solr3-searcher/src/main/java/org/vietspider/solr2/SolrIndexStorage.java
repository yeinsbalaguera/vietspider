/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.solr2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.vietspider.bean.Article;
import org.vietspider.bean.Domain;
import org.vietspider.bean.Image;
import org.vietspider.bean.Meta;
import org.vietspider.bean.MetaRelation;
import org.vietspider.bean.Relation;
import org.vietspider.bean.Relations;
import org.vietspider.bean.SolrIndex;
import org.vietspider.common.io.LogService;
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
public class SolrIndexStorage extends SolrStorage {

  private SolrDataIO solr;
  //  private SolrNlpComputor2 nlpComputor;

  protected volatile java.util.Queue<Article> tempArticles = new ConcurrentLinkedQueue<Article>();

  public SolrIndexStorage() throws Exception {
    super();

    solr = new SolrDataIO(this);

    DatabaseService.setMode(DatabaseService.SEARCH);
    
    //    nlpComputor = new SolrNlpComputor2(this);

//    if("81".equals(SystemProperties.getInstance().getValue("web.port"))) {
//      new InitialAutoIndexing().start();
//    }

    this.start();
  }

  public SolrDataIO getSolr() { return solr; }

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
          SolrIndex index = solr.createSolrIndex(article);
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

  public void exit() {
    solr.close();
  }

  @SuppressWarnings("unused")
  public boolean contains(long id) { return false; }
  @SuppressWarnings("unused")
  public void delete(long id) { }
  @SuppressWarnings("unused")
  public boolean isDeleting(long id) { return false; }
  @SuppressWarnings("unused")
  public boolean isDelete(long id) throws Throwable {
    return false;
  }

  public Article loadArticle(String metaId) {
    if(rsolrDatabase != null) {
      Article article = rsolrDatabase.loadArticle(metaId);
      if(article != null) return article;
    }
    return rcrawler.loadArticle(metaId);
  }

  public List<Article> loadArticles(List<String> ids) {
    //    if(rsolrDatabase != null) {
    //      Article article = rsolrDatabase.loadArticle(metaId);
    //      if(article != null) return article;
    //    }
    return rcrawler.loadArticles(ids);
  }

  public Domain loadDomain(String id) { return rsolrDatabase.loadDomain(id); }
  public Domain loadDomain(long id) { return rsolrDatabase.loadDomain(String.valueOf(id)); }
  @SuppressWarnings("unused")
  public Image loadImage(String id) { return null; }

  public Meta loadMeta(String metaId) {
    if(rsolrDatabase != null)  {
      return rsolrDatabase.loadMeta(metaId);
    }
    return null; 
  }

  public List<MetaRelation> loadMetaRelation(long id) throws Throwable {
    if(rsolrDatabase != null)  {
      return rsolrDatabase.loadMetaRelations(String.valueOf(id)).getMetaRelations();
    }
    return new ArrayList<MetaRelation>();
  }

  public Meta loadRelMeta(long relId)  {
    Meta meta = rsolrDatabase.loadMeta(String.valueOf(relId));
    if(meta != null) return meta;
    if(rsolrDatabase != null)  {
      return rsolrDatabase.loadMeta(String.valueOf(relId));
    }
    return null;
  }

  @Override
  public Relations loadRelations(String metaId) throws Throwable {
    Article article = rsolrDatabase.loadArticle(metaId);
    if(article == null) return null;

    Relations relations = new Relations(metaId);
    relations.setRelations(article.getRelations());
    return relations;
  }

  @SuppressWarnings("unused")
  public void save(List<Relation> relations) {
  }
  @SuppressWarnings("unused")
  public String loadIdByURL(String url) { return null; }

  //main search
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
    if(article == null) return;
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
