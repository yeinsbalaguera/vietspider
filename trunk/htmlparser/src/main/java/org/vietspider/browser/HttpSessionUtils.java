/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.browser;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.text.SWProtocol;
import org.vietspider.net.client.HttpMethodHandler;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 24, 2008  
 */
//login syntax
/* type 1 
 * http://login/url
 * username:password
 * 
 * type 2:
 * http://login/url
 * username:password
 * add.field1 = value
 * add.field2 = value
 * 
 * 
 * type 3:
 * http://login/url
 * username (unused):password (unused)
 * disable.auto.detect
 * add.field1 = value (username)
 * add.field2 = value (password)
 * 
 * type 4:
 * 
 * http://login/url
 * username (unused):password (unused)
 * disable.auto.detect
 * add.field1 = value (username)
 * add.field2 = value (password)
 * remove.field2 = [disable]
 */

public class HttpSessionUtils {

  public final static String PROXY = "Proxy";

  private Object errorLabel;
  private HttpMethodHandler methodHandler;
//  private boolean redirect = true;

  public HttpSessionUtils(HttpMethodHandler _methodHandler, Object errorLabel) {
    this.methodHandler = _methodHandler;
    this.errorLabel = errorLabel;
  }

  public boolean login(Properties properties, String charset, URL home, String referer) throws Exception {
    return login(properties.getProperty("Login"), charset, home, referer, null);
  }
  
  public boolean login(String loginValue, String charset, URL home, String referer) throws Exception {
    return login(loginValue, charset, home, referer, null);
  }
  
  public boolean login(String loginValue, String charset,
      URL home, String referer, List<String> ignoreFields) throws Exception {
    if(loginValue == null  || (loginValue = loginValue.trim()).isEmpty()) return true;
    String [] elements = loginValue.split("\n");
    URL loginUrl  = home;
    String username = null;
    String password = null;

    if(elements.length < 1) return true;

    if(SWProtocol.isHttp(elements[0])) {
      try {
        loginUrl = new URL(elements[0]);
      } catch (Exception e) {
        return false;
      }
      int idx = elements[1].indexOf(':');
      if(idx > 0) {
        username = elements[1].substring(0, idx).trim();
        password = elements[1].substring(idx+1).trim();
      }
    } else if(elements.length < 2 || SWProtocol.isHttp(elements[1])) {
      try {
        if(elements.length > 1) loginUrl = new URL(elements[1]);
      } catch (Exception e) {
        return false;
      }
      int idx = elements[0].indexOf(':');
      if(idx > 0) {
        username = elements[0].substring(0, idx).trim();
        password = elements[0].substring(idx+1).trim();
      }
    }
    
    if(elements.length > 2) {
      ignoreFields = new ArrayList<String>();
      for(int i = 0; i < elements.length; i++) {
        elements[i] = elements[i].trim();
        if(elements[i].startsWith("ignore ")) {
          ignoreFields.add(elements[i].substring(7));
//          System.out.println(" ======  > "+ elements[i].substring(8));
        }
      }
    }
    
    if(username == null || password == null) return true; 
    LoginWebsiteHandler loginWebsite = new LoginWebsiteHandler();
//    loginWebsite.ignoreData("rememberme", "forever");
    if(ignoreFields != null) loginWebsite.getRemoveFields().addAll(ignoreFields);
    for(int i = 2; i < elements.length; i++) {
      if(elements[i].trim().equalsIgnoreCase("disable.auto.detect")) {
        loginWebsite.setAutoDetect(false);
        continue;
      } 
      loginWebsite.putData(elements[i]);
    }
//    httpMethod.setTimeout(30);
//    try {
    return loginWebsite.login(methodHandler, referer, charset, loginUrl, username, password) ;
//    System.out.println(response);
//    if(response == null) {
//    LogService.getInstance().setMessage(source, "Cann't login to website");
//    return  false;
//    }
//    // comment this line
//    saveLogin(response);
//    } catch (Exception e) {
//      throw e;
//      LogService.getInstance().setMessage(errorLabel, e, "Cann't login to website");
//      return false;
//    }
  }

  public void setProxy(Properties prop, String proxy) {
    String proxyEnable = prop.getProperty(Application.PROXY_ENABLE);
    if(!"true".equalsIgnoreCase(proxyEnable)) return;
    // set source proxy 
    String proxyHost = prop.getProperty(Application.PROXY_HOST);
    if(proxyHost != null && !proxyHost.trim().isEmpty()) {
      try {
        int proxyPort = Integer.parseInt(prop.getProperty(Application.PROXY_PORT).trim());
        String proxyUser = prop.getProperty(Application.PROXY_USER);
        if(proxyUser != null && proxyUser.trim().isEmpty()) proxyUser = null;
        String proxyPassword = prop.getProperty(Application.PROXY_PASSWORD);
        methodHandler.getWebClient().registryProxy(proxyHost, proxyPort, proxyUser, proxyPassword);
      } catch (Exception e) {
        LogService.getInstance().setThrowable(errorLabel, e);
      }
      return ;
    } 
    
    if(proxy == null || proxy.trim().isEmpty()) {
      LogService.getInstance().setMessage(null, "Proxy not found!");
      return;
    }

    String [] elements = proxy.split(":");
    if(elements.length < 2) return;

    int proxyPort = -1;
    try {
      proxyPort = Integer.parseInt(elements[1].trim());
    } catch (Exception e) {
      LogService.getInstance().setThrowable(errorLabel, e);
      return;
    }

    String proxyUser = elements.length < 3 ? null : elements[2].trim();
    String proxyPassword = elements.length < 4 ? null : elements[3].trim();
    try {
      methodHandler.getWebClient().registryProxy(
          elements[0].trim(), proxyPort, proxyUser, proxyPassword);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(errorLabel, e);
    }
  }
  
  /*public final static boolean isMultiProxies(String value) {
    if(value == null) return false;
    int counter = 0;
    int index = 0;
    while(index < value.length()) {
      char c = value.charAt(index);
      if(c == ':') counter++;
      if(counter > 3) return true;
      index++;
    }
    return false;
  }
  
  public final static String toMultiProxies(String proxy) {
    File file  = new File(UtilFile.getFolder("system/proxy/"), "proxies.txt");
    if(!file.exists() || file.length() < 1) return null;
    String value =  null;
    try {
      value =  new String(RWData.getInstance().load(file), "utf-8");
    } catch (Exception e) {
      return null;
    }
    if(value == null || value.trim().isEmpty())  return null;
    
    int idx = proxy.indexOf("error.code");
    if(idx > -1) {
      String errorCode = proxy.substring(idx+11);
      return"# error.code " + errorCode + "\n" + value;
    }
    
    idx = proxy.indexOf("error.size");
    if(idx > -1) {
      String errorCode = proxy.substring(idx+11);
      return"# error.size " + errorCode + "\n" + value;
    }
    
    return value;
  }*/

}
