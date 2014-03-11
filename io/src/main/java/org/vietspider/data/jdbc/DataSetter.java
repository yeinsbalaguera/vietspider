/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.data.jdbc;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Calendar;
import java.util.List;

import org.vietspider.bean.Article;
import org.vietspider.bean.Content;
import org.vietspider.bean.Domain;
import org.vietspider.bean.Image;
import org.vietspider.bean.Meta;
import org.vietspider.bean.Relation;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.UtilFile;
import org.vietspider.common.text.CalendarUtils;
import org.vietspider.db.database.DBScripts;
import org.vietspider.db.database.DatabaseService;
import org.vietspider.db.database.DatabaseWriter;
/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Feb 8, 2007
 */
public class DataSetter implements DatabaseWriter {

  private DBScripts dbScripts;
  protected SqlUtil sqlUtil;

  public DataSetter() throws Exception {
    File file = UtilFile.getFile("system", "dbsave.xml");
    dbScripts = new DBScripts(file, "database");
    sqlUtil = new SqlUtil();
  }
  
  public void save(Article article) throws Exception {
//    if(Application.LICENSE == Install.SEARCH_SYSTEM) {
//      SyncDbService.getService().save(article);
//    }
    save1(article.getMeta(), article.getDomain(), article.getContent());
    
    List<Image> images = article.getImages();
    for(int i = 0; i < images.size(); i++) {
      try {
        save(images.get(i));
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
  }
  
  public void saveAs(Article article) throws Exception {
    save1(article.getMeta(), article.getDomain(), article.getContent());
  }

  @Deprecated()
  public void save(Meta meta, Domain domain, Content content) throws Exception {
    save1(meta, domain, content);
  }

  private void save1(Meta meta, Domain domain, Content content) throws Exception {
    Connection connection = JdbcConnection.get();
    Statement statement = null;
    try {
      connection.setAutoCommit(false);
      statement = connection.createStatement();

      if(domain != null) {
        int updateDomain =  save(statement, domain);
        if(!connection.getAutoCommit()) connection.commit();
        if(updateDomain <= 0) {
          connection.rollback();
          return;
        } 
        if(!connection.getAutoCommit()) connection.commit();
      }

      if(meta != null) {
        int updateMeta =  save(statement, meta);
        if(updateMeta <= 0) {
          connection.rollback();
          return;
        }
      }

      if(content != null) {
        int updateContent = save(connection, statement, content);
        if(updateContent <= 0) {
          connection.rollback();
          return;
        }
      }

      //      System.out.println(updateDomain + " : " + updateMeta + "  : " + updateDomain);

      if(!connection.getAutoCommit())  connection.commit();
    } catch (Exception e) {
      connection.rollback();
      throw e;
    } finally {
      if(statement != null) statement.close();
      connection.setAutoCommit(true);
      JdbcConnection.release(connection);
    }
  }

  private String cacheId;

  private int save(Statement statement, Domain domain) throws Exception {
    String id  = domain.getId();
    if(cacheId != null && cacheId.equals(id)) return 1;
    cacheId = id;
    try {
      Domain old = DatabaseService.getLoader().loadDomainById(id);   
      if(old != null) return 1;
    } catch (Exception e) {
      LogService.getInstance().setMessage(e, e.toString());
    }

    String sql = dbScripts.get("saveDomain");
    sql = sqlUtil.toSql(domain, sql);

    return statement.executeUpdate(sql);
  }

  private int save(Statement statement, Meta meta) throws Exception {
    String sql = dbScripts.get("saveMeta");
    sql = sqlUtil.toSql(meta, sql);
    Meta old = DatabaseService.getLoader().loadMeta(meta.getId());
    if(old != null) return 1;
    //    System.out.println("MetaSave-Did=" +meta.getDomain());
    return statement.executeUpdate(sql);
  }

  private int save(Connection connection, Statement statement, Content content) throws Exception {
    PreparedStatement updateContent = null;
    try {
      String sql = dbScripts.get("saveContent");

      if(sql.indexOf("$content") > -1){
        sql = sqlUtil.toSql(content, sql);
        return statement.executeUpdate(sql);
      }
      
      sql = sqlUtil.toSql(content, sql);
      updateContent = connection.prepareStatement(sql);
      if(JdbcConnection.isType(DBConnections.ORACLE)){
        oracle.sql.CLOB clob= oracle.sql.CLOB.createTemporary(connection, true, oracle.sql.CLOB.DURATION_SESSION);
        clob.setString(1, content.getContent());
        updateContent.setClob(1, clob);       
      } else {
        SerialClob clob = new SerialClob(content.getContent().toCharArray());
        updateContent.setClob(1, clob);
      }     
      return updateContent.executeUpdate();
    } finally {
      updateContent.close();
    }
  }

  public void set(Content content) throws Exception {
    String sql = dbScripts.get("updateStatus");
    sql = sqlUtil.toSql(content, sql);

    Statement statement = null;
    Connection connection = JdbcConnection.get();
    try {
      statement = connection.createStatement();
      statement.executeUpdate(sql);
      statement.close();
    } finally{
      if(statement != null) statement.close();
      JdbcConnection.release(connection);
    }
  }

  public void save(Image image) throws Exception {
    String sql = dbScripts.get("saveImage");
    sql = sqlUtil.toSql(image, sql);
    Image old = DatabaseService.getLoader().loadImage(image.getId());
    if(old != null) return ;
    PreparedStatement update = null;
    Connection connection = JdbcConnection.get();
    try {
      connection.setAutoCommit(false);
      update = connection.prepareStatement(sql);
      byte [] bytes = image.getImage();
      try{
        update.setBinaryStream(1, new ByteArrayInputStream(bytes), bytes.length);
        //        update.setBytes(1, bytes);
      } catch (Exception e) {
        update.setBinaryStream(1, new ByteArrayInputStream(bytes), bytes.length);
      }
      update.executeUpdate();
      if(!connection.getAutoCommit()) connection.commit();
      update.close();
    } finally {
      if(update != null) update.close();
      connection.setAutoCommit(true);
      JdbcConnection.release(connection);
    }
  }

  public void save(Relation relation) throws Exception {
    String sql = dbScripts.get("saveRelation");
    sql = sqlUtil.toSql(relation, sql);

    Statement statement = null;
    Connection connection = JdbcConnection.get();
    try {
      statement = connection.createStatement();
      statement.executeUpdate(sql);
      statement.close();
    } finally {
      if(statement != null) statement.close();
      JdbcConnection.release(connection);
    }
  }

  public void save(List<Relation> relations) throws Exception {
    if(relations == null) return;
    String sql = dbScripts.get("saveRelation");
    Statement statement = null;
    Connection connection = JdbcConnection.get();
    try {
      statement = connection.createStatement();
      for(Relation relation : relations) {
        statement.addBatch(sqlUtil.toSql(relation, sql));
      }
      statement.executeBatch();
      statement.close();
    } finally {
      if(statement != null) statement.close();
      JdbcConnection.release(connection);
    }
  }
  
  public void save(String metaId, String title, String desc, String newContent)  throws Exception {
    String updateTitle = "UPDATE META SET TITLE = ";
    String updateDesc = "UPDATE META SET DES = ";
    
    if(JdbcConnection.isType(DBConnections.MYSQL) 
        || JdbcConnection.isType(DBConnections.SQLSERVER)) {
      updateTitle += "N'" + title + "' WHERE ID = " + metaId;
      updateDesc += "N'" + desc  + "' WHERE ID = " + metaId;
    } else {
      updateTitle += "'" + title + "' WHERE ID = " + metaId;
      updateDesc += "'" + desc  + "' WHERE ID = " + metaId;
    }
    DatabaseService.getUtil().execute(updateTitle, updateDesc);
    
    if(newContent == null) return;
    
    DatabaseService.getDelete().deleteContent(metaId);
    
    Calendar calendar = Calendar.getInstance();
    String date = CalendarUtils.getDateFormat().format(calendar.getTime());
    Content content = new Content(metaId, date, newContent);

    DatabaseService.getSaver().save(new Article(null, null, content));
  }
  
}
