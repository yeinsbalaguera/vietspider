/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.db.tp;

import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.QParser;
import org.apache.solr.search.SolrQueryParser;
import org.vietspider.content.tp.TpWorkingData;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 6, 2010  
 */
class TpSolrQueryParser extends QParser {

  String sortStr;

  private SolrQueryParser keyParser;
  private SolrQueryParser titleParser;

  public TpSolrQueryParser(String qstr,
      SolrParams localParams, SolrParams params, SolrQueryRequest req) {
    super(qstr, localParams, params, req);

    String defaultField = getParam(CommonParams.DF);
    if (defaultField==null) {
      defaultField = getReq().getSchema().getDefaultSearchFieldName();
    }
    
    keyParser = new SolrQueryParser(this, "key");
    titleParser = new SolrQueryParser(this, "title");
//    titleParser.setDefaultOperator(Operator.AND);
  }


  public Query parse() throws ParseException {
    String pattern = qstr;
    int idx = qstr.indexOf("title:");
    if(idx < 0) return keyParser.parse(qstr);
    pattern = qstr.substring(0, idx);
    String title = qstr.substring(idx+6);
    
    StringBuilder builder = new StringBuilder();
    for(int i = 0; i < title.length(); i++) {
      char c = title.charAt(i);
      for(int k = 0; k < TpWorkingData.SPECIALS.length; k++) {
        if(c != TpWorkingData.SPECIALS[k]) continue;
        builder.append('\\');
        break;
      }
      builder.append(c);
    }
    title = builder.toString();
    
    BooleanQuery patternQuery  = new BooleanQuery();
    patternQuery.add(keyParser.parse(pattern), BooleanClause.Occur.SHOULD);
    if(title.trim().length() > 0) {
      patternQuery.add(titleParser.parse(title), BooleanClause.Occur.SHOULD);
    }
//    return keyParser.parse(qstr);
//    System.out.println(" thay co "+ patternQuery);
    
    return patternQuery;
  }



}
