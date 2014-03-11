/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.server.plugin;

import java.io.OutputStream;

import org.apache.http.HttpRequest;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 13, 2008  
 */
public abstract class PluginHandler {
  
  protected OutputStream output;
  
  protected HttpRequest request;
  
  public String getLogMessage() { return null; }
  
  public abstract String getName();
  
  public abstract byte[] invoke(String action, String value) throws Exception ;

  public void setOutput(OutputStream output) { this.output = output; }

  public void setRequest(HttpRequest request) { this.request = request; }
  
}

