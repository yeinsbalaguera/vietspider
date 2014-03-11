/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.data.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.vietspider.bean.Article;
import org.vietspider.bean.Domain;
import org.vietspider.bean.Meta;
import org.vietspider.bean.Relation;
import org.vietspider.common.io.LogService;
import org.vietspider.db.database.DatabaseUtils;
import org.vietspider.db.database.MetaList;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Feb 11, 2007
 */
public class DataUtils extends AbstGetter implements DatabaseUtils {

  public DataUtils() throws Exception {
    super();
  }

  @SuppressWarnings("unchecked")
  public void loadEvent(String date, MetaList data) throws Exception {
    String sql = createSQL("$date", date, "loadEvent");
    Connection connection = JdbcConnection.get();
    List<String> metas;
    try {
      metas = loadField(sql, connection);
    } finally {
      JdbcConnection.release(connection);
    }

    Iterator<String> iter = metas.iterator();
    List<Article> articles = data.getData();
    boolean add;

    while(iter.hasNext()) {
      String id = iter.next();
      add = true;
      for(Article art : articles){
        List<Relation> relations = art.getRelations();
        for(Relation rel : relations) {
          if(rel == null 
              || rel.getRelation() == null) continue;
          
          if(rel.getRelation().equals(id)) {
            add = false;
            break;
          }
        }
        if(!add) break;
      }
      
      if(add){
        Article article = new Article();
        Meta meta = new Meta();
        meta.setId(id);
        article.setMeta(meta);
        sql = createSQL("$id", id, "loadRelation");
        connection = JdbcConnection.get();
        try {
          article.setRelations(loadObjects(sql, Relation.class, connection));
        } finally {
          JdbcConnection.release(connection);
        }
        articles.add(article);
        continue;
      }
      iter.remove();
    }

    Collections.sort(articles, new Comparator<Article>(){
      public int compare(Article relation1, Article relation2){
        return relation2.getRelations().size() - relation1.getRelations().size();
      }
    });

    int total = articles.size();
    int totalPage = total / data.getPageSize() ;
    if (total % data.getPageSize() > 0) totalPage++ ;
    data.setTotalPage(totalPage);

    int currentPage = data.getCurrentPage();
    int from = (currentPage - 1)*data.getPageSize();
    int to = Math.min(currentPage*data.getPageSize(), total);
    List<Article> arts = new ArrayList<Article>();
    for(int i = from; i < to; i++){
      arts.add(articles.get(i));
    }

    connection = JdbcConnection.get();
    try {
      for(Article article : arts){
        try {
          sql = createSQL("$id", article.getMeta().getId(), "loadArticleForEvent");
          loadArticle(sql, article, connection, Meta.class, Domain.class);
          if(article.getMeta() == null) continue;
          article.setMetaRelations(loadMetaRelation(article.getMeta().getId(), connection));
        } catch (Exception e) {
          LogService.getInstance().setThrowable("APPLICATION:", e, sql);
        }
      }
    } finally {
      JdbcConnection.release(connection);
    }
    data.setData(arts);
  }

  @SuppressWarnings("unchecked")
  public void searchMeta(String pattern, MetaList data) throws Exception {
    List<Article> articles = data.getData();

    String sql = createSQL("$content", pattern, "search") ;
    Connection connection = JdbcConnection.get();
    List<String> contents = new ArrayList<String>();
    try {
      contents = loadField(sql, connection);
    } finally {
      JdbcConnection.release(connection);
    }

    int total = contents.size();
    int totalPage = total / data.getPageSize() ;
    if (total % data.getPageSize() > 0) totalPage++ ;
    data.setTotalPage(totalPage);

    int currentPage = data.getCurrentPage();
    int from = (currentPage - 1)*data.getPageSize();
    int to = Math.min(currentPage *data.getPageSize(), total);

    connection = JdbcConnection.get();
    try {
      for(int i = from; i < to; i++){
        sql = createSQL("$id", contents.get(i), "loadArticleForEvent");
        Article article = loadArticle(sql, connection, Meta.class, Domain.class); 
        if(article.getMeta() == null) continue;
        article.setMetaRelations(loadMetaRelation(article.getMeta().getId(), connection));
        articles.add(article);
      }
    } finally {
      JdbcConnection.release(connection); 
    }
  } 

  public List<Meta> loadTopEvent() throws Exception {
    Connection connection = JdbcConnection.get();
    List<Meta> metas = new ArrayList<Meta>();
    try {
//      System.out.println(dbScripts.get("loadTopEventId"));
      List<String> list = loadField(dbScripts.get("loadTopEventId"), connection);
      for(int i = 0; i < list.size(); i++) {
        try {
          String sql =  createSQL("$id", list.get(i), "loadTopEvent");
          Meta meta = loadObject(sql, Meta.class, connection);
          if(meta != null) metas.add(meta);
        } catch (Exception e) {
          LogService.getInstance().setThrowable(e);
        }          
      }
    } finally {
      JdbcConnection.release(connection);
    }
    return metas;
  }

  @SuppressWarnings("unchecked")
  public void loadTopArticles(int top, MetaList list) throws Exception {
    String sql = createSQL("$top", String.valueOf(top), "loadTopArticles");
    Connection connection = JdbcConnection.get();
    try {
      List<Article> arts = loadObjects(sql, Article.class, connection,  Meta.class, Domain.class);
      list.setData(arts);
    } finally {
      JdbcConnection.release(connection);
    }
  }
  
  public void execute(String...sqls) throws Exception  {
    Connection connection = JdbcConnection.get();
    Statement statement = null;
    PreparedStatement updateContent = null;
    try {
      connection.setAutoCommit(false);
      statement = connection.createStatement();
      for(String sql : sqls) {
        statement.execute(sql);
      }
      connection.commit();
      connection.setAutoCommit(true);
    } finally {
      if(statement != null) statement.close();
      if(updateContent != null) updateContent.close();
      JdbcConnection.release(connection);
    }
  }
  

}
