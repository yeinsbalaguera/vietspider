/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.io.websites2;

import java.io.File;
import java.util.Calendar;

import org.vietspider.common.io.LogService;
import org.vietspider.common.io.UtilFile;
import org.vietspider.db.website.WebsiteDatabases;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 1, 2009  
 */
public class GenerateWebsiteScheduler extends Thread  {

  private static volatile GenerateWebsiteScheduler instance;

  public static final GenerateWebsiteScheduler createService(WebsiteDatabases databases) {
    if(instance != null) return instance;
    instance = new GenerateWebsiteScheduler(databases);
    return instance;
  }

  private short [] hours = {-1};
  
  private GenerateWebsiteExecutor executor;
  
  private volatile boolean generateNow  = false;
  
  private GenerateWebsiteScheduler(WebsiteDatabases databases) {
    this.executor = new GenerateWebsiteExecutor(databases);
    this.start();
  }

  public void setSchedule(short [] values) { hours = values; }

  private boolean isWorkingHour() {
    Calendar calendar = Calendar.getInstance();

    File loadFile = UtilFile.getFile("system", "web.generate");
    Calendar lastModifiedCalendar = Calendar.getInstance();
    lastModifiedCalendar.setTimeInMillis(loadFile.lastModified());
    
    int lastHour = lastModifiedCalendar.get(Calendar.HOUR_OF_DAY); 
    int lastDate = lastModifiedCalendar.get(Calendar.DAY_OF_MONTH); 
    int hour = calendar.get(Calendar.HOUR_OF_DAY);
    int date = calendar.get(Calendar.DAY_OF_MONTH);
    if(lastHour == hour && lastDate == date) return false;
    
    for(int i = 0; i < hours.length; i++) {
      if(hour == hours[i]) return true;
    }
    return false;
  }

  public void run() {
    while(true) {
      try {
        if(isWorkingHour()) {
          generate();
        } else if(generateNow) {
          generateNow = false;
          generate();
        }
      } catch (Exception e) {
        LogService.getInstance().setThrowable("SITE", e);
      }

      try {
        Thread.sleep(60*1000);
      } catch (Exception e) {
      }
    }
  }

  public void generate() throws Exception {
    LogService.getInstance().setMessage("SITE", null, "Start generate websites to sources...");
    UtilFile.deleteFolder(UtilFile.getFolder("sources/sources/SITE"));
    UtilFile.deleteFolder(UtilFile.getFolder("sources/sources/BLOG"));
    
    executor.generate("vn");
    executor.generate("en");
    executor.generate("*");
    LogService.getInstance().setMessage("SITE", null, "Finish generate websites to sources...");
  }
  

  public void setGenerateNow(boolean generateNow) {
    this.generateNow = generateNow;
  }

}
