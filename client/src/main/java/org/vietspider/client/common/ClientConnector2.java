/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.client.common;

import java.io.File;
import java.net.InetAddress;
import java.net.URL;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.vietspider.ClientProperties;
import org.vietspider.client.common.source.SimpleSourceClientHandler;
import org.vietspider.common.io.UtilFile;
import org.vietspider.net.client.AbstClientConnector;
import org.vietspider.net.client.WebClient;
import org.vietspider.net.server.URLPath;
import org.vietspider.user.User;

/**
 * Linux 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 26, 2007  
 */

public class ClientConnector2 extends  AbstClientConnector {
  
  private static ClientConnector2 connector;
  
  public static ClientConnector2 currentInstance() {
    if(connector == null) connector = new ClientConnector2();
    return connector;
  }
  
  public static ClientConnector2 currentInstance(String host, String port) throws Exception {
    currentInstance().setURL("http://"+host+":"+port);
    return currentInstance();
  }
  
  private String cacheFolder = null;
  private int permission = User.DATA_READ_ONLY;

  public ClientConnector2() {
    loadDefault();
  }
  
  public void loadDefault() {
    loadDefaultURL();
    webClient = new WebClient();
    try {
      setURL(url);
    }catch (Exception e) {
      ClientLog.getInstance().setException(e);
    }
  }
  
  public void setURL(String _url) throws Exception {
    this.url = _url;
    StringBuilder builder = new StringBuilder();
    int i = 0;
    while(i < url.length()) {
      char c = url.charAt(i);
      if(Character.isLetterOrDigit(c)) {
        builder.append(c);
      } else {
        builder.append('_');
      }
      i++;
    }
    this.cacheFolder = builder.toString();
    if(webClient != null) webClient.shutdown();
    webClient.setURL(null, new URL(url));
    
    ClientProperties client = ClientProperties.getInstance();
    
    if("true".equalsIgnoreCase(client.getValue("proxy.enable"))) {
      String proxyHost = client.getValue("proxy.host");
      String proxyPort = client.getValue("proxy.port");
      String proxyUser = client.getValue("proxy.user");
      String proxyPassword = client.getValue("proxy.password");
      setProxy(proxyHost, proxyPort, proxyUser, proxyPassword);
    }
  }
  
  public void setProxy(String proxyHost, String proxyPort, String proxyUser, String proxyPassword) {
    if(proxyHost == null || proxyHost.trim().isEmpty()) return ;
    try {
      int intProxyPort = Integer.parseInt(proxyPort.trim());
      if(proxyUser != null && proxyUser.trim().isEmpty()) proxyUser = null;
      webClient.registryProxy(proxyHost, intProxyPort, proxyUser, proxyPassword);
    } catch (Exception e) {
      ClientLog.getInstance().setException(e);
    }
  }
  
  public int ping(String username, String password_) throws Exception {
    SimpleSourceClientHandler.clear();
    
    this.password = password_;
    userHeader = new BasicHeader("username", username);
    Header [] headers = {
        userHeader,
        new BasicHeader("password", password),
        new BasicHeader("action", "server.ping")
    };
    
    byte [] bytes = post(URLPath.APPLICATION_HANDLER, new byte[0], headers);
    String value = new String(bytes).trim();
    if(value.equals("-2")) {
      return -2;
    } else if(value.equals("-1"))  {
      return -1;
    }
    
    String [] elements = value.split(",");
    if(elements.length < 2) return 0;
    try {
      application = elements[0];
      permission = Integer.parseInt(elements[1]);
    } catch (Exception e) {
      return 0;
    }
    return permission;
  }
  
  public void loadDefaultURL() { 
    url = ClientProperties.getInstance().getValue("remote.server");
    if(url == null || url.trim().length() < 1) {
      try {
        url  = "http://"+InetAddress.getLocalHost().getHostName()+":9245/";
      }catch (Exception e) {
        //ClientLog.getInstance().setException(e);
        url = "http://127.0.0.1:9245/";
      }
    }
    
    url = url.trim();
    
    if(url.charAt(url.length() - 1) == '/') {
      url = url.substring(0, url.length()-1);
    }
  }

  public int getPermission() { return permission; }

  public static File getCacheFolder(String folder) {
    if(folder.length() > 0 
        && folder.charAt(folder.length() -1) != '/') {
      folder = folder+"/";
    }
    return UtilFile.getFolder("client/"+currentInstance().cacheFolder+"/"+folder);
  }
  
  public static File getCacheFile(String folder, String file) {
    if(folder.length() > 0 
        && folder.charAt(folder.length() -1) != '/') {
      folder = folder+"/";
    }
    return UtilFile.getFile("client/"+currentInstance().cacheFolder+"/"+folder, file);
  }

}
