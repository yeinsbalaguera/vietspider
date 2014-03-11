/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.index;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.NumericRangeQuery;
import org.vietspider.db.ContentIndex;
import org.vietspider.serialize.NodeMap;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 25, 2009  
 */
public class SearchQuery extends CommonSearchQuery {

  protected QueryParser parser;

  public SearchQuery() {
  }

  public SearchQuery(String pattern) {
    super(pattern);
  }

  public QueryParser getParser() {
    if(parser == null) parser = new QueryParser();
    return parser; 
  }
  
  public void setParser(QueryParser parser) { this.parser = parser;  }

  public BooleanQuery createQuery() throws Exception {
    BooleanQuery query = getParser().createQuery(getLPattern());
//    if(action != null && !containsAction()) {
//      query = getParser().createQuery(action + " " +getLPattern());
//    } else {
//      query = getParser().createQuery(getLPattern());
//    }
    
    String [] actions = getActions();
    
    if(/*region == null 
        &&*/ price != null 
        && date < 1 
        && actions == null) {
//      System.out.println(" buoc 1 "+ query);
      return query;
    }

    BooleanQuery newQuery  = new BooleanQuery();
    
    if(actions != null && !containsAction()) {
      for(int i = 0; i < actions.length; i++) {
        BooleanQuery actionQuery  = getParser().createQuery(actions[i]);
        newQuery.add(actionQuery, BooleanClause.Occur.MUST);
      }
    }
    
//    if(action != null) {
//      BooleanQuery actionQuery = getParser().createQuery(action.toLowerCase());
//      newQuery.add(actionQuery, BooleanClause.Occur.MUST);
//    } 
    
    newQuery.add(query, BooleanClause.Occur.MUST);
    
//    System.out.println(" ===========  >"+ newQuery);

//    if(region != null && !(region = region.trim().toLowerCase()).isEmpty()) {
//      TermQuery termQuery = new TermQuery(new Term(ContentIndex.FIELD_REGION, region));
//      termQuery.setBoost(0.0f);
//      BooleanClause booleanClause = new BooleanClause(termQuery, BooleanClause.Occur.MUST);
//      newQuery.add(booleanClause);
//    } 

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
    
//    System.out.println( " tai day ta co "+ newQuery);
    
    return newQuery;
  }
  
  private boolean containsAction() {
    String text = getLPattern();
    String [] actions = getActions();
    if(actions  == null) return false;
    for(int i = 0; i < actions.length; i++) {
      if(text.indexOf(actions[i]) > -1) return true;
    }
    return false;
  }
  
  public void savePattern() {
//    SearchMonitor.getInstance().savePattern(pattern, (int)total);
  }

}
