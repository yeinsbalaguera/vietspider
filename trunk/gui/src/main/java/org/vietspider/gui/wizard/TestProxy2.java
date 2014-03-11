/***************************************************************************
 * Copyright 2001-2012 ArcSight, Inc. All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.wizard;

import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.vietspider.gui.web.FastWebClient2;

/** 
 * Author : Nhu Dinh Thuan
 *          thuannd2@fsoft.com.vn
 * Jan 3, 2012  
 */
public class TestProxy2 {
  private static DefaultHttpClient initHttpClient() {
    
    DefaultHttpClient client = new DefaultHttpClient();
    
    String proxyHost = System.getProperty("https.proxyHost");
    if(proxyHost == null) {
     proxyHost = System.getProperty("http.proxyHost");
    }
    
    if(proxyHost != null) {
     
     String proxyUser = System.getProperty("https.proxyUser");
     if(proxyUser == null) {
      proxyUser = System.getProperty("http.proxyUser");
     }
     System.out.println(proxyUser);
     
     String proxyPassword = System.getProperty("https.proxyPassword");
     if(proxyPassword == null) {
      proxyPassword = System.getProperty("http.proxyPassword");
     }
     
     if(proxyUser != null && proxyPassword != null) {
      client.getCredentialsProvider().setCredentials(
                       new AuthScope("localhost", 8080), 
                       new UsernamePasswordCredentials(proxyUser, proxyPassword));
     }
     
     String proxyPort = System.getProperty("https.proxyPort");
     if(proxyPort == null) {
      proxyPort = System.getProperty("http.proxyPort");
     }
     if(proxyPort == null) proxyPort = "80";
     
     
     HttpHost proxy = new HttpHost(proxyHost, Integer.parseInt(proxyPort));
              
              client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
    }
    
    return client;
  }
  
  public static void main(String[] args) {
    System.setProperty("java.net.useSystemProxies", "true");
    try {
      ProxySelector selector = ProxySelector.getDefault();
      List<Proxy> list = selector.select(new URI("http://google.com.vn"));
      if (list.size() > 0 
          && list.get(0) != null
          && list.get(0).address() != null) {
        String host = list.get(0).address().toString();
        int idx = host.indexOf(":");
        int port = 80;
        if(idx > 0) {
          try {
            port = Integer.parseInt(host.substring(idx+1).trim());
            host = host.substring(0, idx);
          } catch (Exception e) {
          }
        }
        System.out.println(host + " : " + port);
        System.setProperty("http.proxyHost", host) ;
        System.setProperty("http.proxyPort", String.valueOf(port)) ;
      }
    } catch (Exception e) {
    }
//    initHttpClient();
    
    new FastWebClient2();
  }
}
