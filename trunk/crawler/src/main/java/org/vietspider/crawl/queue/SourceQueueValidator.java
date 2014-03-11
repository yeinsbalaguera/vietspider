/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.queue;

import java.io.File;
import java.util.Calendar;

import org.vietspider.common.Application;
import org.vietspider.common.io.UtilFile;
import org.vietspider.common.text.NameConverter;
import org.vietspider.db.SystemProperties;
import org.vietspider.db.link.track.LinkLogStorages;
import org.vietspider.io.model.GroupIO;
import org.vietspider.model.Group;
import org.vietspider.model.Groups;
import org.vietspider.model.Source;
import org.vietspider.model.SourceIO;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jun 9, 2008  
 */
final class SourceQueueValidator {
  
  private static volatile SourceQueueValidator validator;
  
  static synchronized  void createInstance() {
    validator = new SourceQueueValidator();
  }
  
  public static SourceQueueValidator getInstance() { return validator; }
  
  private int weight = 30;
  private Groups groups;

  private SourceQueueValidator() {
    SystemProperties system = SystemProperties.getInstance();
    try {
      weight = Integer.parseInt(system.getValue(Application.WEIGHT_PRIORITY_EXECUTOR));
    } catch (Exception e) {
      weight = 30;
    }
    this.groups = GroupIO.getInstance().loadGroups();
  }
  
  public boolean validate(Source source, long accessCode) {
    if(source == null) return false;
    int priority = source.getPriority();
    
    String [] addresses = source.getHome();
    if(addresses.length < 1) {
      LinkLogStorages.getInstance().save(source, "{no.homepages}");
      return false;
    }
    
    if(priority == -1) {
      LinkLogStorages.getInstance().save(source, "{crawl.disable.by.priority}");
      return false;
    }
    
    if(weight < 1 || priority == 0) return true;
    
    Calendar calendar = Calendar.getInstance();
    Group group = groups.getGroup(source.getGroup());

    int startDownloadTime = group.getStartTime();
    int endDownloadTime = group.getEndTime();
    int hour = calendar.get(Calendar.HOUR_OF_DAY);
    
    if(group.isDownloadInRangeTime()) {
      if(hour < startDownloadTime || hour > endDownloadTime)  {
        LinkLogStorages.getInstance().save(source, "{out.of.crawl.time.in.group}");
        return false;
      }
    } else {
      if(hour >= startDownloadTime && hour <= endDownloadTime) {
        LinkLogStorages.getInstance().save(source, "{out.of.crawl.time.in.group}");
        return false;
      }
    }
    
    if(accessCode == -1) return true;
    
    String folderName  = NameConverter.encode(source.getFullName());
    File folder = new File(UtilFile.getFolder("track/link/"), folderName);
    File [] files = null;
    if(folder.exists() && (files = folder.listFiles()) != null && files.length > 0) return true;
    
    long currentTime = calendar.getTimeInMillis();
//    System.out.println("============================= " + source + "===================");
//    System.out.println("==== access code " + accessCode );
//    System.out.println("==== current time " + currentTime);
//    System.out.println(" === > "+ (currentTime - accessCode));
//    System.out.println(" ===== > " + (priority*weight*60*1000));
//    System.out.println("============================= ===================================");
    if((currentTime - accessCode) >= priority*weight*60*1000){
      return true;
    }
    LinkLogStorages.getInstance().save(source, "{not.in.crawl.time}");
    return false;
  }
  
  boolean shortValidate(Source source) {
    if(source == null) return false;
    int priority = source.getPriority();
    
    if(priority == -1) {
      LinkLogStorages.getInstance().save(source, "{crawl.disable.by.priority}");
      return false;
    }
    
    if(priority == 0) return true;
    
    Calendar calendar = Calendar.getInstance();
    Group group = groups.getGroup(source.getGroup());
    
    if(priority < group.getMinPriority()) {
      SourceIO.getInstance().savePriority(source, 0);
      source.setPriority(0);
      priority = source.getPriority();
    } else if(priority > group.getMaxPriority()) {
      SourceIO.getInstance().savePriority(source, group.getMaxPriority());
      source.setPriority(group.getMaxPriority());
//      source.setPriority(group.getMaxPriority());
      priority = source.getPriority();
    }

    int startDownloadTime = group.getStartTime();
    int endDownloadTime = group.getEndTime();
    int hour = calendar.get(Calendar.HOUR_OF_DAY);
    
    if(group.isDownloadInRangeTime()) {
      if(hour < startDownloadTime || hour > endDownloadTime) {
        LinkLogStorages.getInstance().save(source, "{out.of.crawl.time.in.group}");
        return false;
      }
    } else {
      if(hour >= startDownloadTime && hour <= endDownloadTime) {
        LinkLogStorages.getInstance().save(source, "{out.of.crawl.time.in.group}");
        return false;
      }
    }
    
    return true;
  }
  
  long computeTimeout(Source source) {
    int priority = source.getPriority();
    return priority*weight*60*1000;
  }
  
}
