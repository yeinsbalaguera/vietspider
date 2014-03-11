/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.solr2;

import org.vietspider.index.SearchResponse;
import org.vietspider.index.SearchResultCollection;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 10, 2011  
 */
public interface ISolrIndexingSearcher {
  
  public SearchResponse search(SearchResponse searcher);
  
  public SearchResultCollection searchForCached(SearchResponse searcher, int index);
  
  public SearchResultCollection loadArticles(SearchResultCollection collection, int index);
}
