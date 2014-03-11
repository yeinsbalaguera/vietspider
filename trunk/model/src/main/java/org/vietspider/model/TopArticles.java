/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.model;

import java.util.LinkedList;
import java.util.List;

import org.vietspider.bean.Article;
import org.vietspider.bean.Relation;
import org.vietspider.serialize.NodeMap;
import org.vietspider.serialize.NodesMap;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 23, 2011  
 */
@NodeMap("top-articles")
public class TopArticles {

  @NodeMap("id")
  private String id;
  
  @NodesMap(value = "article", item ="item")
  private List<TopArticle> articles = new LinkedList<TopArticle>();
  
  public TopArticles() {
    
  }
  
  public TopArticles(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public List<TopArticle> getArticles() {
    return articles;
  }

  public void setArticles(List<TopArticle> articles) {
    this.articles = articles;
  }
  
  public void add(Article article) {
    if(articles.size() > 5) articles.remove(0);
    TopArticle topArticle = new TopArticle();
    topArticle.setId(article.getId());
    List<Relation> rels = article.getRelations();
    for(int i = 0; i < rels.size(); i++) {
      topArticle.getRels().add(rels.get(i).getRelation());
    }
    articles.add(topArticle);
  }
  
  
  
}
