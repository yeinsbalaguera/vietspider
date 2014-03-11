/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.solr;

import java.io.File;
import java.util.Calendar;
import java.util.Iterator;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.core.CoreContainer;
import org.vietspider.common.io.UtilFile;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 28, 2009  
 */
public class TestSolr3SearchByTime {

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
      Calendar calendar = Calendar.getInstance();
      calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 2);
      long max = calendar.getTimeInMillis();
      
      calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 1000);
      long min = calendar.getTimeInMillis();
        
//      SolrQuery query = new  SolrQuery("+nhà+property:\"region hà nội\"").
      SolrQuery query = new  SolrQuery("time:[" + min + " TO " + max + "]").
//      setQuery("\"mai phương thúy\"").
      setFacet(true).
      setFacetMinCount(1).
      setFacetLimit(8).setStart(0);
      

      QueryResponse response = server.query(query);
//      System.out.println(response.getResults().size());
      System.out.println(response.getResults().getNumFound());

      Iterator<SolrDocument> iter = response.getResults().iterator();

      while (iter.hasNext()) {
        SolrDocument resultDoc = iter.next();

        long id = (Long)resultDoc.getFieldValue("id"); //id is the uniqueKey field
        System.out.println("");
        
        long time  = (Long)resultDoc.getFieldValue("time");
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        System.out.println(id + " : " + calendar.getTime());
        System.out.println(resultDoc.getFieldValue("title"));
//        System.out.println(time > start && time  < end);
        
//        System.out.println(id  + "====>" + (String) resultDoc.getFieldValue("title"));
//        System.out.println((String) resultDoc.getFieldValue("desc"));
//        System.out.println((String) resultDoc.getFieldValue("text"));
//        System.out.println(resultDoc.getFieldValues("property"));
      }
      
//      Map<String, Map<String, List<String>>> map = response.getHighlighting();
//      System.out.println(map);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      System.exit(0);
    }
  }
}
