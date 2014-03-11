/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.data.jdbc2;

import java.io.File;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.sql.RowSet;

import org.vietspider.bean.Article;
import org.vietspider.bean.Bean;
import org.vietspider.bean.MetaRelation;
import org.vietspider.bean.Relation;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.UtilFile;
import org.vietspider.data.jdbc.RS2Object;
import org.vietspider.data.jdbc.SqlUtil;
import org.vietspider.data.jdbc2.JdbcInfo.Conn;
import org.vietspider.db.database.DBScripts;
/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Jan 14, 2007
 */
public class Loader {

  protected DBScripts dbScripts;

  protected RS2Object rs2Object;
  protected SqlUtil sqlUtil;

  public Loader() throws Exception {
    File file = UtilFile.getFile("system", "dbload.xml");
    dbScripts = new DBScripts(file, "database");
    rs2Object = new RS2Object();
    sqlUtil = new SqlUtil();
  }

  public <T extends Bean<?>> T loadObject(String sql,
      Class<T> clazz, Conn connection) throws Exception {
    ResultSet resultSet = connection.executeQuery(sql);
    if(resultSet == null || !resultSet.next()) return null;
    T t = clazz.newInstance();
    rs2Object.toObject(resultSet, t);    
    return t;
  }

  List<String> loadField(String sql, Conn connection) throws Exception {
    if(sql.indexOf("MAXROWS") > -1) sql = connection.setMaxRows(sql);
    ResultSet resultSet = connection.executeQuery(sql);
    List<String> list = new ArrayList<String>();
    while(resultSet.next()){     
      list.add(resultSet.getString(1));
    }    
    return list;
  }

  Bean<?>[] loadObjects(String sql, Conn connection, Class<? extends Bean<?>>...classes) throws Exception {
    if(sql.indexOf("MAXROWS") > -1) sql = connection.setMaxRows(sql);
    //      System.out.println(sql);
    ResultSet resultSet = connection.executeQuery(sql);
    if(resultSet == null || !resultSet.next()) return new Bean[0];
    Bean<?> [] beans = new Bean[classes.length]; 
    for(int i = 0; i < beans.length; i++){
      beans[i] = classes[i].newInstance();
    }
    rs2Object.toObjects(resultSet, beans);    
    return beans;
  }

  <T extends Bean<?>> List<T> loadObjects(String sql, Class<T> clazz, Conn conn,  
      Class<? extends Bean<?>>...classes) throws Exception {
    if(sql.indexOf("MAXROWS") > -1) sql = conn.setMaxRows(sql);
    ResultSet resultSet = conn.executeQuery(sql);
    if(resultSet == null) return null;
    List<T> list = new ArrayList<T>();
    if(classes.length < 1) {
      rs2Object.toObjects(resultSet, clazz, list);
    } else {
      rs2Object.toObjects(resultSet, clazz, list, classes);
    }
    return list;
  }

  Article loadArticle(String sql, Conn connection, Class<? extends Bean<?>>...classes) throws Exception {
    Article article = new Article();
    loadArticle(sql, article, connection, classes);    
    return article;
  }

  void loadArticle(String sql, Article article, Conn connection, Class<? extends Bean<?>>...classes) throws Exception {
    Bean<?> [] beans = loadObjects(sql, connection, classes);
    for(Bean<?> bean : beans){
      article.setField(bean.getField(), bean);
    }
  }

  List<MetaRelation> loadMetaRelation(String metaId, Conn connection) throws Exception {
    String sql = createSQL("$id", metaId, "loadRelation");
    List<Relation> list;
    try {
      list = loadObjects(sql, Relation.class, connection);
    } catch (Exception e) {
      LogService.getInstance().setThrowable("APPLICATION", e, sql);
      throw e;
    }

    List<MetaRelation> relations = new LinkedList<MetaRelation>();
    for(Relation relation : list){
      if(relation.getRelation() == null 
          || relation.getRelation().trim().isEmpty()) continue;
      sql = createSQL("$id", relation.getRelation(), "loadMetaRelation");
      MetaRelation mr;
      try {
        mr = loadObject(sql, MetaRelation.class, connection);
      } catch (Exception e) {
        LogService.getInstance().setThrowable("APPLICATION", e, sql);
        throw e;
      }
      if(mr == null) continue;
      mr.setPercent(relation.getPercent());
      relations.add(mr);
    }
    return relations;

  }

  String setMaxRows(RowSet rowset, String sql) throws Exception {
    String [] eles = sql.split("MAXROWS");
    int max = Integer.parseInt(eles[1].trim());
    rowset.setMaxRows(max);
    return eles[0];
  }  

  String createSQL(String name, String value, String loader){
    StringBuilder builder = new StringBuilder(dbScripts.get(loader));    
    sqlUtil.replace(name, builder, value, false);
    return builder.toString();
  }

  //  public DBScripts getDbScripts() { return dbScripts; }
  //
  //  public RS2Object getRs2Object() { return rs2Object; }
  //
  //  public SqlUtil getSqlUtil() { return sqlUtil; }

}
