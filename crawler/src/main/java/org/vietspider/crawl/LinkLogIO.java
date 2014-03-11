/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl;

import org.vietspider.crawl.link.Link;
import org.vietspider.db.link.track.LinkLog;
import org.vietspider.db.link.track.LinkLogStorages;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 1, 2010  
 */
public class LinkLogIO {
  
  public static void saveLinkLog(Link link, String message, int phase) {
    saveLinkLog(link, message, phase, null);
  }
  
  public static void saveLinkLog(Link link, String message, int phase, String dataId) {
    if(link.getSourceFullName() == null) return;
    String fullName = link.getSourceFullName();
    
    LinkLog log = new LinkLog(fullName, link.getAddress());
    log.setBytes(link.getTotalOfBytes());
    log.setLevel(link.getLevel());
    if(log.getLevel() == 0) {
      log.setType(LinkLog.TYPE_HOME);
    } else if(link.isData()){
      log.setType(LinkLog.TYPE_DATA);
    } else if(link.isLink()){
      log.setType(LinkLog.TYPE_LINK);
    } else {
      log.setType(LinkLog.TYPE_INVALID);
    }
    log.setDataId(dataId);
    log.setPhase(phase);
    log.setMessage(message);
    LinkLogStorages.getInstance().save(log);
  }
  
  public static LinkLog createLinkLog(Link link, String message, int phase) {
    return createLinkLog(link, message, phase, null);
  }
  
  public static LinkLog createLinkLog(Link link, String message, int phase, String dataId) {
    String fullName = link.getSourceFullName();
    
    LinkLog log = new LinkLog(fullName, link.getAddress());
    log.setBytes(link.getTotalOfBytes());
    log.setLevel(link.getLevel());
    if(log.getLevel() == 0) {
      log.setType(LinkLog.TYPE_HOME);
    } else if(link.isData()){
      log.setType(LinkLog.TYPE_DATA);
    } else if(link.isLink()){
      log.setType(LinkLog.TYPE_LINK);
    } else {
      log.setType(LinkLog.TYPE_INVALID);
    }
    log.setDataId(dataId);
    log.setPhase(phase);
    log.setMessage(message);
//    LinkLogStorages.getInstance().save(log);
    return log;
  }
  
  public static void saveLinkLog(Link link, Throwable throwable, int phase) {
    saveLinkLog(link, throwable, phase, null);
  }
  
  public static void saveLinkLog(Link link, Throwable throwable, int phase, String dataId) {
    String fullName = link.getSourceFullName();
    
    LinkLog log = new LinkLog(fullName, link.getAddress());
    log.setBytes(link.getTotalOfBytes());
    log.setLevel(link.getLevel());
    if(log.getLevel() == 0) {
      log.setType(LinkLog.TYPE_HOME);
    } else if(link.isData()){
      log.setType(LinkLog.TYPE_DATA);
    } else if(link.isLink()){
      log.setType(LinkLog.TYPE_LINK);
    } else {
      log.setType(LinkLog.TYPE_INVALID);
    }
    log.setDataId(dataId);
    log.setPhase(phase);
    log.setMessage(throwable.toString());
    log.setThrowable(throwable);
    LinkLogStorages.getInstance().save(log);
  }
}
