/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.solr2.search;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.solr.search.QParser;
import org.apache.solr.search.SolrQueryParser;
import org.vietspider.bean.NLPData;
import org.vietspider.locale.vn.VietnameseConverter;

import vn.hus.nlp.tokenizer.VietTokenizer;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 13, 2011  
 */
public class TextDataQueryParser {
  
  SolrQueryParser titleParser;
  SolrQueryParser titleNoMarkParser;
  SolrQueryParser contentParser;
  SolrQueryParser contentNoMarkParser;
  SolrQueryParser timeParser;
  SolrQueryParser actionParser;
  
  TextDataQueryParser(QParser qparser, String defaultField) {
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
  
  
  BooleanQuery createQuery1(String text, short type) throws ParseException {
    if(type != NLPData.NORMAL_TEXT) {
      return normalParse(text);
    }
    
    VietTokenizer tokenizer = VietTokenizer.getInstance();
    List<String>  elements = tokenizer.segmentLine(text);
//    for(String element :  elements) {
//      System.out.println("=============> "+ element);
//    }
    if(elements.size() < 2) {
      return normalParse(text);
    }
    
    return hightParse(elements);
  }
  
  BooleanQuery hightParse(List<String> elements) throws ParseException {
    List<String> melements = new ArrayList<String>(elements.size());
    for(int i = 0; i < elements.size(); i++) {
      melements.add(VietnameseConverter.toTextNotMarked(elements.get(i)));
    }
    // type 1: all of words exist together
    //in title
    BooleanQuery patternQuery  = new BooleanQuery();
    Query query = existAll(titleParser, elements, 3.0f, 0.5f);
    if(query != null) {
//      query.setBoost(10.0f);
      patternQuery.add(query, BooleanClause.Occur.SHOULD);
    }
    
    query = existAll(titleNoMarkParser, melements, 2.5f, 0.3f);
    if(query != null) {
//      query.setBoost(8.0f);
      patternQuery.add(query, BooleanClause.Occur.SHOULD);
    }
    
    query = existAll(contentParser, elements, 2.5f, 0.4f);
    if(query != null) {
//      query.setBoost(8.0f);
      patternQuery.add(query, BooleanClause.Occur.SHOULD);
    }
    
    query = existAll(contentNoMarkParser, melements, 2.0f, 0.2f);
    if(query != null) {
//      query.setBoost(5.0f);
      patternQuery.add(query, BooleanClause.Occur.SHOULD);
    }
    
    query = common(titleParser, contentParser, elements);
    if(query != null) {
      query.setBoost(0.2f);
      patternQuery.add(query, BooleanClause.Occur.SHOULD);
    }
    
    query = common(titleNoMarkParser, contentNoMarkParser, melements);
    if(query != null) {
      query.setBoost(0.1f);
      patternQuery.add(query, BooleanClause.Occur.SHOULD);
    }
    
    
    return patternQuery;
  }
  
  BooleanQuery common(SolrQueryParser parser1, 
      SolrQueryParser parser2, List<String> elements)  throws ParseException {
    BooleanQuery patternQuery = null;
    for(int i = 0; i < elements.size(); i++) {
      Proximity proximity = QueryUtils.buildProximity(elements.get(i));
      BooleanQuery _query  = new BooleanQuery();
      if(proximity != null && proximity.counter > 1) {
        _query.add(parser1.parse(proximity.value), BooleanClause.Occur.SHOULD);
        _query.add(parser2.parse(proximity.value), BooleanClause.Occur.SHOULD);
      } else {
        _query.add(parser1.parse(elements.get(i)), BooleanClause.Occur.SHOULD);
        _query.add(parser2.parse(elements.get(i)), BooleanClause.Occur.SHOULD);
      }
      
      if(patternQuery == null) {
        patternQuery = new BooleanQuery();
      }
      patternQuery.add(_query, BooleanClause.Occur.MUST);  
    }
    
    return patternQuery;
  }
  
  BooleanQuery existAll(SolrQueryParser parser, 
      List<String> elements, float high, float low) throws ParseException {
    BooleanQuery patternQuery = null;
    for(int i = 0; i < elements.size(); i++) {
      Proximity proximity = QueryUtils.buildProximity(elements.get(i));
      Query query = null;
      if(proximity != null && proximity.counter > 1) {
        query = parser.parse(proximity.value);
        query.setBoost(high);
      } else {
        query = parser.parse(elements.get(i));
        query.setBoost(low);
      }
      if(patternQuery == null) {
        patternQuery = new BooleanQuery();
      }
      patternQuery.add(query, BooleanClause.Occur.MUST);  
    }
    return patternQuery;
  }
  
  
  BooleanQuery normalParse(String text) throws ParseException {
    String nomarkText = VietnameseConverter.toTextNotMarked(text);
    Proximity proximity = QueryUtils.buildProximity(text);

    List<Query> list = new ArrayList<Query>();

    Query _query = titleParser.parse(text);
    if(proximity != null && proximity.counter == 2) {
      _query.setBoost(0.5f);
    } else {
      _query.setBoost(3.0f);
    }
    list.add(_query);

    if(proximity != null) {
      _query = titleParser.parse(proximity.value);
      _query.setBoost(10.0f);
      list.add(_query);
    }

    _query = titleNoMarkParser.parse(nomarkText);
    if(proximity != null && proximity.counter == 2) {
      _query.setBoost(0.5f);
    } else {
      _query.setBoost(1.7f);
    }
    list.add(_query);

    _query = contentParser.parse(text);
    if(proximity != null && proximity.counter == 2) {
      _query.setBoost(0.2f);
    } else {
      _query.setBoost(1.0f);
    }
    list.add(_query);

    if(proximity != null) {
      _query = contentParser.parse(proximity.value);
      _query.setBoost(9f);
      list.add(_query);
    }

    _query = contentNoMarkParser.parse(nomarkText);
    if(proximity != null && proximity.counter == 2) {
      _query.setBoost(0.2f);
    } else {
      _query.setBoost(0.7f);
    }
    list.add(_query);

    BooleanQuery patternQuery  = new BooleanQuery();
    for(int i = 0; i < list.size(); i++) {
      patternQuery.add(list.get(i), BooleanClause.Occur.SHOULD);  
    }

    return patternQuery;
  }
  
  
}
