/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.data.jdbc2;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import org.vietspider.bean.Content;
import org.vietspider.bean.Domain;
import org.vietspider.bean.Image;
import org.vietspider.bean.Meta;
import org.vietspider.bean.Relation;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.UtilFile;
import org.vietspider.data.jdbc.SerialClob;
import org.vietspider.data.jdbc.SqlUtil;
import org.vietspider.data.jdbc2.JdbcInfo.Conn;
import org.vietspider.db.database.DBScripts;
import org.vietspider.db.database.DatabaseService;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jun 30, 2011  
 */
class Saver {
  
  private DBScripts dbScripts;
  protected SqlUtil sqlUtil;
  
  public Saver() throws Exception {
    File file = UtilFile.getFile("system", "dbsave.xml");
    dbScripts = new DBScripts(file, "database");
    sqlUtil = new SqlUtil();
  }
  
  List<String> createDomainSQLs(Queue<Domain> queue) throws Exception {
    List<Domain> list = new ArrayList<Domain>();
    List<Domain> saved = new ArrayList<Domain>();
    while(!queue.isEmpty()) {
      Domain domain = queue.poll();
      boolean add = true;
      for(int j = 0; j < list.size(); j++) {
        if(domain.getId().equals(list.get(j))) {
          add = false;
          break;
        }
      }
      if(!add) continue;
      
      for(int j = 0; j < saved.size(); j++) {
        if(domain.getId().equals(saved.get(j))) {
          add = false;
          break;
        }
      }
      if(!add) continue;
      
      try {
        Domain old = DatabaseService.getLoader().loadDomainById(domain.getId());   
        if(old != null) {
          saved.add(old);
          add = false;
        }
      } catch (Exception e) {
        LogService.getInstance().setMessage(e, e.toString());
      }
      
      if(!add) continue;
      
      list.add(domain);
    }

    List<String> sqls = new ArrayList<String>();
    for(int i = 0; i < list.size(); i++) {
      String sql = dbScripts.get("saveDomain");
      sqls.add(sqlUtil.toSql(list.get(i), sql));
    }
    return sqls;
  }

  List<String> createMetaSQLs(Queue<Meta> temp) throws Exception {
    List<Meta> list = new ArrayList<Meta>();
    List<Meta> saved = new ArrayList<Meta>();
    while(!temp.isEmpty()) {
      Meta meta = temp.poll();
      boolean add = true;
      for(int j = 0; j < saved.size(); j++) {
        if(meta.getId().equals(saved.get(j))) {
          add = false;
          break;
        }
      }
      if(!add) continue;
      
      try {
        Meta old = DatabaseService.getLoader().loadMeta(meta.getId());
        if(old != null) {
          saved.add(old);
          add = false;
        }
      } catch (Exception e) {
        LogService.getInstance().setMessage(e, e.toString());
      }
      
      if(!add) continue;
      list.add(meta);
    }
    
    
    List<String> sqls = new ArrayList<String>();
    for(int i = 0; i < list.size(); i++) {
      String sql = dbScripts.get("saveMeta");
      sqls.add(sqlUtil.toSql(list.get(i), sql));
    }
    return sqls;
  }
  
  List<String> createRelSQLs(Queue<Relation> relations) throws Exception {
    List<String> sqls = new ArrayList<String>();
    while(!relations.isEmpty()) {
      String sql = dbScripts.get("saveRelation");
      sqls.add(sqlUtil.toSql(relations.poll(), sql));
    }
    return sqls;
  }
  
  List<String> createUpdateContentSQLs(Queue<Content> contents) throws Exception {
    List<String> sqls = new ArrayList<String>();
    while(!contents.isEmpty()) {
      String sql = dbScripts.get("updateStatus");
      sqls.add(sqlUtil.toSql(contents.poll(), sql));
    }
    return sqls;
  }

  void save(Conn conn, Queue<Content> contents) throws Exception {
    String sql = dbScripts.get("saveContent");
    if(sql.indexOf("$content") > -1){
      List<String> sqls = new ArrayList<String>();
      while(!contents.isEmpty()) {
        String query = sqlUtil.toSql(contents.poll(), sql);
//        System.out.println("bebe " + query);
        sqls.add(query);
      }
      conn.batch(sqls);
      return ;
    }
    
    while(!contents.isEmpty()) {
      Content content = contents.poll();
      PreparedStatement updateContent = null;
      try {
        updateContent = conn.prepared(sqlUtil.toSql(content, sql));
        if(JdbcService.getInstance().isType(JdbcInfo.ORACLE)){
          oracle.sql.CLOB clob = conn.oracleClob();
          clob.setString(1, content.getContent());
          updateContent.setClob(1, clob);       
        } else {
          SerialClob clob = new SerialClob(content.getContent().toCharArray());
          updateContent.setClob(1, clob);
        }     
//        System.out.println(" chuan bi vao day " + updateContent.toString());
        updateContent.executeUpdate();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      } finally {
        conn.releasePrepared();
      }
    }
  }

  void save(Conn conn, Image image) throws Exception {
    String sql = dbScripts.get("saveImage");
    Image old = DatabaseService.getLoader().loadImage(image.getId());
    if(old != null) return;

    PreparedStatement update = conn.prepared(sqlUtil.toSql(image, sql));
    byte [] bytes = image.getImage();
    try {
      update.setBinaryStream(1, new ByteArrayInputStream(bytes), bytes.length);
      //        update.setBytes(1, bytes);
    } catch (Exception e) {
      update.setBinaryStream(1, new ByteArrayInputStream(bytes), bytes.length);
    }
    update.executeUpdate();
    conn.releasePrepared();
  }
  

}
