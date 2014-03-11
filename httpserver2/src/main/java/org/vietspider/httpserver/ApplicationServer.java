/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.httpserver;

import java.net.InetAddress;

import org.apache.http.protocol.HttpRequestHandlerRegistry;
import org.vietspider.common.Application;
import org.vietspider.db.SystemProperties;
import org.vietspider.server.handler.cms.metas.CMSHandler;
import org.vietspider.webui.search.SearchHandler;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 4, 2009  
 */
public abstract class ApplicationServer {
  
  private String host;
  protected HttpServer server; 
  
  public ApplicationServer() {
  }
  
  public void shutdown() {
    server._shutdown();
  }
  
  protected void init(int port) {
    SystemProperties systemProperties = SystemProperties.getInstance();
    boolean registry = false;
    try {
      registry = Boolean.valueOf(systemProperties.getValue("client.registry"));
    } catch (Exception e) {
      registry = false;
    }

    this.server = HttpServer.getInstance(registry, port);
  }

  public String getHost() {
    if(host != null) return host;
    host = "localhost";
    
    SystemProperties systemProperties = SystemProperties.getInstance();
    try{
      host = systemProperties.getValue(Application.HOST);
    } catch (Exception e) {
    }
    
    if(host == null || host.trim().length() < 1) {
      try {
        InetAddress inet = InetAddress.getLocalHost();
        host = inet.getHostName();
      } catch (Exception e) {
        host = "127.0.0.1";
      }
    }
    return host; 
  }
  
  public int getPort() { return server.getPort(); }
  
  protected void createContextHandler(HttpRequestHandlerRegistry reqistry, CMSHandler<?> handler) {
//    System.out.println("===>"+ handler.getPath()+"/*");
    reqistry.register(handler.getPath()+"/*", handler);
  }

  protected void createContextHandler(HttpRequestHandlerRegistry reqistry, SearchHandler<?> handler) {
    reqistry.register(handler.getPath()+"/*", handler);
  }
}
