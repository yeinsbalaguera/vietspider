/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.data.jdbc2;

import java.sql.ResultSet;
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
import org.vietspider.data.jdbc.DateSort;
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
public class DataGetter extends Loader implements DatabaseReader {

  public DataGetter() throws Exception {
    super();
  }

  public List<String> loadDateFromDomain() throws Exception {
    JdbcInfo.Conn connection = JdbcService.getInstance().getConn();
    connection.create();
    try {
      List<String> list = loadField(dbScripts.get("loadDateFromDomain"), connection);
      Collections.sort(list, new DateSort());
      return list;
    } finally {
      connection.realse();
    }
  }

  public Domain loadDomainById(final String id) throws Exception {
    Domain domain = JdbcService.getInstance().loadTempDomain(id);
    if(domain != null) return domain;

    JdbcInfo.Conn connection = JdbcService.getInstance().getConn();
    connection.create();
    try {
      String sql = createSQL("$id", id, "loadDomainById");
      return loadObject(sql.toString(), Domain.class, connection);
    } finally {
      connection.realse();
    }
  }

  public List<Meta> loadMetaFromDomain(String domainId) throws Exception {
    List<Meta>  metas = JdbcService.getInstance().loadTempMetas(domainId);
    JdbcInfo.Conn connection = JdbcService.getInstance().getConn();
    connection.create();
    try {
      String sql = createSQL("$domain", domainId, "loadMetaBySource");
      metas.addAll(loadObjects(sql, Meta.class, connection));
      return metas;
    } finally {
      connection.realse();
    }
  }

  @SuppressWarnings("unchecked")
  public void loadMetaFromDomain(Domain domain, MetaList list) throws Exception {
    List<Article> articles = list.getData();
    if(domain.getName() == null && domain.getCategory() == null) {
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

  private void loadArticles(String sql, MetaList data, Class<? extends Bean<?>>...classes) throws Exception {
    CachedRowSet crs = new CachedRowSetImpl();
    if(sql.indexOf("MAXROWS") > -1) sql = setMaxRows(crs, sql);
    crs.setPageSize(data.getPageSize());
    JdbcInfo.Conn connection = JdbcService.getInstance().getConn();
    try {
      connection.create(
          ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

      ResultSet resultSet = connection.executeQuery(sql);
      crs.populate(resultSet, (data.getCurrentPage() - 1) * data.getPageSize() + 1);
      List<Article> list = data.getData();
      rs2Object.toObjects(crs, Article.class, list, classes);
      for(Article  article : list) {
        article.setMetaRelations(loadMetaRelation(article.getMeta().getId(), connection));
      }
    } finally {
      if(crs != null) crs.close();
      connection.realse();
    }
  }

  public Image loadImage(String id) throws Exception {
    Image image = JdbcService.getInstance().loadTempImage(id);
    if(image != null) return image;
    JdbcInfo.Conn conn = JdbcService.getInstance().getConn();
    conn.create();
    try {
      return loadObject(createSQL("$id", id, "loadImage"), Image.class, conn);
    } finally {
      conn.realse();
    }
  }

  public List<Content> loadContentForMining() throws Exception {
    String sql = dbScripts.get("loadContentForMining");
    JdbcInfo.Conn connection = JdbcService.getInstance().getConn();
    connection.create();
    try {
      return loadObjects(sql, Content.class, connection);
    } finally {
      connection.realse();
    }
  }

  public List<Relation> loadRelation(String metaId) throws Exception {
    List<Relation> relations = JdbcService.getInstance().loadTempRelations(metaId);
    String sql = createSQL("$id", metaId, "loadRelation");
    JdbcInfo.Conn connection = JdbcService.getInstance().getConn();
    connection.create();
    try {
      relations.addAll(loadObjects(sql, Relation.class, connection));
      return relations;
    } finally {
      connection.realse();
    }
  }

  @SuppressWarnings("unchecked")
  public Article loadArticle(String metaId) throws Exception {
    Meta meta = JdbcService.getInstance().loadTempMeta(metaId);
    if(meta != null) {
      Content content = JdbcService.getInstance().loadTempContent(metaId);
      if(content == null) content = loadContent(metaId);
      Domain domain = loadDomainById(meta.getDomain());
      Article article = new Article(domain, meta, content);
      return article;
    }

    JdbcInfo.Conn connection = JdbcService.getInstance().getConn();
    connection.create();
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
      connection.realse();
    }
  }

  @SuppressWarnings("unused")
  public List<Article> loadArticles(String [] metaIds, short mode) throws Exception {
    return loadArticles(metaIds);
  }

  public List<Article> loadArticles(String [] metaIds) throws Exception {
    JdbcInfo.Conn connection = JdbcService.getInstance().getConn();
    connection.create();
    try {
      List<Article> articles = new ArrayList<Article>();
      for (String metaId : metaIds) {
        Article article = new Article();

        Meta meta = loadMeta(metaId);
        if(meta == null) continue;

        article.setMeta(meta);
        article.setMetaRelations(loadMetaRelation(metaId, connection));
        //        System.out.println(article.getMeta().getId()+ " : " + article.getMetaRelation().size());

        Domain domain = loadDomainById(meta.getDomain());
        article.setDomain(domain);

        articles.add(article);
      }
      return articles;
    } finally {
      connection.realse();
    }
  }

  public Content loadContent(String metaId) throws Exception {
    Content content = JdbcService.getInstance().loadTempContent(metaId);
    if(content != null) return content;
    JdbcInfo.Conn connection = JdbcService.getInstance().getConn();
    connection.create();
    try {
      return loadObject(createSQL("$id", metaId, "loadContent"), Content.class, connection);
    } finally {
      connection.realse();
    }
  }

  public Meta loadMeta(String metaId) throws Exception {
    Meta meta = JdbcService.getInstance().loadTempMeta(metaId);
    if(meta != null) return meta;
    JdbcInfo.Conn connection = JdbcService.getInstance().getConn();
    connection.create();
    try {
      return loadObject(createSQL("$id", metaId, "loadMeta"), Meta.class, connection);
    } finally {
      connection.realse();
    }
  }

  public List<Image> loadImages(String metaId) throws Exception {
    List<Image> images = JdbcService.getInstance().loadTempImages(metaId);
    if(images.size() > 0) return images;
    JdbcInfo.Conn connection = JdbcService.getInstance().getConn();
    connection.create();
    try {
      String sql = createSQL("$id", metaId, "loadImages");
      return loadObjects(sql, Image.class, connection);
    } finally {
      connection.realse();
    }
  }

  @SuppressWarnings("unused")
  public IArticleIterator getIterator(Date date, boolean create) { return null; }

  @SuppressWarnings("unused")
  public Article loadArticle(String id, short mode) throws Exception {
    return loadArticle(id);
  }

  @SuppressWarnings("unused")
  public void search(MetaList metas, CommonSearchQuery query) {}

  @SuppressWarnings("unused")
  public SearchResponse search(SearchResponse searcher) { return null; }

  @SuppressWarnings("unused")
  public String loadIdByURL(String url) { return null;}

  @SuppressWarnings("unused")
  public String searchIdByURL(String dateValue, String url) { return null;}

  @SuppressWarnings("unused")
  public String searchForCached(SearchResponse searcher) { return null; }

  @SuppressWarnings("unused")
  public String loadArticleForSearch(SearchResultCollection collection) { return null; }

  public String loadRawText(String id) throws Exception {
    return "not support";
  }
  
  public String loadMetaAsRawText(String id) throws Exception {
    return "not support";
  }
}
