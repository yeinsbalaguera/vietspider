/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.data.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.sql.rowset.CachedRowSet;

import org.vietspider.bean.Article;
import org.vietspider.bean.Bean;
import org.vietspider.bean.Content;
import org.vietspider.bean.Domain;
import org.vietspider.bean.Image;
import org.vietspider.bean.Meta;
import org.vietspider.bean.Relation;
import org.vietspider.db.database.DatabaseReader;
import org.vietspider.db.database.MetaList;
import org.vietspider.index.CommonSearchQuery;
import org.vietspider.index.SearchResponse;
import org.vietspider.index.SearchResultCollection;

import com.sun.rowset.CachedRowSetImpl;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Feb 8, 2007
 */
public class DataGetter extends AbstGetter implements DatabaseReader {

  public DataGetter() throws Exception {
    super();
  }

  public List<String> loadDateFromDomain() throws Exception {
    Connection connection = JdbcConnection.get();
    try {
      List<String> list = loadField(dbScripts.get("loadDateFromDomain"), connection);
      //      list.add("2009-06-26");
      //      list.add("15-05-2009");
      //      list.add("30-06-2009");
      //      list.add("03.07.2009");
      Collections.sort(list, new DateSort());
      //      for(int i = 0; i < list.size(); i++) {
      //        System.out.println(list.get(i));
      //      }
      return list;
    } finally {
      JdbcConnection.release(connection);
    }
  }

  public Domain loadDomainById(String id) throws Exception {
    Connection connection = JdbcConnection.get();
    try {
      String sql = createSQL("$id", id, "loadDomainById");
      Domain domain = loadObject(sql.toString(), Domain.class, connection);
      return domain;
    } finally {
      JdbcConnection.release(connection);
    }
  }

  public List<Meta> loadMetaFromDomain(String domainId) throws Exception {
    Connection connection = JdbcConnection.get();
    try {
      String sql = createSQL("$domain", domainId, "loadMetaBySource");
      return loadObjects(sql, Meta.class, connection);
    } finally {
      JdbcConnection.release(connection);
    }
  }

  @SuppressWarnings("unchecked")
  public void loadMetaFromDomain(Domain domain, MetaList list) throws Exception {
    List<Article> articles = list.getData();
    if(domain.getName() == null && domain.getCategory() == null){
      String sql = createSQL("$date", domain.getDate(), "loadMetaByDate") ;
      loadArticles(sql, list, Meta.class, Domain.class);
    } else if(domain.getName() == null) {
      StringBuilder sql = new StringBuilder(dbScripts.get("loadMetaByCategory"));
      sqlUtil.replace("$date", sql, domain.getDate(), false);
      sqlUtil.replace("$category", sql, domain.getCategory(), false);
      loadArticles(sql.toString(), list, Meta.class, Domain.class);
    } else {
      String sql = createSQL("$domain",domain.getId(), "loadMetaBySource") ;
      loadArticles(sql.toString(), list, Meta.class);
      for(Article  article : articles){
        article.setDomain(domain);
      }
    } 
  }

  public void loadArticles(String sql, MetaList data, Class<? extends Bean<?>>...classes) throws Exception {
    CachedRowSet crs = new CachedRowSetImpl();
    if(sql.indexOf("MAXROWS") > -1) sql = setMaxRows(crs, sql);
    crs.setPageSize(data.getPageSize());
    Statement statement = null;
    Connection connection = JdbcConnection.get();
    try {
      statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
      ResultSet resultSet = statement.executeQuery(sql);
      crs.populate(resultSet, (data.getCurrentPage() - 1) * data.getPageSize() + 1);
      List<Article> list = data.getData();
      rs2Object.toObjects(crs, Article.class, list, classes);
      crs.close(); 
      for(Article  article : list) {
        article.setMetaRelations(loadMetaRelation(article.getMeta().getId(), connection));
      }
    } finally {
      if(statement != null) statement.close();
      if(crs != null) crs.close();
      JdbcConnection.release(connection);
    }
  }

  /*private int count(String sql) throws Exception {
    StringBuilder builder  = new StringBuilder("SELECT count(META.ID) ");
    builder.append(sql.substring(sql.toUpperCase().indexOf("FROM")));
    int index = builder.toString().toUpperCase().indexOf("ORDER");
    if(index  > 0) builder.setLength(index);

    Statement statement = null;
    Connection connection = JdbcConnection.get();
    try {
      statement = connection.createStatement();
      ResultSet resultSet = statement.executeQuery(builder.toString());
      if(resultSet == null || !resultSet.next()) return 0;
      int total = resultSet.getInt(1);
      statement.close();
      return total;
    }finally{
      if(statement != null) statement.close();
      JdbcConnection.release(connection);
    }
  }*/

