/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.net.channel;

import java.io.Serializable;
import java.net.URL;
import java.util.Properties;

import org.vietspider.browser.FastWebClient;
import org.vietspider.browser.HttpSessionUtils;
import org.vietspider.model.SourceProperties;
import org.vietspider.net.client.HttpMethodHandler;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 24, 2008  
 */
@SuppressWarnings("serial")
public class LoginWebsite implements Serializable {
  
  private boolean isLogin = false;
  private ISourceConfig config;
  
  public LoginWebsite(ISourceConfig config) {
    this.config = config;
  }
  
  public void reset() { isLogin = false; }
  
  public void login() throws Exception {
    if(isLogin) return;
    String homepage = config.getHomepage();
    if(homepage == null || homepage.isEmpty()) return;
    
    String referer = "";
    Properties properties = config.getProperties();
    if(properties.containsKey(SourceProperties.REFERER_NAME)) {
      referer = properties.getProperty(SourceProperties.REFERER_NAME).trim();
    }
    final String referer_ = referer;
    final URL url = new URL(homepage);
    
    FastWebClient webclient = config.getWebClient();
    String charset = config.getCharset();
    HttpMethodHandler handler = new HttpMethodHandler(webclient);
    final HttpSessionUtils httpSessionUtils = new HttpSessionUtils(handler, null);
    webclient.setLog(true);
    isLogin = httpSessionUtils.login(properties, charset, url, referer_);
    webclient.setLog(false);
  }

}
