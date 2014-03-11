/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.solr;

import java.io.File;
import java.util.Calendar;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.core.CoreContainer;
import org.vietspider.common.io.UtilFile;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 28, 2009  
 */
public class TestSolrDeleteArticle {

  public static void main(String[] args) throws Exception{
    System.setProperty("vietspider.data.path", "D:\\java\\headvances3\\trunk\\vietspider\\startup\\src\\test\\data\\");
    File file  = UtilFile.getFolder("system/solr");  
    System.setProperty("solr.solr.home", file.getAbsolutePath());
    file  = UtilFile.getFolder("content/solr/data");  
    System.setProperty("solr.data.dir", file.getAbsolutePath());
    CoreContainer.Initializer initializer = new CoreContainer.Initializer();
    CoreContainer coreContainer = initializer.initialize();
    EmbeddedSolrServer server = new EmbeddedSolrServer(coreContainer, "");

    try {
//      SolrQuery query = new  SolrQuery("+nhà+property:\"region hà nội\"").
//      SolrQuery query = new  SolrQuery("+(bán|cho thuê)+căn hộ").
////      setQuery("\"mai phương thúy\"").
//      setFacet(true).
//      setFacetMinCount(1).
//      setFacetLimit(8).setStart(0).
//      setHighlight(true).
//      setHighlightSimplePre("<b>").
//      setHighlightSimplePost("</b>").
//      setHighlightSnippets(2).
//      setHighlightFragsize(200).
//      addFacetField("title").
//      addFacetField("desc").
//      addFacetField("text");
      

//      query = query.addFilterQuery("property:\"region hà nội\"");
//      query = query.addFacetQuery("property:\"region hà nội\"");
//      query.setParam("property", "region hà nội");
//      query.setQuery("desc:thành long or text:mai phương thúy");
      
      Calendar calendar = Calendar.getInstance();
      calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - 100);
      long start = calendar.getTimeInMillis();
      calendar = Calendar.getInstance();
      long end = calendar.getTimeInMillis();
      SolrQuery query = new  SolrQuery("time:[" + start + " TO " + end + "]");
//      query = query.addFilterQuery("time:[" + start + " TO " + end + "]");
      
      System.out.println(query.toString());

      QueryResponse response = server.query(query);
//      System.out.println(response.getResults().size());
      System.out.println(response.getResults().getNumFound());
      
      System.out.println(query.getQuery());
      
      server.deleteByQuery(query.getQuery());
      server.commit();
      
      response = server.query(query);
//    System.out.println(response.getResults().size());
      System.out.println(response.getResults().getNumFound());

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      System.exit(0);
    }
  }
}
