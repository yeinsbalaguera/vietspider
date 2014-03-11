/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db.content;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.vietspider.common.io.LogService;
import org.vietspider.common.text.CalendarUtils;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 18, 2009  
 */
public class ArticleDatabaseUtils {
  
  public static String searchIdByURL(String dateValue, String url) throws Exception {
    SimpleDateFormat simpleDateFormat  = CalendarUtils.getDateFormat();
    Date date  = simpleDateFormat.parse(dateValue);
    ArticleDatabase database = (ArticleDatabase)IDatabases.getInstance().getDatabase(date, true, false);
    if(database == null) {
      return "Not found! Database not found.";
    }
    
    String id = null;
    try {
      id = database.searchId(url);
    } catch (Throwable e) {
      LogService.getInstance().setThrowable(e);
    }
    
    if(id == null) return "Not found!";
    return id;
  }

}
