/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.webapp;

import java.net.URL;

import org.vietspider.cache.InmemoryCache;
import org.vietspider.common.io.LogService;
import org.vietspider.db.SystemProperties;
import org.vietspider.net.client.HttpResponseReader;
import org.vietspider.net.client.WebClient;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 2, 2011  
 */
abstract class TranslatorMachine {
  
  protected TranslateMode config;
  protected WebClient webClient;
  protected HttpResponseReader reader;
  
  protected InmemoryCache<String, String> cached;
  
  public TranslatorMachine(TranslateMode config, String homepage) {
    this.config = config;
    createWebClient(homepage);
    reader = new HttpResponseReader();
    cached = new InmemoryCache<String, String>("translate", 500);
    cached.setLiveTime(30*60);
  }
  
  abstract String translate(String text, String from, String to) throws Exception;

  private void createWebClient(String homepage) {
    webClient = new WebClient();
    try {
      webClient.setURL(null, new URL(homepage));
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    SystemProperties system = SystemProperties.getInstance();
    if("true".equalsIgnoreCase(system.getValue("proxy.enable"))) {
      String proxyHost = system.getValue("proxy.host");
      String proxyPort = system.getValue("proxy.port");
      String proxyUser = system.getValue("proxy.user");
      String proxyPassword = system.getValue("proxy.password");
      setProxy(proxyHost, proxyPort, proxyUser, proxyPassword);
    }
  }

  private void setProxy(String proxyHost, String proxyPort, String proxyUser, String proxyPassword) {
    if(proxyHost == null || proxyHost.trim().isEmpty()) return ;
    try {
      int intProxyPort = Integer.parseInt(proxyPort.trim());
      if(proxyUser != null && proxyUser.trim().isEmpty()) proxyUser = null;
      webClient.registryProxy(proxyHost, intProxyPort, proxyUser, proxyPassword);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }
  
}
