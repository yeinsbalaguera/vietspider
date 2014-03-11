/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.client.common;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URL;
import java.util.Hashtable;
import java.util.Iterator;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicHeader;
import org.vietspider.ClientProperties;
import org.vietspider.client.common.source.SimpleSourceClientHandler;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.net.client.WebClient;
import org.vietspider.net.server.URLPath;
import org.vietspider.user.User;

/**
 * Linux 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 26, 2007  
 */

public class ClientConnector {

  private WebClient webClient;
  
  private String remoteURL  = "http://127.0.0.1:9245/";
  
  private String application = "vietspider"; 
  
  private static ClientConnector connector;
  
  private Header userHeader;
  private String password;
  
  private String cacheFolder = null;
  
  private int permission = User.DATA_READ_ONLY;
  
  private Hashtable<String, HttpRequest> currentRequests = new Hashtable<String, HttpRequest>();
  
  public static ClientConnector currentInstance() {
    if(connector == null) connector = new ClientConnector();
    return connector;
  }
  
  public String getUsername() { return userHeader.getValue(); }
  
  public static ClientConnector currentInstance(String host, String port) throws Exception {
    currentInstance().setURL("http://"+host+":"+port);
    return currentInstance();
  }

  public ClientConnector() {
    loadDefault();
  }
  
  public void loadDefault() {
    loadDefaultURL();
    webClient = new WebClient();
    try {
      setURL(remoteURL);
    }catch (Exception e) {
      ClientLog.getInstance().setException(e);
    }
  }
  
  public void abort() {
    Iterator<String> iter = currentRequests.keySet().iterator();
    while(iter.hasNext()) {
      String key = iter.next();
      HttpRequest request = currentRequests.get(key);
      if(request instanceof HttpGet) {
        ((HttpGet)request).abort();
      } else if(request instanceof HttpPost) {
        ((HttpPost)request).abort();
      }
    }
  }
  
  public void setURL(String url) throws Exception {
    this.remoteURL = url;
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
    webClient.setURL(null, new URL(remoteURL));
    
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

  public byte[] post(String path, byte [] bytes, Header...headers) throws Exception {
    InputStream stream = loadPostStream(path, bytes, headers);
    byte [] data = new byte[0];
    try {
      data = RWData.getInstance().loadInputStream(stream).toByteArray();
      stream.close();
    } finally {
      stream.close();
    }
    return data;
  }
  
  public InputStream loadPostStream(String path, byte[] bytes, Header...headers) throws Exception {
    HttpResponse httpResponse = loadPostResponse(path, bytes, headers);
    if(httpResponse == null) return new ByteArrayInputStream(new byte[0]);
    return httpResponse.getEntity().getContent();
  }
  
  public HttpResponse loadPostResponse(String path, byte[] bytes, Header...headers) throws Exception {
//  	System.out.println("   remoteURL+path=" + remoteURL+path);
    HttpPost httpPost = new HttpPost(remoteURL+path);
    httpPost.addHeader(userHeader);
    for(Header header : headers) httpPost.addHeader(header); 
    if(bytes != null) httpPost.setEntity(new ByteArrayEntity(bytes));
    httpPost.setHeader("Content-Type", "text/xml; charset=utf-8");
//    httpPost.setHeader("Content-Length", String.valueOf(bytes.length));
    currentRequests.put(remoteURL+path, httpPost);
    
    HttpResponse httpResponse = null;
    try {
      HttpHost httpHost = webClient.createHttpHost(remoteURL);
      httpResponse = webClient.execute(httpHost, httpPost);
    } catch (HttpHostConnectException e) {
      loadDefaultURL();
      setURL(remoteURL);
      throw e;
    }
    if(httpResponse == null) return null;
    currentRequests.remove(remoteURL+path);
    return httpResponse;
  }
  
  public byte[] get(String path) throws Exception {
    InputStream stream = loadInputStreamByGetMethod(path);
    byte [] bytes = new byte[0];
    try {
      bytes = RWData.getInstance().loadInputStream(stream).toByteArray();
      stream.close();
    } finally {
      stream.close();
    }
    return bytes;
  }
  
  private InputStream loadInputStreamByGetMethod(String path) throws Exception {
    HttpGet httpGet = new HttpGet(remoteURL+path);
    currentRequests.put(remoteURL+path, httpGet);
    HttpResponse httpResponse = null;
    try {
      HttpHost httpHost = webClient.createHttpHost(remoteURL);
      httpResponse = webClient.execute(httpHost, httpGet);
    } catch (HttpHostConnectException e) {
      loadDefaultURL();
      setURL(remoteURL);
      throw e;
    }
    if(httpResponse == null) return new ByteArrayInputStream(new byte[0]);
    currentRequests.remove(remoteURL+path);
    return httpResponse.getEntity().getContent();
  }
  
  private void loadDefaultURL() { 
    remoteURL = ClientProperties.getInstance().getValue("remote.server");
    if(remoteURL == null || remoteURL.trim().length() < 1) {
      try {
        remoteURL  = "http://"+InetAddress.getLocalHost().getHostName()+":9245/";
      }catch (Exception e) {
        //ClientLog.getInstance().setException(e);
        remoteURL = "http://127.0.0.1:9245/";
      }
    }
    
    remoteURL = remoteURL.trim();
    
    if(remoteURL.charAt(remoteURL.length() - 1) == '/') {
      remoteURL = remoteURL.substring(0, remoteURL.length()-1);
    }
  }

  public String getRemoteURL() { return remoteURL; }

  public String getApplication() { return application; }

  public int getPermission() { return permission; }

  public Header getUserHeader() { return userHeader; }

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

  public Hashtable<String, HttpRequest> getCurrentRequests() { return currentRequests; }

  public WebClient getWebClient() { return webClient; }

  public String getPassword() { return password; }

}
