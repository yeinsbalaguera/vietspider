/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.solr2;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.vietspider.bean.Article;
import org.vietspider.bean.NLPData;
import org.vietspider.bean.NLPRecord;
import org.vietspider.common.Application;
import org.vietspider.db.content.IDatabases;
import org.vietspider.db.database.DatabaseService;
import org.vietspider.db.database.MetaList;
import org.vietspider.index.ClassifiedSearchQuery;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 28, 2009  
 */
public class TestNewSolr {
  
  private static void testSearch() {
    ClassifiedSearchQuery query = new ClassifiedSearchQuery("nhà chính chủ 500 triệu");
    
//    Calendar calendar = Calendar.getInstance();
//    long max = calendar.getTimeInMillis();
//    calendar.set(Calendar.DATE, calendar.get(Calendar.DATE)-3);
//    long min = calendar.getTimeInMillis();    
//    query.setIndexFrom(min);
//    query.setIndexTo(max);
    
    MetaList metas = new MetaList();
    metas.setCurrentPage(1);
    metas.setAction("search");
    
//    storage.search(metas, query);
    DatabaseService.getLoader().search(metas, query);
    
    List<Article> articles = metas.getData();
    System.out.println(" ket qua la "+ articles.size());
    for(int i = 0; i < articles.size(); i++) {
//      System.out.println(articles.get(i).getMeta().getTitle());
//      System.out.println(articles.get(i).getMeta().getDesc());
      NLPRecord nlp = articles.get(i).getNlpRecord();
      if(nlp == null) continue;
//      List<String> list = nlp.getData(NLPData.ACTION_OBJECT);
//      System.out.println(list);
//      System.out.println("===============================================");
    }
  }
  
//  private static void testTpt(String phone) {
//    NlpTptStorage storage = NlpTptService.getInstance().getStorage();
//    storage.test();
//    List<NlpTptModel> list = storage.getByMobile(phone);
//    if(list == null) {
//      System.out.println(" null data ");
//      return;
//    }
//    System.out.println(list.size());
//    for(int i = 0; i < list.size(); i++) {
//      NlpTptModel model = list.get(i);
//      System.out.println(model.getAddress());
//    }
//  }
  
  private static void testTpt2(String pattern) throws Exception {
    SolrQuery query = new SolrQuery();
    query = query.setQuery(pattern);
    query = query.setFields("id");
    //    query = query.addSortField("id", SolrQuery.ORDER.desc);
    query = query.setFacetMinCount(1);
    query = query.setFacetLimit(2).setStart(0);
    query = query.setHighlight(false);
    query = query.setQueryType("vshandler");
    
//    System.out.println(IDatabases.getInstance());
    
    SolrIndexStorage storage = (SolrIndexStorage) IDatabases.getInstance();

    QueryResponse response = storage.getSolr().getReader().search(query);
    SolrDocumentList results = response.getResults();
    System.out.println("results = " + results.getNumFound());
    Iterator<SolrDocument> iter = results.iterator();
    while (iter.hasNext()) {
      SolrDocument resultDoc = iter.next();
      String metaId = (String)resultDoc.getFieldValue("id");
      System.out.println(metaId);
      Article article = DatabaseService.getLoader().loadArticle(metaId);
      System.out.println(article.getMeta().getTitle());
      NLPRecord record = article.getNlpRecord();
      List<String> phones = record.getData(NLPData.TELEPHONE);
      for(int i = 0; i < phones.size(); i++) {
        System.out.println(phones.get(i));
      }
      System.out.println(metaId);
    }

  }

  public static void main(String[] args) {
    try {
//      File file = new File(SolrIndexStorage.class.getResource("").toURI());
//      String path = file.getAbsolutePath()+File.separator+".."+File.separator+"..";
//      path += File.separator+".."+File.separator+".."+File.separator+".."+File.separator+"..";
//      path += File.separator + "startup"+File.separator+"src"+File.separator+"test"+File.separator+"data";
//      file  = new File(path);
      
      File file = new File("D:\\java\\test\\data\\");

      //    UtilFile.FOLDER_DATA = file.getCanonicalPath();
      System.setProperty("vietspider.data.path", file.getCanonicalPath());
      System.setProperty("vietspider.test", "true");
      
      
      Application.PRINT = false;
      
      testSearch();
//      testTpt("0918456074");
//      testTpt2("field:phone:0918131313");

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      System.exit(1);
    }
  }
}
