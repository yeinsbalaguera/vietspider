/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.vietspider.common.text.CalendarUtils;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 9, 2008  
 */
public class DbCleanerService {
  
  private Set<String> set = new HashSet<String>();
  private SimpleDateFormat idFormat = new SimpleDateFormat("yyyyMMdd");
  private SimpleDateFormat folderFormat = CalendarUtils.getFolderFormat();
  
  public DbCleanerService() {
  }
  
  public void add(String id) {
    //check exist date data
    Date date =  null;
    try {
      date = idFormat.parse(id.substring(0, 8));
    } catch (Exception e) {
      return;
    }
    if(date == null) return;
    String dateValue = folderFormat.format(date);
    if(!set.contains(dateValue)) set.add(dateValue);
  }
  
  /*public void clean() {
//    File folder = UtilFile.getFolder("content_1/summary-111/");
//    Iterator<String> iterator = set.iterator();
    SourceLogUtils log  = new SourceLogUtils();
    String [] dateValues = log.loadDate();
    for(String dateValue : dateValues) {
//      String dateValue = iterator.next();
//      File dateFile  = new File(folder, dateValue);
//      if(dateFile.exists()) continue;
//      try {
//        dateValue = dateFormat.format(folderFormat.parse(dateValue));
//      } catch (Exception e) {
//        continue;
//      }
      
//      DbIndexerService.getInstance().deleleIndexedByDate(dateValue);
      
    }
    //end while
  }*/

}
