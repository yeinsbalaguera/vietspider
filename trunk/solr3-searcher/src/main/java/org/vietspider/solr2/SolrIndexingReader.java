/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.solr2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.vietspider.bean.Article;
import org.vietspider.common.io.LogService;
import org.vietspider.index.CommonSearchQuery;
import org.vietspider.index.SearchData;
import org.vietspider.index.SearchResponse;
import org.vietspider.index.SearchResultCollection;
import org.vietspider.solr2.highlight.TextHighlighter;
import org.vietspider.solr2.search.QueryUtils;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 23, 2010  
 */
public class SolrIndexingReader implements ISolrIndexingSearcher {

  private EmbeddedSolrServer server;
  private SolrIndexStorage storage;
  
  private MemoryDuplicateDetector duplicateDetector;

  //  private ArticleLoader articleLoader;

  SolrIndexingReader(EmbeddedSolrServer server, 
      SolrIndexStorage storage, SolrIndexingWriter writer) throws Exception  {
    this.server = server;
    this.storage = storage;
    
    this.duplicateDetector = new MemoryDuplicateDetector(writer);

    //    this.articleLoader = new ArticleLoader(databases);
  }

  @Override
  public SearchResponse search(SearchResponse searcher) {
    CommonSearchQuery baseQuery = searcher.getQuery();

    SolrQuery query = QueryUtils.createQuery(baseQuery, searcher.getStart());

    QueryResponse response = search(query);
    
    if(response == null) return searcher;

    searcher.setTotal(response.getResults().getNumFound());
    searcher.setTime(response.getElapsedTime());


    //    StringBuilder builder = new StringBuilder();
    //    builder.append(searchQuery.getCode()).append('.').append(searcher.getStart());
    //    LocalSearchResult cachedResult = new LocalSearchResult(builder.toString());
    //    System.out.println(" chuan bi search "+ searchQuery.getPattern());

    TextHighlighter highlighter = new TextHighlighter(baseQuery);
    
    NlpPresentationBuilder nlpBuilder = new NlpPresentationBuilder(baseQuery);

    loadArticles(highlighter, nlpBuilder, searcher, response);


    //    cachedResult.setTotal(response.getResults().getNumFound());
    //    LocalCachedResults.getInstance().put(cachedResult);

    return searcher;
  }

  void loadArticles(TextHighlighter highlighter, 
      NlpPresentationBuilder nlpBuilder, SearchData data, QueryResponse response) {
    if(data.total() >= data.getSize()) return;

    SolrDocumentList results = response.getResults();
    
    //    System.out.println("found " + results.getNumFound());

    Iterator<SolrDocument> iter = results.iterator();
    int counter = 0;

    HashMap<String, Float> scores = new HashMap<String, Float>();
    List<String> ids = new ArrayList<String>();
    while (iter.hasNext()) {
      SolrDocument resultDoc = iter.next();
      String metaId = (String)resultDoc.getFieldValue("id");
      //      System.out.println(metaId);
      if(metaId == null) continue;
      ids.add(metaId);

      Float score = (Float) resultDoc.getFieldValue("score");
      //      System.out.println(" ===== > "+ score);
      if(score == null) score = new Float(0.0f);
      scores.put(metaId, score);

      counter++;
      if(data.total() >= data.getSize()) break;
    }

    List<Article> articles = storage.loadArticles(ids);
    duplicateDetector.compute(scores, articles);
    
    for(Article article : articles) {
      article.setScore(scores.get(article.getId()));
      
      if(article.getMeta() == null || article.getMeta().getTitle() == null) continue;
      if(article.getContent() == null || article.getContent().getContent() == null) continue;

      try {
        highlighter.highlight(article);
      } catch (Exception e) {
        LogService.getInstance().setMessage(e, e.toString());
      }
      
      nlpBuilder.buildActionObject(article);
      nlpBuilder.buildArea(article);
      nlpBuilder.buildPrice(article);
      nlpBuilder.buildAddress(article);

      //    Meta meta = article.getMeta();
      //    Map<String, Map<String, List<String>>> map = response.getHighlighting();
      //    String[] hlelements = loadHighlighting(metaId, map);
      //    if(hlelements[0] != null && !hlelements[0].trim().isEmpty()) meta.setTitle(hlelements[0]);
      //    if(hlelements[1] != null && !hlelements[1].trim().isEmpty()) meta.setDesc(hlelements[1]);

      data.addArticle(article);
    }
  }
  
  /*private void setPrice(Map<Short, Collection<?>> nlpQuery, Article article) {
    Collection<?> collection = nlpQuery.get(NLPData.PRICE_MONTH);
    if(collection != null && collection.size() > 0) {
      NLPRecord nlpRecord = article.getNlpRecord();
      List<String> list = nlpRecord.getData(NLPData.PRICE_MONTH);
    }
  }*/

  public QueryResponse search(SolrQuery query) {
    try {
      return server.query(query);
    } catch (Exception e) {
      LogService.getInstance().setThrowable("SOLR READER", e, e.toString());
    }
    return null;
  }

  @SuppressWarnings("unused")
  public SearchResultCollection searchForCached(SearchResponse searcher, int index) {
    return null;
  }
  @SuppressWarnings("unused")
  public SearchResultCollection loadArticles(SearchResultCollection collection, int index) {
    // TODO Auto-generated method stub
    return null;
  }


}
