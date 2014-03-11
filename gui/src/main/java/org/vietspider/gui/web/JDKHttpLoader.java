package org.vietspider.gui.web;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URL;
import java.util.List;

/***************************************************************************
 * Copyright 2001-2011 ArcSight, Inc. All rights reserved.  		 *
 **************************************************************************/

/** 
 * Author : Nhu Dinh Thuan
 *          thuannd2@fsoft.com.vn
 * Dec 30, 2011  
 */
class JDKHttpLoader {

  private String host;
  private int port;

  private HttpURLConnection connection;
  //  private String username;
  //  private  String password;
  //  private WebClient webClient;

  JDKHttpLoader() {
    //    this.webClient = webClient;
  }

  byte[] loadContent(String link) throws Exception {
    try {
      ProxySelector selector = ProxySelector.getDefault();
      List<Proxy> list = selector.select(new URI(link));
      if (list.size() > 0 
          && list.get(0) != null
          && list.get(0).address() != null) {
        host = list.get(0).address().toString();
        int idx = host.indexOf(":");
        port = 80;
        if(idx > 0) {
          try {
            port = Integer.parseInt(host.substring(idx+1).trim());
            host = host.substring(0, idx);
          } catch (Exception e) {
          }
        }
        System.setProperty("http.proxyHost", host) ;
        System.setProperty("http.proxyPort", String.valueOf(port)) ;
      }
    } catch (IllegalArgumentException e) {
    }
    
    URL u = new URL(link);
    connection = (HttpURLConnection) u.openConnection();

    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    byte [] bytes = new byte[4096];
    DataInputStream di = new DataInputStream(connection.getInputStream());
    int read = -1;
    while( (read = di.read(bytes, 0, bytes.length)) != -1) {
      stream.write(bytes, 0, read);
    }
    connection.disconnect();
    connection = null;
    return stream.toByteArray();
    //    webClient.registryProxy(host, port, null, null);
  }

  void abort() {
    if(connection == null) return;
    connection.disconnect();
    connection = null;
  }
  
  boolean enabledProxy(String link) {
    ProxySelector selector = ProxySelector.getDefault();
    try {
      List<Proxy> list = selector.select(new URI(link));
      return list.size() > 0 
      && list.get(0) != null
      && list.get(0).address() != null;
    } catch (Exception e) {
      return false;
    }
  }
}
