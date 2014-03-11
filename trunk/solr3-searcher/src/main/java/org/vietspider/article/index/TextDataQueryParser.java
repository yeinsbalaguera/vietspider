/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.article.index;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.solr.search.QParser;
import org.apache.solr.search.SolrQueryParser;
import org.vietspider.locale.vn.VietnameseConverter;
import org.vietspider.solr2.search.Proximity;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 13, 2011  
 */
class TextDataQueryParser {
  
  SolrQueryParser titleParser;
  SolrQueryParser titleNoMarkParser;
  SolrQueryParser contentParser;
  SolrQueryParser tagParser;
  SolrQueryParser contentNoMarkParser;
  
  SolrQueryParser timeParser;
  
  TextDataQueryParser(QParser qparser, String defaultField) {
    tagParser = new SolrQueryParser(qparser, "tag");
    
    titleParser = new SolrQueryParser(qparser, "title");
    titleNoMarkParser = new SolrQueryParser(qparser, "title_no_mark");
    
    contentParser = new SolrQueryParser(qparser, defaultField);
    contentNoMarkParser = new SolrQueryParser(qparser, "text_no_mark");
    
    timeParser = new SolrQueryParser(qparser, "time");
  }
  
  void setDefaultOperator(QueryParser.Operator operator) {
    titleParser.setDefaultOperator(operator);
    titleNoMarkParser.setDefaultOperator(operator);
    contentParser.setDefaultOperator(operator);
    contentNoMarkParser.setDefaultOperator(operator);
  }
  
  
  BooleanQuery createQuery1(String text) throws ParseException {
    return normalParse(text);
  }
  
  BooleanQuery normalParse(String text) throws ParseException {
    String nomarkText = VietnameseConverter.toTextNotMarked(text);
    Proximity proximity = ArticleQueryUtils.buildProximity(text);

    List<Query> list = new ArrayList<Query>();
    
//    if(proximity != null) {
//      System.out.println("========beo beo  > "+ proximity.getValue());
//    }
    if(proximity != null 
        && proximity.getValue() != null
        && proximity.getValue().trim().length() > 0) {
      Query _query = tagParser.parse(proximity.getValue());
      _query.setBoost(15.0f);
      list.add(_query);
    }
    

    Query _query = titleParser.parse(text);
    if(proximity != null && proximity.getCounter() == 2) {
      _query.setBoost(0.5f);
    } else {
      _query.setBoost(3.0f);
    }
//    System.out.println("========== >"+ _query);
    list.add(_query);

    if(proximity != null) {
      _query = titleParser.parse(proximity.getValue());
      _query.setBoost(10.0f);
      list.add(_query);
    }

    _query = titleNoMarkParser.parse(nomarkText);
    if(proximity != null && proximity.getCounter() == 2) {
      _query.setBoost(0.5f);
    } else {
      _query.setBoost(1.7f);
    }
    list.add(_query);
    
    _query = titleNoMarkParser.parse(text);
    if(proximity != null && proximity.getCounter() == 2) {
      _query.setBoost(0.5f);
    } else {
      _query.setBoost(1.7f);
    }
    list.add(_query);

    _query = contentParser.parse(text);
    if(proximity != null && proximity.getCounter() == 2) {
      _query.setBoost(0.2f);
    } else {
      _query.setBoost(1.0f);
    }
    list.add(_query);

    if(proximity != null) {
      _query = contentParser.parse(proximity.getValue());
      _query.setBoost(9f);
      list.add(_query);
    }

    _query = contentNoMarkParser.parse(nomarkText);
    if(proximity != null && proximity.getCounter() == 2) {
      _query.setBoost(0.2f);
    } else {
      _query.setBoost(0.7f);
    }
    list.add(_query);
    
    _query = contentNoMarkParser.parse(text);
    if(proximity != null && proximity.getCounter() == 2) {
      _query.setBoost(0.2f);
    } else {
      _query.setBoost(0.7f);
    }
    list.add(_query);
//    System.out.println(" chay thu vao day "+ list.size());

    BooleanQuery patternQuery  = new BooleanQuery();
    for(int i = 0; i < list.size(); i++) {
//      System.out.println(" chay thu "+ list.get(i));
      patternQuery.add(list.get(i), BooleanClause.Occur.SHOULD);  
    }

    return patternQuery;
  }
  
  
}
