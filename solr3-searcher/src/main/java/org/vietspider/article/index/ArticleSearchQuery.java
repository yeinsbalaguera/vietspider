/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.article.index;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.NumericRangeQuery;
import org.vietspider.db.ContentIndex;
import org.vietspider.index.CommonSearchQuery;
import org.vietspider.serialize.NodeMap;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 25, 2009  
 */
@NodeMap("article-search-query")
public class ArticleSearchQuery extends CommonSearchQuery {

  protected TextQueryParser parser;
  
  protected String source;

  public ArticleSearchQuery() {
  }

  public ArticleSearchQuery(String pattern) {
    super(pattern);
  }

  public TextQueryParser getParser() {
    if(parser == null) parser = new TextQueryParser();
    return parser; 
  }
  
  public void setParser(TextQueryParser parser) { this.parser = parser;  }

  public BooleanQuery createQuery() throws Exception {
    BooleanQuery query = getParser().createQuery(getLPattern());
    
    if(date < 1) {
//      System.out.println(" buoc 1 "+ query);
      return query;
    }

    BooleanQuery newQuery  = new BooleanQuery();
    
    newQuery.add(query, BooleanClause.Occur.MUST);
    
    if(date > 0) {
      Calendar calendar = Calendar.getInstance();
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
      int max = Integer.parseInt(dateFormat.format(calendar.getTime()));

      calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - (date - 1));
      int min = Integer.parseInt(dateFormat.format(calendar.getTime()));

      NumericRangeQuery rangeQuery = NumericRangeQuery.newIntRange(
          ContentIndex.FIELD_POST_DATE, min, max, true, true);
      BooleanClause booleanClause = new BooleanClause(rangeQuery, BooleanClause.Occur.MUST);
      newQuery.add(booleanClause);
    }
    
    
    return newQuery;
  }
  
  public String getSource() { return source; }
  public void setSource(String source) {  this.source = source; }

  public void savePattern() {
//    SearchMonitor.getInstance().savePattern(pattern, (int)total);
  }

}
