/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.model;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.bean.Article;
import org.vietspider.bean.Domain;
import org.vietspider.serialize.NodeMap;
import org.vietspider.serialize.NodesMap;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 23, 2011  
 */
@NodeMap("top-collection")
public class TopArticleCollection {
  
  @NodesMap(value = "collection-articles", item ="item")
  private List<TopArticles> list = new ArrayList<TopArticles>();
  
  public TopArticleCollection() {
    
  }

  public List<TopArticles> getList() {
    return list;
  }

  public void setList(List<TopArticles> articles) {
    this.list = articles;
  }
  
  public void add(Article article) {
    Domain domain = article.getDomain();
    String id  = domain.getCategory();
    for(int i = 0; i < list.size(); i++) {
      if(list.get(i).getId().equals(id)) {
        list.get(i).add(article);
        return;
      }
    }
    TopArticles topArticles = new TopArticles(id);
    topArticles.add(article);
    list.add(topArticles);
  }
  
  
}
