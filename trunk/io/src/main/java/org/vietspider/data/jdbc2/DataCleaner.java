/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.data.jdbc2;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.vietspider.common.io.LogService;
import org.vietspider.common.io.UtilFile;
import org.vietspider.data.jdbc.SqlUtil;
import org.vietspider.db.database.DBScripts;
import org.vietspider.db.database.DatabaseCleaner;
import org.vietspider.db.database.DatabaseService;
import org.vietspider.locale.ParseDateUtils;


/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Feb 9, 2007
 */
public class DataCleaner implements DatabaseCleaner {

  private DBScripts dbScripts;

  public DataCleaner() throws Exception {
    dbScripts = new DBScripts(UtilFile.getFile("system", "dbdelete.xml"), "database");
  }

  public void deleteDomain(String date) throws Exception {
    LogService.getInstance().setMessage("GCEXECUTOR", null, "Delete expire data on date "+date);
    deleteDomain1(date);
    deleteDomain2(date);
  }

  private void deleteDomain1(String date) throws Exception {
    Calendar calendar = new ParseDateUtils().parse(date); 
    SimpleDateFormat idDateFormat = new SimpleDateFormat("yyyyMMdd");
    String idDate = idDateFormat.format(calendar.getTime());

    //    Date dateInstance = CalendarUtils.getDateFormat().parse(date);
    //    SimpleDateFormat idDateFormat = new SimpleDateFormat("yyyyMMdd");
    //    String idDate = idDateFormat.format(dateInstance);

    JdbcService.getInstance().delete("DELETE FROM CONTENT WHERE META_ID LIKE '"+idDate+"%'");
    JdbcService.getInstance().delete("DELETE FROM RELATION  WHERE RELATION_ID LIKE '"+idDate+"%'");
    JdbcService.getInstance().delete("DELETE FROM RELATION  WHERE META_ID LIKE '"+idDate+"%'");
    JdbcService.getInstance().delete("DELETE FROM IMAGE WHERE META_ID LIKE '"+idDate+"%'");
    JdbcService.getInstance().delete("DELETE FROM META WHERE ID LIKE '"+idDate+"%'");
  }

  private void deleteDomain2(String date) throws Exception {
    List<String> metas = new ArrayList<String>();
    JdbcInfo.Conn connection = JdbcService.getInstance().getConn();
    connection.create();
    try { 
      DataGetter getter = (DataGetter)DatabaseService.getLoader();
      String sql = createSQL("$date", date, "loadMetaByDate");
      metas = getter.loadField(sql, connection);
    } finally {
      connection.realse();
    }
    for(String id : metas){
      deleteArticle(id);
    }

    JdbcService.getInstance().delete(createSQL("$date", date, "deleteDomain"));
    JdbcService.getInstance().delete(" DELETE FROM DOMAIN WHERE DATE = '" + date + "'");
  }

  public void deleteArticle(String...ids) throws Exception {
    for(int i = 0; i < ids.length; i++) {
      deleteArticle( ids[i]);
    }
  }

  public void deleteArticle(String id) throws Exception {
    JdbcService.getInstance().delete(createSQL("$id", id, "deleteContent"));
    String sql = createSQL("$id", id, "deleteRelation");
    if(sql.toUpperCase().indexOf("RELATION_ID") < 0) {
      sql += " OR RELATION_ID = " + id;
    }
    JdbcService.getInstance().delete(sql);

    sql = createSQL("$id", id, "deleteImage");
    JdbcService.getInstance().delete(sql);

    sql = createSQL("$id", id, "deleteMeta");
    JdbcService.getInstance().delete(sql);
  }

  public void deleteMeta1(String id) {
    String sql = createSQL("$id", id, "deleteMeta");
    JdbcService.getInstance().delete(sql);
  }

  public void executeSQL(String sql) throws Exception {
    JdbcService.getInstance().delete(sql);
  }

  public void deleteImage(String id) throws Exception {
    String sql = createSQL("$id", id, "deleteImage");
    JdbcService.getInstance().delete(sql);
  }

  public void deleteRelation(String id) throws Exception {
    String sql = createSQL("$id", id, "deleteRelation");
    if(sql.toUpperCase().indexOf("RELATION_ID") < 0) {
      sql += " OR RELATION_ID = " + id;
    }
    JdbcService.getInstance().delete(sql);
  }

  public void deleteContent(String id) throws Exception {
    String sql = createSQL("$id", id, "deleteContent");
    JdbcService.getInstance().delete(sql);
  }

  String createSQL(String name, String value, String loader){
    StringBuilder builder = new StringBuilder(dbScripts.get(loader));    
    new SqlUtil().replace(name, builder, value, false);
    return builder.toString();
  }

  @SuppressWarnings("unused")
  public void deleteExpireDate(File folder, int expire)  {

  }

  public void deleteNoConstraintData(String min) {
    JdbcService.getInstance().setSystemClean(min);
  }





}