  public Image loadImage(String id) throws Exception {
    Connection connection = JdbcConnection.get();
    try {
      return loadObject(createSQL("$id", id, "loadImage"), Image.class, connection);
    } finally {
      JdbcConnection.release(connection);
    }
  }

  public List<Content> loadContentForMining() throws Exception {
    String sql = dbScripts.get("loadContentForMining");
    Connection connection = JdbcConnection.get();
    try {
      return loadObjects(sql, Content.class, connection);
    } finally { 
      JdbcConnection.release(connection);
    }
  }

  public List<Relation> loadRelation(String metaId) throws Exception{
    String sql = createSQL("$id", metaId, "loadRelation");
    Connection connection = JdbcConnection.get();
    try {
      return loadObjects(sql, Relation.class, connection);
    } finally {
      JdbcConnection.release(connection);
    }
  }

  @SuppressWarnings("unchecked")
  public Article loadArticle(String metaId) throws Exception {
    Connection connection = JdbcConnection.get();
    try {
      String sql = createSQL("$id", metaId, "loadArticle");
      Article article = loadArticle(sql, connection, Meta.class, Content.class, Domain.class);   
      if(article.getMeta() != null){
        article.setMetaRelations(loadMetaRelation(article.getMeta().getId(), connection));
      } else {
        sql = "SELECT META.ID, META.URL FROM META WHERE META.ID = " + metaId;
        article.setMeta(loadObject(sql, Meta.class, connection));
      }

      return article;
    } finally {
      JdbcConnection.release(connection);
    }
  }
  
  public List<Article> loadArticles(String [] metaIds, short mode) throws Exception {
    return loadArticles(metaIds);
  }

  public List<Article> loadArticles(String [] metaIds) throws Exception {
    Connection connection = JdbcConnection.get();
    List<Article> articles = new ArrayList<Article>();
    try {
      for (String metaId : metaIds) {
        Article article = new Article();
        Meta meta = loadObject(
            createSQL("$id", metaId, "loadMeta"), Meta.class, connection);
        if(meta == null) continue;
        article.setMeta(meta);
        article.setMetaRelations(loadMetaRelation(metaId, connection));
        //        System.out.println(article.getMeta().getId()+ " : " + article.getMetaRelation().size());

        String sql = createSQL("$id", meta.getDomain(), "loadDomainById");
        Domain domain = loadObject(sql.toString(), Domain.class, connection);
        article.setDomain(domain);
        articles.add(article);
      }
    } finally {
      JdbcConnection.release(connection);
    }
    return articles;
  }

  public Content loadContent(String metaId) throws Exception {
    Connection connection = JdbcConnection.get();
    try {
      return loadObject(
          createSQL("$id", metaId, "loadContent"), Content.class, connection);
    } finally {
      JdbcConnection.release(connection);
    }
  }

  public Meta loadMeta(String metaId) throws Exception {
    Connection connection = JdbcConnection.get();
    try {
      return loadObject(createSQL("$id", metaId, "loadMeta"), Meta.class, connection);
    } finally {
      JdbcConnection.release(connection);
    }
  }

  public List<Image> loadImages(String metaId) throws Exception {
    Connection connection = JdbcConnection.get();
    try {
      String sql = createSQL("$id", metaId, "loadImages");
      return  loadObjects(sql, Image.class, connection);
    } finally {
      JdbcConnection.release(connection);
    }
  }

  @Override
  public IArticleIterator getIterator(Date date, boolean create) { return null; }

  @SuppressWarnings("unused")
  public Article loadArticle(String id, short mode) throws Exception {
    return loadArticle(id);
  }


  public void search(MetaList metas, CommonSearchQuery query) {}

  public SearchResponse search(SearchResponse searcher) { return null; }

  public String loadIdByURL(String url) { return null;}

  public String searchIdByURL(String dateValue, String url) { return null;}

  public String searchForCached(SearchResponse searcher) { return null; }
  
  public String loadArticleForSearch(SearchResultCollection collection) { return null; }
  
  public String loadRawText(String id) throws Exception {
    return "not support";
  }
  
  public String loadMetaAsRawText(String id) throws Exception {
    return "not support";
  }
}
