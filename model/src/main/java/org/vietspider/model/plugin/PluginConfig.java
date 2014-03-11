/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.model.plugin;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 20, 2010  
 */
public abstract class PluginConfig {
  
  public volatile transient long lastModified = System.currentTimeMillis();
  
  public abstract String getCharset() ;
  
  public abstract String getHomepage();
  
  public abstract String getLoginAddress();
  public abstract String getUsername();
  public abstract String getPassword();
  
  public final void setLastModified(long lastModified) { this.lastModified = lastModified; }
  public final long getLastModified() { return lastModified; }
}
