/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.index;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

import org.vietspider.bean.Article;
import org.vietspider.serialize.NodeMap;
import org.vietspider.serialize.NodesMap;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 20, 2010  
 */
@NodeMap("search-result-collection")
public class SearchResultCollection implements SearchData {
  
  @NodeMap("total")
  private long total;
  
  @NodeMap("id")
  private String id;
  
  @NodeMap("pattern")
  private String pattern;
  
  @NodesMap(value = "collection", item = "item")
  private ArrayList<SearchResult> collection = new ArrayList<SearchResult>();
  
  public ArrayList<SearchResult> getCollection() { return collection; }
  public void setCollection(ArrayList<SearchResult> collection) { this.collection = collection; }
  
  public long getTotal() { return total; }
  public void setTotal(long total) { this.total = total; }
  
  public String getId() { return id; }
  public void setId(String id) { this.id = id; }
  
  public String getPattern() { return pattern; }
  public void setPattern(String pattern) { this.pattern = pattern;  }
  
  public void merge(SearchResultCollection temp) {
    total += temp.getTotal();
    collection.addAll(temp.getCollection());
  }
  
  public void sort() {
    TreeMap<String, SearchResult> sort = new TreeMap<String, SearchResult>();
    for(int i = 0;  i < collection.size(); i++) {
      SearchResult result = collection.get(i);
      if(result == null || result.getId() == null) continue;
      sort.put(result.getId(), result);
    }
//    System.out.println("==== > truoc so.rt "+ collection.size());
    ArrayList<SearchResult> newList = new ArrayList<SearchResult>();
    newList.addAll(sort.values());
    Collections.sort(newList, new SearchResultComparator());
    collection = newList;
//    System.out.println("==== > sau sort "+ collection.size());
  }
  
  public void addArticle(Article article) {
    for(int i = 0; i < collection.size(); i++) {
      collection.get(i).setArticle(article);
    }
  }
  
  public void toArticles(List<Article> list) {
    if(collection == null) return;
    for(int i = 0; i < collection.size(); i++) {
      if(collection.get(i) == null) continue;
      Article article = collection.get(i).getArticle();
      if(article == null) continue;
      list.add(article);
    }
  }
  
  public int total() { return 0; }
  
  public int getSize() { return 10; }
  
}
