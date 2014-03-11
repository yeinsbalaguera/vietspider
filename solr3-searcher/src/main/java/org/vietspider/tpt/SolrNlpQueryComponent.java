/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.tpt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Query;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.ShardParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.StrUtils;
import org.apache.solr.handler.component.QueryComponent;
import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;
import org.apache.solr.search.QParser;
import org.apache.solr.search.QParserPlugin;
import org.apache.solr.search.QueryParsing;
import org.apache.solr.util.SolrPluginUtils;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 6, 2010  
 */
public class SolrNlpQueryComponent extends QueryComponent {
  
  public static final String VS_COMPONENT_NAME = "nlp_query";
  
  @Override
  public void prepare(ResponseBuilder rb) throws IOException  {
    SolrQueryRequest req = rb.req;
    SolrParams params = req.getParams();
    if (!params.getBool(COMPONENT_NAME, true)) {
      return;
    }
    SolrQueryResponse rsp = rb.rsp;

    // Set field flags
    String fl = params.get(CommonParams.FL);
    int fieldFlags = 0;
    if (fl != null) {
      fieldFlags |= SolrPluginUtils.setReturnFields(fl, rsp);
    }
    rb.setFieldFlags( fieldFlags );

    String defType = params.get(QueryParsing.DEFTYPE);
    defType = defType==null ? QParserPlugin.DEFAULT_QTYPE : defType;

    if (rb.getQueryString() == null) {
      rb.setQueryString( params.get( CommonParams.Q ) );
    }
    
    try {
      QParser parser = getParser(rb.getQueryString(), defType, req);
      rb.setQuery( parser.getQuery() );
//      System.out.println("tete " + parser.getSort(true));
      rb.setSortSpec(parser.getSort(true) );
      rb.setQparser(parser);

      String[] fqs = req.getParams().getParams(CommonParams.FQ);
      if (fqs!=null && fqs.length!=0) {
        List<Query> filters = rb.getFilters();
        if (filters == null) {
          filters = new ArrayList<Query>();
          rb.setFilters( filters );
        }
        for (String fq : fqs) {
          if (fq != null && fq.trim().length()!=0) {
            QParser fqp = QParser.getParser(fq, null, req);
//            System.out.println("hihi filter "+fqp.getClass());
//            rb.setQuery(fqp.getQuery());
            filters.add(fqp.getQuery());
          }
        }
      }
    } catch (ParseException e) {
      throw new SolrException(SolrException.ErrorCode.BAD_REQUEST, e);
    }

    // TODO: temporary... this should go in a different component.
    String shards = params.get(ShardParams.SHARDS);
    if (shards != null) {
      List<String> lst = StrUtils.splitSmart(shards, ",", true);
      rb.shards = lst.toArray(new String[lst.size()]);
    }
    String shards_rows = params.get(ShardParams.SHARDS_ROWS);
    if(shards_rows != null) {
      rb.shards_rows = Integer.parseInt(shards_rows);
    }
    String shards_start = params.get(ShardParams.SHARDS_START);
    if(shards_start != null) {
      rb.shards_start = Integer.parseInt(shards_start);
    }
  }
  
  private QParser getParser(String qstr, String defaultType, SolrQueryRequest req) throws ParseException {
    SolrParams localParams = QueryParsing.getLocalParams(qstr, req.getParams());
    String type;
    
    if (localParams == null) {
      type = defaultType;
    } else {
      String localType = localParams.get(QueryParsing.TYPE);
      type = localType == null ? defaultType : localType;
      qstr = localParams.get("v");
    }

    type = type==null ? QParserPlugin.DEFAULT_QTYPE : type;

//    QParserPlugin qplug = req.getCore().getQueryPlugin(type);
    return new SolrNlpQueryParser(qstr, localParams, req.getParams(), req);
  }   
  
}
