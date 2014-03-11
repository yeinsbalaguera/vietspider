/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db.source.monitor;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Queue;

import org.vietspider.common.text.CalendarUtils;
import org.vietspider.model.Source;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jun 9, 2009  
 */
public class MonitorDataSaver {
  
  protected volatile Queue<SourceLog> waitData ;
  
  MonitorDataSaver(Queue<SourceLog> waitData) {
    this.waitData = waitData;
  }
  
 /* public synchronized void updateCrawlTime(Source source, Calendar calendar, String desc) {
    DateFormat folderFormat = CalendarUtils.getFolderFormat();
    String dateFolder = folderFormat.format(calendar.getTime());
    update(source, dateFolder, 1, 0, 0, 0, 0, desc);
  }*/
  
  public synchronized void updateLastAccess(Source source, Calendar calendar, long lastAccess) {
    DateFormat folderFormat = CalendarUtils.getFolderFormat();
    String dateFolder = folderFormat.format(calendar.getTime());
    update(source, dateFolder, 0, 0, 0, lastAccess, null);
  }

  public synchronized void updateTotalLink(Source source, Calendar calendar, int link) {
    DateFormat folderFormat = CalendarUtils.getFolderFormat();
    String dateFolder = folderFormat.format(calendar.getTime());
    update(source, dateFolder, 0, link, 0, 0, null);
  }

//  public synchronized void updateTotalDownload(Source source, Calendar calendar, long download) {
//    DateFormat folderFormat = CalendarUtils.getFolderFormat();
//    String dateFolder = folderFormat.format(calendar.getTime());
//    update(source, dateFolder, 0, 0, 0, download, 0, null);
//  }

  public synchronized void updateTotalLinkAndData(Source source, Calendar calendar, int link, int data) {
    DateFormat folderFormat = CalendarUtils.getFolderFormat();
    String dateFolder = folderFormat.format(calendar.getTime());
    update(source.getFullName(), dateFolder, 0, link, data, 0, null);
  }

  public synchronized void updateTotalData(String sourceName, String dateFolder, int data) {
    update(sourceName, dateFolder, 0, 0, data, 0, null);
  }

  public synchronized void update(Source source, 
      String dateFolder, int time, int link, int data, long lastAccess, String desc) {
    String sourceName = source.getFullName();
    update(sourceName, dateFolder, time, link, data,  lastAccess, desc);
  }

  public synchronized void update(String sourceName, 
      String dateFolder, int time, int link, int data,
      long lastAccess, String desc)  {

    Iterator<SourceLog> iterator = waitData.iterator();
    while(iterator.hasNext()) {
      SourceLog element = iterator.next();
      if(!element.getName().equals(sourceName) 
          || !element.getDate().equals(dateFolder) ) continue;
      element.setCrawlTime(element.getCrawlTime() + time);
      
      element.setTotalLink(element.getTotalLink() + link);
      element.setTotalData(element.getTotalData()+ data);
      
//      element.setTotalDownload(element.getTotalDownload() + download);
      
      element.setLastAccess(lastAccess);
      if(desc != null && desc.length() > 0) element.setDesc(desc);
      return;
    }
    
    SourceLog sourceLog = new SourceLog();
    sourceLog.setName(sourceName);
    
    sourceLog.setCrawlTime(time);

    sourceLog.setTotalLink(link);
    sourceLog.setTotalData(data);
    
    sourceLog.setLastAccess(lastAccess); 

    sourceLog.setDate(dateFolder);
    if(desc != null) sourceLog.setDesc(desc);

    waitData.add(sourceLog);
  }
  
}
