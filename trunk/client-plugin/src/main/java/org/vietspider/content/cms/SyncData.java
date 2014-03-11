/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.cms;

import org.vietspider.serialize.Object2XML;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 10, 2009  
 */
public abstract class SyncData {
  
  protected String plugin;
  
  public SyncData(String plugin) {
    this.plugin = plugin;
  }

  public String getPlugin() { return plugin; }
  
  public String toXML() {
    try {
      return Object2XML.getInstance().toXMLDocument(this).getTextValue();
    } catch (Exception e) {
      return "";
    }
  }
  
  public boolean isAlertMessage() { return true; }

}
