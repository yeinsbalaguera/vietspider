/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.data.jdbc;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.vietspider.common.io.LogService;
import org.vietspider.common.io.UtilFile;
import org.vietspider.common.text.CalendarUtils;
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

    Connection connection = JdbcConnection.get();
    Statement statement = connection.createStatement();
    try {
      String sql = "DELETE FROM CONTENT WHERE META_ID LIKE '"+idDate+"%'";
      statement.executeUpdate(sql);
    } catch (Exception exp) {
      LogService.getInstance().setMessage("GC", exp, exp.toString());
    }

    try {
      String sql = "DELETE FROM RELATION  WHERE RELATION_ID LIKE '"+idDate+"%'";
      statement.executeUpdate(sql);
    } catch (Exception exp) {
      LogService.getInstance().setMessage("GC", exp, exp.toString());
    }

    try {
      String sql = "DELETE FROM RELATION  WHERE META_ID LIKE '"+idDate+"%'";
      statement.executeUpdate(sql);
    } catch (Exception exp) {
      LogService.getInstance().setMessage("GC", exp, exp.toString());
    }

    try {
      String sql = "DELETE FROM IMAGE WHERE META_ID LIKE '"+idDate+"%'";
      statement.executeUpdate(sql);
    } catch (Exception exp) {
      LogService.getInstance().setMessage("GC", exp, exp.toString());
    }

    try {
      String  sql = "DELETE FROM META WHERE ID LIKE '"+idDate+"%'";
      statement.executeUpdate(sql);
    } catch (Exception exp) {
      LogService.getInstance().setMessage("GC", exp, exp.toString());
    }

    if(statement != null) statement.close();
    JdbcConnection.release(connection);
  }

  private void deleteDomain2(String date) throws Exception {
    List<String> metas = new ArrayList<String>();
    Connection connection = JdbcConnection.get();
    try {
      DataGetter getter = (DataGetter)DatabaseService.getLoader();
      String sql = createSQL("$date", date, "loadMetaByDate");
      metas = getter.loadField(sql, connection);
    } finally {
      JdbcConnection.release(connection);
    }

    for(String id : metas){
      deleteArticle(id);
      /*deleteContent(id);
      deleteRelation(id);
      deleteImage(id);
      deleteMeta1(id);*/
    }

    Statement statement = null;
    connection = JdbcConnection.get();
    try {
      statement = connection.createStatement();

      for(String id : metas){
        deleteArticle(statement, id);
      }

      deleteDomain(statement, date);
    } finally{
      if(statement != null) statement.close();
      JdbcConnection.release(connection);
    }
  }

  /* public void deleteFilter(String name) throws Exception {
    String sql = createSQL("$name", name, "deleteFilter");
    Statement statement = null;
    Connection connection = JdbcConnection.get();
    try {
      statement = connection.createStatement();
      statement.executeUpdate(sql);
      statement.close(); 
    }finally{
      if(statement != null) statement.close();
      JdbcConnection.release(connection);
    }
  }*/
  
  private void deleteDomain(Statement statement, String date) { 
    String sql = createSQL("$date", date, "deleteDomain");
    try {
      int delete = statement.executeUpdate(sql);
      if(delete != 0) return;
      LogService.getInstance().setMessage(null, sql);
    } catch (Exception e) {
      LogService.getInstance().setThrowable("GCEXECUTOR", e, sql);
    }
    
    /*if(delete == 0) {
      LogService.getInstance().setMessage(null, sql);
      if(dateValue.indexOf(' ') > -1) {
        dateValue = dateValue.substring(0, dateValue.indexOf(' '));
      }
      sql = " DELETE FROM DOMAIN WHERE DATE LIKE '"+dateValue+"%'";
      delete = statement.executeUpdate(sql);
    }*/
      
    try {
      sql = " DELETE FROM DOMAIN WHERE DATE = '" + date + "'";
      int delete = statement.executeUpdate(sql);
      if(delete != 0) return;
      LogService.getInstance().setMessage(null, sql);
    } catch (Exception e) {
      LogService.getInstance().setThrowable("GCEXECUTOR", e, sql);
    }
    
  }

  public void deleteArticle(String...ids) throws Exception {
    Statement statement = null;
    Connection connection = JdbcConnection.get();
    try {
      statement = connection.createStatement();
      for(int i = 0; i < ids.length; i++) {
        deleteArticle(statement, ids[i]);
      }
    } finally {
      if(statement != null) statement.close();
      JdbcConnection.release(connection);
    }
  }

  public void deleteArticle(Statement statement, String id) throws Exception {
    StringBuilder builder = new StringBuilder();
    Exception exception = null ;

    String sql = createSQL("$id", id, "deleteContent");
    try {
      statement.executeUpdate(sql);
    } catch (Exception e) {
      exception  = e;
      builder.append(e.toString()).append('\n');
    }

    sql = createSQL("$id", id, "deleteRelation");
    if(sql.toUpperCase().indexOf("RELATION_ID") < 0) {
      sql += " OR RELATION_ID = " + id;
    }
    try {
      statement.executeUpdate(sql);
    } catch (Exception e) {
      exception  = e;
      builder.append(e.toString()).append('\n');
    }


    sql = createSQL("$id", id, "deleteImage");
    try {
      statement.executeUpdate(sql);
    } catch (Exception e) {
      exception  = e;
      builder.append(e.toString()).append('\n');
    }

    sql = createSQL("$id", id, "deleteMeta");
    try {
      statement.executeUpdate(sql);
    } catch (Exception e) {
      builder.append(e.toString()).append('\n');
    }

    if(exception == null) return;
    Exception exp = new Exception(builder.toString());
    exp.setStackTrace(exception.getStackTrace());
    throw exp;
  }

  public void deleteMeta1(String id) throws Exception {
    String sql = createSQL("$id", id, "deleteMeta");
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

  public void executeSQL(String sql) throws Exception {
    Statement statement = null;
    Connection connection = JdbcConnection.get();
    try {
      statement = connection.createStatement();
      statement.executeUpdate(sql);
    } finally {
      if(statement != null) statement.close();
      JdbcConnection.release(connection);
    }
  }

  public void deleteImage(String id) throws Exception {
    String sql = createSQL("$id", id, "deleteImage");
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

  public void deleteRelation(String id) throws Exception {
    String sql = createSQL("$id", id, "deleteRelation");
    if(sql.toUpperCase().indexOf("RELATION_ID") < 0) {
      sql += " OR RELATION_ID = " + id;
    }
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

  public void deleteContent(String id) throws Exception {
    String sql = createSQL("$id", id, "deleteContent");
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

  String createSQL(String name, String value, String loader){
    StringBuilder builder = new StringBuilder(dbScripts.get(loader));    
    new SqlUtil().replace(name, builder, value, false);
    return builder.toString();

  }

  public void deleteNoConstraintData(String min) {
    List<String> dates = loadDates();

    int minValue = Integer.parseInt(min);
    for(String date : dates) {
      int dateInt = Integer.parseInt(date);
      if(dateInt >= minValue) continue;
      
      LogService.getInstance().setMessage("GCEXECUTOR", null, "Delete no constraint data on date "+date);

      String deleteImage = " DELETE FROM IMAGE WHERE META_ID LIKE '"+date+"%'";
      String deleteContent = " DELETE FROM CONTENT WHERE META_ID LIKE '"+date+"%'";
      String deleteRelation = " DELETE FROM RELATION WHERE META_ID LIKE '"
        + date + "%'  OR RELATION_ID LIKE '" + date + "%'";
      String deleteMeta = " DELETE FROM META WHERE ID LIKE '"+date+"%'";

      int counter = 0;

      Statement statement = null;
      Connection connection = null;

      try {
        connection = JdbcConnection.get();
        statement = connection.createStatement();

        try {
          int value  = statement.executeUpdate(deleteImage);
          if(value > 0) counter = value;
        } catch (Exception e) {
          LogService.getInstance().setMessage(e, e.getMessage());
        }

        try {
          int value  = statement.executeUpdate(deleteRelation);
          if(value > 0) counter = value;
        } catch (Exception e) {
          LogService.getInstance().setMessage(e, e.getMessage());
        }

        try {
          int value  =statement.executeUpdate(deleteContent);
          if(value > 0) counter = value;
        } catch (Exception e) {
          LogService.getInstance().setMessage(e, e.getMessage());
        }

        try {
          int value = statement.executeUpdate(deleteMeta);
          if(value > 0) counter = value;
        } catch (Exception e) {
          LogService.getInstance().setMessage(e, e.getMessage());
        }

        if(counter < 1) {
          DateFormat idDateFormat = new SimpleDateFormat("yyyyMMdd");
          SimpleDateFormat dataDateFormat = CalendarUtils.getDateFormat();
          String dateText = dataDateFormat.format(idDateFormat.parse(date));
          String deleteDomain = " DELETE FROM DOMAIN WHERE DATE LIKE '"+dateText+"%'";
          try {
            statement.executeUpdate(deleteDomain);
          } catch (Exception e) {
            deleteDomain = " DELETE FROM DOMAIN WHERE \"DATE\" LIKE '"+dateText+"%'";
            try {
              statement.executeUpdate(deleteMeta);
            } catch (Exception e2) {
              LogService.getInstance().setMessage(e, e.getMessage());
              LogService.getInstance().setMessage(e2, e.getMessage());
            }
          }
        }
        statement.close();
        JdbcConnection.release(connection);
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      } finally {
        try {
          if(statement != null) statement.close();
        } catch (Exception e) {
          LogService.getInstance().setThrowable(e);
        }
        JdbcConnection.release(connection);
      }
    }
    deleteExpireImages(min);
  }

  private List<String> loadDates()  {
    Statement statement = null;
    Connection connection = null;
    List<String> dates = new ArrayList<String>();
    try {
      connection = JdbcConnection.get();
      statement = connection.createStatement();

      String sql = "SELECT * FROM DOMAIN";
      DateFormat idDateFormat = new SimpleDateFormat("yyyyMMdd");
      try {
        ParseDateUtils parser = new ParseDateUtils();
        ResultSet resultSet = statement.executeQuery(sql);
        while(resultSet.next()) {
          Date instanceDate = parser.parse(resultSet.getString(2)).getTime(); 
          dates.add(idDateFormat.format(instanceDate));
        }
      } catch (Exception e) {
        LogService.getInstance().setMessage(e, e.getMessage());
      }
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    } finally {
      try {
        if(statement != null) statement.close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
      JdbcConnection.release(connection);
    }
    return dates;
  }

  private void deleteExpireImages(String lastDate) {
    StringBuilder builder = new StringBuilder("SELECT ");
    if(JdbcConnection.isType(DBConnections.ORACLE)) {
      builder.append("META_ID FROM IMAGE ROWNUM < 2");
    } else if(JdbcConnection.isType(DBConnections.SQLSERVER)) {
      builder.append("TOP 1 META_ID FROM IMAGE");
    } else if(JdbcConnection.isType(DBConnections.MYSQL)) {
      builder.append("META_ID FROM IMAGE LIMIT 0,1");
    } else if(JdbcConnection.isType(DBConnections.POSTGRES)) {
      builder.append("META_ID FROM IMAGE LIMIT 1");
    } else if(JdbcConnection.isType(DBConnections.APACHEDB)) {
      builder.append("META_ID FROM IMAGE");
    } else if(JdbcConnection.isType(DBConnections.HSQL)) {
      builder.append("META_ID FROM IMAGE LIMIT 2 OFFSET 1");
    } else {
      builder.append("META_ID FROM IMAGE LIMIT 2 OFFSET 1");
    }

    Statement statement = null;
    Connection connection =  null;
    try {
      connection = JdbcConnection.get();
      statement = connection.createStatement();
      statement.setMaxRows(1);
      ResultSet resultSet = statement.executeQuery(builder.toString());
      if(!resultSet.next()) return;
      String date = resultSet.getString(1).substring(0, 8);
      int current = Integer.parseInt(date);
      int min = Integer.parseInt(lastDate);
      if(current >= min) return;
//      System.out.println(current +"  trong khi "+ min + " < "+(current < min));      
      String sql = "DELETE FROM IMAGE WHERE META_ID LIKE '"+date+"%'";
      statement.executeUpdate(sql);
      
      if(statement != null) statement.close();
      JdbcConnection.release(connection);
      
      deleteExpireImages(lastDate);
      
    } catch (Exception e) {
      LogService.getInstance().setThrowable("GC", e, builder.toString());
    } finally {
      try {
        if(statement != null) statement.close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable("GC", e);
      }
      JdbcConnection.release(connection);
    }
  }
  
  public void deleteExpireDate(File folder, int expire)  {
    
  }

}
