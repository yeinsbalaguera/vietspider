/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.model.plugin;

import org.vietspider.common.io.LogService;
import org.vietspider.serialize.Object2XML;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 10, 2009  
 */
public abstract class SyncContent {
  
  public final static int WAITING = 0;
  public final static int SENDING = 1;
  public final static int ERROR = -1;
  public final static int SUCCESSFULL = 2;
  
  protected String plugin;

  private int id = -1;
  private String xml;
  private int status;
  private String message;
  
  private long start = -1;
  
  private boolean isShowMessage = true;

  public SyncContent(String plugin) {
    this.plugin = plugin;
  }

  public String getPlugin() { return plugin; }
  
  public String toXML() {
    if(xml != null) return xml;
    try {
      xml = Object2XML.getInstance().toXMLDocument(this).getTextValue();
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
      xml = "";
    }
    
    return xml;
  }
  
  public int getId() {
    if(id == -1) id = toXML().hashCode();
    return id; 
  }
  
  public int getStatus() { return status; }
  public void setStatus(int status) { this.status = status; }
  
  public String getMessage() { return message; }
  public void setMessage(String message) { this.message = message; }
  
  public void setStart(long start) {
    this.start = start;
  }
  
  public boolean isTimeout() {
    if(start < 0) return false;
    return System.currentTimeMillis() - start >=  60*1000;
  }

  public long getStart() {
    return start;
  }

  public boolean isShowMessage() { return isShowMessage;  }
  public void setShowMessage(boolean isShowMessage) { this.isShowMessage = isShowMessage; }
  
  public abstract boolean isDebug() ;
  public abstract void setDebug(boolean debug) ;

}
