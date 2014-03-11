/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.index;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.vietspider.bean.Article;
import org.vietspider.serialize.NodeMap;
import org.vietspider.serialize.NodesMap;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 5, 2010  
 */
@NodeMap("search-response")
public class SearchResponse implements SearchData {
  
  @NodeMap("total")
  private long total;
  
//  @NodeMap("counter")
//  private long counter = 0 ;
  
  @NodeMap("time")
  private long time;
  
  @NodesMap(value = "articles", item = "item")
  private List<Article> articles = new ArrayList<Article>();
  
  @NodeMap("size")
  private int size = 10;
  
  @NodeMap("start")
  private int start = 0;
  
  @NodeMap("max")
  private int max = 0;
  
  @NodeMap(value = "query", depth = 4)
  private CommonSearchQuery query ;
  
  private String id;
  
  public SearchResponse() {
  }
  
  public void merge(SearchResponse response) {
    if(response == null) return;
    total += response.getTotal();
    time = (time + response.getTime())/2;
    articles.addAll(response.getArticles());
  }
  
  public void sortData() {
    Collections.sort(articles, new SearchArticleComparator());
    
    while(articles.size() > size) {
      articles.remove(articles.size() - 1);
    }
  }
  
  public String getId() {
    if(id != null) return id;
    StringBuilder builder = new StringBuilder();
    builder.append(query.getCode());
    builder.append('_').append(start);
    builder.append('_').append(size);
    id = builder.toString();
    return id;
  }

  public long getTotal() { return total; }
  public void setTotal(long total) { this.total = total; }
  
//  @GetterMap("counter")
//  public long getCounter() { return counter;  }
//  @SetterMap("counter")
//  public void setCounter(long counter) { this.counter = counter; }

  public long getTime() { return time; }
  public void setTime(long time) { this.time = time; }

  public List<Article> getArticles() {
    return articles;
  }

  public void setArticles(List<Article> articles) {
    this.articles = articles;
  }

  public int getSize() { return size; }
  public void setSize(int size) { this.size = size;  }
  
  public int getMax() { return max; }
  public void setMax(int max) { this.max = max; }

  public CommonSearchQuery getQuery() { return query; }
  public void setQuery(CommonSearchQuery query) { this.query = query; }

  public int getStart() { return start; }
  public void setStart(int start) { this.start = start; }
  
  public void addArticle(Article article) { articles.add(article); }
  
  public int total() { return articles.size(); }

//  public SearchResponse cloneInstance() {
//    SearchResponse searcher2 = new SearchResponse();
//    searcher2.setSize(size);
//    searcher2.setQuery(query);
//    searcher2.setStart(start);
//    searcher2.setArticles(articles);
//    searcher2.setTotal(total);
//    searcher2.setTime(time);
//    
//    return searcher2;
//  }
  
  
}
