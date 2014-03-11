/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.data.jdbc2;

import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.vietspider.common.io.LogService;
import org.vietspider.common.text.CalendarUtils;
import org.vietspider.data.jdbc2.JdbcInfo.Conn;
import org.vietspider.locale.ParseDateUtils;
/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jun 30, 2011  
 */
class SystemGC {

  void deleteNoConstraintData(Conn conn, String min) {
    List<String> dates = loadDates(conn);

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

      List<String> sqls = new ArrayList<String>();
      sqls.add(deleteImage);
      sqls.add(deleteRelation);
      sqls.add(deleteContent);
      sqls.add(deleteMeta);

      if(counter < 1) {
        DateFormat idDateFormat = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat dataDateFormat = CalendarUtils.getDateFormat();
        try {
          String dateText = dataDateFormat.format(idDateFormat.parse(date));
          String deleteDomain = " DELETE FROM DOMAIN WHERE DATE LIKE '"+dateText+"%'";
          sqls.add(deleteDomain);
          deleteDomain = " DELETE FROM DOMAIN WHERE \"DATE\" LIKE '"+dateText+"%'";
          sqls.add(deleteMeta);
        } catch (Exception e) {
          LogService.getInstance().setThrowable(e);
        }
      }

      try {
        conn.batch(sqls);
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }

    }

    deleteExpireImages(conn, min);
  }

  private List<String> loadDates(Conn conn)  {
    List<String> dates = new ArrayList<String>();
    try {
      String sql = "SELECT * FROM DOMAIN";
      DateFormat idDateFormat = new SimpleDateFormat("yyyyMMdd");
      try {
        ParseDateUtils parser = new ParseDateUtils();
        ResultSet resultSet = conn.executeQuery(sql);
        while(resultSet.next()) {
          Date instanceDate = parser.parse(resultSet.getString(2)).getTime(); 
          dates.add(idDateFormat.format(instanceDate));
        }
      } catch (Exception e) {
        LogService.getInstance().setMessage(e, e.getMessage());
      }
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    } 
    return dates;
  }

  private void deleteExpireImages(Conn conn, String lastDate) {
    StringBuilder builder = new StringBuilder("SELECT ");
    if(JdbcService.getInstance().isType(JdbcInfo.ORACLE)) {
      builder.append("META_ID FROM IMAGE ROWNUM < 2");
    } else if(JdbcService.getInstance().isType(JdbcInfo.SQLSERVER)) {
      builder.append("TOP 1 META_ID FROM IMAGE");
    } else if(JdbcService.getInstance().isType(JdbcInfo.MYSQL)) {
      builder.append("META_ID FROM IMAGE LIMIT 0,1");
    } else if(JdbcService.getInstance().isType(JdbcInfo.POSTGRES)) {
      builder.append("META_ID FROM IMAGE LIMIT 1");
    } else if(JdbcService.getInstance().isType(JdbcInfo.APACHEDB)) {
      builder.append("META_ID FROM IMAGE");
    } else if(JdbcService.getInstance().isType(JdbcInfo.HSQL)) {
      builder.append("META_ID FROM IMAGE LIMIT 2 OFFSET 1");
    } else {
      builder.append("META_ID FROM IMAGE LIMIT 2 OFFSET 1");
    }

    try {
      conn.setMaxRows(1);
      ResultSet resultSet = conn.executeQuery(builder.toString());
      if(!resultSet.next()) return;
      String date = resultSet.getString(1).substring(0, 8);
      int current = Integer.parseInt(date);
      int min = Integer.parseInt(lastDate);
      if(current >= min) return;
      //      System.out.println(current +"  trong khi "+ min + " < "+(current < min));      
      String sql = "DELETE FROM IMAGE WHERE META_ID LIKE '"+date+"%'";
      conn.execute(sql);

      deleteExpireImages(conn, lastDate);
    } catch (Exception e) {
      LogService.getInstance().setThrowable("GC", e, builder.toString());
    } 
  }
}
