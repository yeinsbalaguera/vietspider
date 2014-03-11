/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.solr2.search;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.QParser;
import org.apache.solr.search.QueryParsing;
import org.apache.solr.search.SolrQueryParser;
import org.vietspider.bean.NLPData;
import org.vietspider.nlp.query.QueryAnalyzer;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 6, 2010  
 */
class SearchQueryParser extends QParser {

  String sortStr;

//  private String[] actions;
//  private float bottomScore = -1;

  private TextDataQueryParser textParser;
  private NlpQueryParser nlpParser;
  private TimeQueryParser timeParser;
  
  private SolrQueryParser phoneParser;
  private SolrQueryParser emailParser;
//  private SolrQueryParser addressParser;

  public SearchQueryParser(String qstr,
      SolrParams localParams, SolrParams params, SolrQueryRequest req) {
    super(QueryUtils.normalize(qstr), localParams, params, req);
//    actions = params.getParams("search_action");
//    String bottomScoreValue = params.get("bottom_score");
//    if(bottomScoreValue != null && !bottomScoreValue.trim().isEmpty()) {
//      bottomScore = Float.parseFloat(bottomScoreValue);
//    }

    String defaultField = getParam(CommonParams.DF);
    if (defaultField==null) {
      defaultField = getReq().getSchema().getDefaultSearchFieldName();
    }

    textParser = new TextDataQueryParser(this, defaultField);
    nlpParser = new NlpQueryParser(this, textParser);
    timeParser = new TimeQueryParser(this);

    //    System.out.println("normalize "+ QueryUtils.normalize(qstr));
  }


  public Query parse() throws ParseException {
//    System.out.println(qstr +  " : " + qstr.startsWith("field\\:"));
    
    if(qstr.startsWith("field\\:")) {
      BooleanQuery mainQuery = new BooleanQuery();
      qstr = qstr.substring(7);
      if(qstr.startsWith("phone\\:")) {
        qstr = qstr.substring(7);
//        System.out.println(qstr);
        if(phoneParser == null) {
          phoneParser = new SolrQueryParser(this, "phone");
          phoneParser.setDefaultOperator(QueryParser.Operator.OR);
        }
//        System.out.println(phoneParser.parse(qstr));
        mainQuery.add(phoneParser.parse(qstr), BooleanClause.Occur.MUST);
      }
      
      if(qstr.startsWith("email\\:")) {
        qstr = qstr.substring(7);
        if(emailParser == null) {
          emailParser = new SolrQueryParser(this, "email");
          emailParser.setDefaultOperator(QueryParser.Operator.OR);
        }
        mainQuery.add(emailParser.parse(qstr), BooleanClause.Occur.MUST);
      }
      
      if(qstr.startsWith("address\\:")) {
        qstr = qstr.substring(9);
//        if(addressParser == null) {
//          addressParser = new SolrQueryParser(this, "address");
//          addressParser.setDefaultOperator(QueryParser.Operator.AND);
//        }
        mainQuery.add(nlpParser.parseAddress(qstr), BooleanClause.Occur.MUST);
      }
      
      List<Query> shouldQueries = timeParser.createShouldQueries(null);
      for(int i = 0; i < shouldQueries.size(); i++) {
        mainQuery.add(shouldQueries.get(i), BooleanClause.Occur.SHOULD);
      }
//      System.out.println("===========> "+ mainQuery);
      return mainQuery;
    }
    
    String opParam = getParam(QueryParsing.OP);
    QueryParser.Operator operator = QueryParser.Operator.AND;
    if (opParam != null) {
      operator = "AND".equals(opParam) ? QueryParser.Operator.AND : QueryParser.Operator.OR;
    }
    textParser.setDefaultOperator(operator);
    nlpParser.setDefaultOperator(operator);

    Map<Short, Collection<?>> records = QueryAnalyzer.getProcessor().process(qstr);

    //    System.out.println(qstr);
    //    System.out.println(" tai day ta co "+ defaultField+ " ===  > "+qstr);

    BooleanQuery mainQuery = null;

    List<BooleanQuery> mustQueries = nlpParser.createMustQueries(qstr, records);
    List<Query> shouldQueries = timeParser.createShouldQueries(records);

    if(mustQueries.size() > 0 || shouldQueries.size() > 0) {
      mainQuery = new BooleanQuery();

      //      BooleanQuery hightQuery = textParser.createHightQuery(qstr);
      //      mainQuery.add(hightQuery, BooleanClause.Occur.SHOULD);

      BooleanQuery nlpQuery = new BooleanQuery();

      for(int i = 0; i < mustQueries.size(); i++) {
        nlpQuery.add(mustQueries.get(i), BooleanClause.Occur.MUST);
      }

      for(int i = 0; i < shouldQueries.size(); i++) {
        nlpQuery.add(shouldQueries.get(i), BooleanClause.Occur.SHOULD);
      }

      mainQuery = nlpQuery;
      
//      Query hlquery = textParser.createQuery1(qstr.toLowerCase());
//      mainQuery.setHighlightquery(hlquery);

      //      mainQuery.add(nlpQuery, BooleanClause.Occur.SHOULD);
    } else {
      mainQuery = textParser.createQuery1(qstr, NLPData.NORMAL_TEXT);
//      Query hlquery = textParser.createQuery1(qstr.toLowerCase());
//      mainQuery.setHighlightquery(hlquery);
    }
    
//    new Exception().printStackTrace();
//    System.out.println(" query is " + mainQuery + " : "+ qstr);
    
    return mainQuery;
  }

//  public String[] getDefaultHighlightFields() { 
//    return textParser.getDefaultHighlightFields(); 
//  }

  public static void main(String[] args) {
    Proximity proximity = QueryUtils.buildProximity("   ba  Vi  b ");
    if(proximity == null) return;
    System.out.println(proximity.value+ " : "+ proximity.counter);
  }

}
