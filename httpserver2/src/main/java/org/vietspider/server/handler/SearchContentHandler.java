/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.server.handler;

import java.io.OutputStream;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.vietspider.bean.Article;
import org.vietspider.bean.ArticleCollection;
import org.vietspider.bean.Domain;
import org.vietspider.bean.Meta;
import org.vietspider.bean.MetaRelation;
import org.vietspider.bean.MetaRelations;
import org.vietspider.bean.Relation;
import org.vietspider.bean.Relations;
import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.db.database.DatabaseService;
import org.vietspider.index.SearchResponse;
import org.vietspider.index.SearchResultCollection;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.XML2Object;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 10, 2009  
 */
public class SearchContentHandler extends CommonHandler {

  @SuppressWarnings("all")
  public void execute(final HttpRequest request, final HttpResponse response,
      final HttpContext context, OutputStream output) throws Exception  {
    Header header = request.getFirstHeader("action");
    String action = header.getValue();

    byte [] bytes = getRequestData(request);


    if("test.connection".equals(action)) {
      output.write("hi".getBytes());
      return;
    }

    if("solr.search.remote".equals(action)) {
      try {
        SearchResponse searcher = XML2Object.getInstance().toObject(SearchResponse.class, bytes);
//        searcher = DatabaseService.getLoader().search(searcher);
        searcher = DatabaseService.getLoader().search(searcher);
//        searcher = CachedService.getInstance().loadSearchResponse(searcher);
        String xml = Object2XML.getInstance().toXMLDocument(searcher).getTextValue();
        output.write(xml.getBytes(Application.CHARSET));
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
      return;
    }
    
    if("solr.search.remote.for.cached".equals(action)) {
      try {
        SearchResponse searcher = XML2Object.getInstance().toObject(SearchResponse.class, bytes);
//        searcher = DatabaseService.getLoader().search(searcher);
        String xml = DatabaseService.getLoader().searchForCached(searcher);
//        String xml = CachedService.getInstance().loadSearchResponseForCached(searcher);
        output.write(xml.getBytes(Application.CHARSET));
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
      return;
    }
    
    if("solr.search.remote.load.articles".equals(action)) {
      try {
        SearchResultCollection collection = 
          XML2Object.getInstance().toObject(SearchResultCollection.class, bytes);
        String xml = DatabaseService.getLoader().loadArticleForSearch(collection);
        output.write(xml.getBytes(Application.CHARSET));
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
      return;
    }

    if("solr.load.article.remote".equals(action)) {
      String metaId = new String(bytes);
      Article article = DatabaseService.getLoader().loadArticle(metaId, Article.NORMAL);
//      Article article = CachedService.getInstance().loadArticle(Article.NORMAL, metaId);
      writeObjectAsXML(output, article);
      return;
    }

    if("solr.load.meta.remote".equals(action)) {
      String metaId = new String(bytes);
      Meta meta = DatabaseService.getLoader().loadMeta(metaId);
//      Meta meta = CachedService.getInstance().loadMeta(metaId);
      writeObjectAsXML(output, meta);
      return;
    }

    if("solr.load.relations.remote".equals(action)) {
      String metaId = new String(bytes);
      List<Relation> list = DatabaseService.getLoader().loadRelation(metaId);
      if(list != null) {
        Relations relations = new Relations(metaId);
        relations.setRelations(list);
        writeObjectAsXML(output, relations);
      }
//      writeObjectAsXML(output, CachedService.getInstance().loadRelations(metaId));
      return;
    }

    if("solr.load.meta.relations.remote".equals(action)) {
      String metaId = new String(bytes);
      List<MetaRelation> list = DatabaseService.getLoader().loadArticle(metaId).getMetaRelations();
      if(list != null) {
        MetaRelations relations = new MetaRelations(metaId);
        relations.setMetaRelations(list);
        writeObjectAsXML(output, relations);
      }
      
      return;
    }

    if("solr.load.domain.remote".equals(action)) {
      String id = new String(bytes);
      Domain domain = DatabaseService.getLoader().loadDomainById(id);
      if(domain != null) writeObjectAsXML(output, domain);
      return;
    }


    if("add.solr.articles".equals(action)) {
      //      ByteArrayInputStream byteInputStream = new ByteArrayInputStream(bytes);
      //      ObjectInputStream objectInputStream = null;

      StringBuilder successCodes = new StringBuilder();
      try {
        ArticleCollection dataCollection = XML2Object.getInstance().toObject(ArticleCollection.class, bytes);
        List<Article> articles = dataCollection.get();
        //        objectInputStream = new ObjectInputStream(byteInputStream);
        //        ArrayList<Article> articles = (ArrayList<Article>)objectInputStream.readObject();
        //        //        System.out.println("==== > "+databases.getClass());
        for(int i = 0; i < articles.size(); i++) {
          Article article = articles.get(i);
          try {
            DatabaseService.getSaver().saveAs(article);
            if(successCodes.length() > 0) successCodes.append("\n");
            successCodes.append(article.getId());
          } catch (Exception e) {
            LogService.getInstance().setThrowable(e);
          }
          //          System.out.println(" ====== > "+ article .getId()+ " : "+article.getContent().getContent().length());
        }
        LogService.getInstance().setMessage("DATABASE", null, 
            "Solr Database: Received " + articles.size() + " articles from remote VietSpider!");
      } catch (Throwable e) {
        LogService.getInstance().setThrowable(e);
      } finally {
        //        try {
        //          if(byteInputStream != null) byteInputStream.close();
        //        } catch (Exception e) {
        //        }
        //        try {
        //          if(objectInputStream != null)  objectInputStream.close();
        //        } catch (Exception e) {
        //        }
      }
      output.write(successCodes.toString().getBytes());
      return;
    }

    LogService.getInstance().setMessage(null, " Not found action: " + action);
  }

  private void writeObjectAsXML(OutputStream output, Object value) throws Exception  {
    if(value == null) {
      output.write(new byte[0]);
      return;
    }
    String xml = Object2XML.getInstance().toXMLDocument(value).getTextValue();
    output.write(xml.getBytes(Application.CHARSET));
  }
  

}
