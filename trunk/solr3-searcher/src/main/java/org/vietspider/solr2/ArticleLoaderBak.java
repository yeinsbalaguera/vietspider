/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.solr2;

import java.util.Iterator;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.vietspider.bean.Article;
import org.vietspider.db.content.ArticleDatabases;
import org.vietspider.index.SearchData;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 10, 2011  
 */
class ArticleLoaderBak {
  
  private ArticleDatabases databases;
  
  ArticleLoaderBak(ArticleDatabases databases) {
    this.databases = databases;
  }
  
  void loadArticles(SearchData data, QueryResponse response) {
    if(data.total() >= data.getSize()) return;
    
    SolrDocumentList results = response.getResults();

    Iterator<SolrDocument> iter = results.iterator();
    int counter = 0;

    while (iter.hasNext()) {
      SolrDocument resultDoc = iter.next();
      String metaId = (String)resultDoc.getFieldValue("id");
      if(metaId == null) continue;
      
//      System.out.println((String)resultDoc.getFieldValue("address"));
      
      Article article = databases.loadArticle(metaId);
      if(article == null) continue;
      
      Float score = (Float) resultDoc.getFieldValue("score");
      if(score == null) score = new Float(0.0f);
      article.setScore(score);
      
//      Meta meta = article.getMeta();
//      Map<String, Map<String, List<String>>> map = response.getHighlighting();
//      String[] hlelements = loadHighlighting(metaId, map);
//      if(hlelements[0] != null && !hlelements[0].trim().isEmpty()) meta.setTitle(hlelements[0]);
//      if(hlelements[1] != null && !hlelements[1].trim().isEmpty()) meta.setDesc(hlelements[1]);

      data.addArticle(article);
      counter++;
      if(data.total() >= data.getSize()) break;
    }

  }
  
//  private static String[] loadHighlighting(
//      String metaId, Map<String, Map<String, List<String>>> map){
//    String [] elements = new String[2];
//    if(map == null) return elements;
//    Map<String, List<String>> em = map.get(metaId);
//    //      System.out.println("thay co "+em.size() +" \n "+ em);
//    if(em == null || em.isEmpty()) return elements;
//    List<String> list = em.get("title");
//    if(list != null && list.size() > 0) {
//      elements [0] = list.get(0);
//    } else {
//      list = em.get("title_no_mark");
//      if(list != null && list.size() > 0) {
//        elements [0] = list.get(0);
//      }
//    }
//
//    list = em.get("text");
//    if(list != null && list.size() > 0) {
//      elements [1] = list.get(0);
//      return elements;
//    }
//
//    list = em.get("text_no_mark");
//    if(list != null && list.size() > 0) {
//      elements [1] = list.get(0);
//      return elements;
//    }
//    return elements;
//  }

}

