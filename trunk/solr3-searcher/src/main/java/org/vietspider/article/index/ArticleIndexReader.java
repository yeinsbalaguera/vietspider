/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.article.index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.vietspider.bean.Article;
import org.vietspider.bean.Meta;
import org.vietspider.common.io.LogService;
import org.vietspider.index.CommonSearchQuery;
import org.vietspider.index.SearchData;
import org.vietspider.index.SearchResponse;
import org.vietspider.index.SearchResultCollection;
import org.vietspider.solr2.ISolrIndexingSearcher;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 23, 2010  
 */
public class ArticleIndexReader implements ISolrIndexingSearcher {

  private EmbeddedSolrServer server;
  private ArticleIndexStorage storage;

  //  private ArticleLoader articleLoader;
  private ArticleIndexWriter writer;
  
  private Detector detector ;

  ArticleIndexReader(EmbeddedSolrServer server, 
      ArticleIndexStorage storage, ArticleIndexWriter writer) throws Exception  {
    this.server = server;
    this.storage = storage;
    this.writer = writer;
    try {
      detector = DetectorFactory.getInstance().create();
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    //    this.articleLoader = new ArticleLoader(databases);
  }

  @Override
  public SearchResponse search(SearchResponse searcher) {
    CommonSearchQuery baseQuery = searcher.getQuery();

    SolrQuery query = ArticleQueryUtils.createQuery(baseQuery, searcher.getStart());
    
    if(query == null) return searcher;

    QueryResponse response = search(query);
    
    if(response == null) return searcher;

    searcher.setTotal(response.getResults().getNumFound());
    searcher.setTime(response.getElapsedTime());


    //    StringBuilder builder = new StringBuilder();
    //    builder.append(searchQuery.getCode()).append('.').append(searcher.getStart());
    //    LocalSearchResult cachedResult = new LocalSearchResult(builder.toString());
    //    System.out.println(" chuan bi search "+ searchQuery.getPattern());
    
    ArticleTextHighlighter highlighter = null;
    if(baseQuery.getPattern() != null 
        && baseQuery.getPattern().trim().length() > 0
        && !"no".equals(baseQuery.getHighlightStart())) {
      highlighter = new ArticleTextHighlighter(baseQuery);
    }
    
    loadArticles(highlighter, searcher, response);
    

    //    cachedResult.setTotal(response.getResults().getNumFound());
    //    LocalCachedResults.getInstance().put(cachedResult);

    return searcher;
  }

  void loadArticles(ArticleTextHighlighter highlighter, 
       SearchData data, QueryResponse response) {
    if(data.total() >= data.getSize()) return;

    SolrDocumentList results = response.getResults();
    
    //    System.out.println("found " + results.getNumFound());

    Iterator<SolrDocument> iter = results.iterator();
//    int counter = 0;

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
      
//      counter++;
      if(data.total() >= data.getSize()) break;
    }

    List<Article> articles = storage.loadArticles(ids);
    
    for(Article article : articles) {
      if(article == null) continue;
      ids.remove(article.getId());
      article.setScore(scores.get(article.getId()));
      
      if(article.getMeta() == null || article.getMeta().getTitle() == null) continue;
      if(article.getContent() == null || article.getContent().getContent() == null) continue;
      
      Meta meta = article.getMeta();
      try {
        String lang = null;
        if(detector != null) lang = detector.detect(meta.getDesc());
        if("vi".equals(lang) && highlighter != null) {
          highlighter.highlight(article);
        } else {
          Map<String, Map<String, List<String>>> map = response.getHighlighting();
          String[] hlelements = loadHighlighting(meta.getId(), map);
//          System.out.println(" map la "+ map);
//          System.out.println(meta.getId() + " bebe : " + hlelements);
//          System.out.println("co 1 " + hlelements[0]);
//          System.out.println("co 2 " + hlelements[1]);
          if(hlelements[0] != null && !hlelements[0].trim().isEmpty()) {
            meta.putProperty("hl.title", hlelements[0]);
          }
          if(hlelements[1] != null && !hlelements[1].trim().isEmpty()) {
            meta.putProperty("hl.desc", hlelements[1]);
          }
        }
      } catch (Exception e) {
        LogService.getInstance().setMessage(e, e.toString());
      }
       
      //    Meta meta = article.getMeta();
      //    Map<String, Map<String, List<String>>> map = response.getHighlighting();
      //    String[] hlelements = loadHighlighting(metaId, map);
      //    if(hlelements[0] != null && !hlelements[0].trim().isEmpty()) meta.setTitle(hlelements[0]);
      //    if(hlelements[1] != null && !hlelements[1].trim().isEmpty()) meta.setDesc(hlelements[1]);

      data.addArticle(article);
    }
    
    for(int i = 0; i < ids.size(); i++) {
      writer.delete(ids.get(i));
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

  private String[] loadHighlighting(
      String metaId, Map<String, Map<String, List<String>>> map){
    String [] elements = new String[2];
    if(map == null) return elements;
    Map<String, List<String>> em = map.get(metaId);
    //      System.out.println("thay co "+em.size() +" \n "+ em);
    if(em == null || em.isEmpty()) return elements;
    List<String> list = em.get("title");
    if(list != null && list.size() > 0) {
      elements [0] = list.get(0);
    } else {
      list = em.get("title_no_mark");
      if(list != null && list.size() > 0) {
        elements [0] = list.get(0);
      }
    }

    list = em.get("text");
    if(list != null && list.size() > 0) {
      elements [1] = list.get(0);
      return elements;
    }

    list = em.get("text_no_mark");
    if(list != null && list.size() > 0) {
      elements [1] = list.get(0);
      return elements;
    }
    return elements;
  }

}
