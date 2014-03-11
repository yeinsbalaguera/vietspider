/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.article.index;

import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.QParser;
import org.apache.solr.search.QueryParsing;
import org.apache.solr.search.SolrQueryParser;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 6, 2010  
 */
class ArticleSolrQueryParser extends QParser {

  String sortStr;

  private TextDataQueryParser textParser;
  private SolrQueryParser sourceParser;

  public ArticleSolrQueryParser(String qstr,
      SolrParams localParams, SolrParams params, SolrQueryRequest req) {
    super(qstr, localParams, params, req);

    String defaultField = getParam(CommonParams.DF);
    if (defaultField==null) {
      defaultField = getReq().getSchema().getDefaultSearchFieldName();
    }

    sourceParser = new SolrQueryParser(this, "source");
    textParser = new TextDataQueryParser(this, defaultField);
  }


  public Query parse() throws ParseException {
    String opParam = getParam(QueryParsing.OP);
    QueryParser.Operator operator = QueryParser.Operator.AND;
    if (opParam != null) {
      operator = "AND".equals(opParam) ? QueryParser.Operator.AND : QueryParser.Operator.OR;
    }
    textParser.setDefaultOperator(operator);
    
    if(qstr.startsWith("source:")) {
      int idx = qstr.indexOf(':');
      qstr = qstr.substring(idx+1);
//      System.out.println(sourceParser.parse(qstr));
      Query _query = sourceParser.parse(qstr);
//      System.out.println("type 1 " + _query);
      return _query;
    }
    
    BooleanQuery _query = textParser.createQuery1(qstr);
//    System.out.println("type 2 " + _query);
    return _query;
  }



}
