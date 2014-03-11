/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.tpt;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.vietspider.common.io.LogService;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 23, 2010  
 */
public class TptIndexingReader  {

  private EmbeddedSolrServer server;
  
  TptIndexingReader(EmbeddedSolrServer server) throws Exception  {
    this.server = server;
  }
  
  public QueryResponse search(SolrQuery query) {
    try {
      return server.query(query);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    return null;
  }


}
