/***************************************************************************
 * Copyright 2003-2007 by VietSpider - All rights reserved.                *    
 **************************************************************************/
package org.vietspider.gui.browser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.vietspider.ClientProperties;
import org.vietspider.cache.InmemoryCache;
import org.vietspider.chars.URLUtils;
import org.vietspider.net.client.HttpResponseReader;
import org.vietspider.net.client.WebClient;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.services.ImageLoader;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Dec 11, 2011
 */
public class WebIconLoader implements Runnable {
  
  private final static InmemoryCache<String, Image> cached = new InmemoryCache<String, Image>("web.icon", 500);
  static {
    cached.setLiveTime(5*60);
  }
  
  private WebClient webClient;
  private BrowserWidget widget;
  
  public WebIconLoader(BrowserWidget widget) {
    this.widget = widget;
    webClient = new WebClient();
  }
  
  public void run() {
    Image image = getWebIcon();
    if(image == null) {
      ImageLoader imageLoader = new ImageLoader();
      this.widget.getItem().setImage(imageLoader.load(widget.getDisplay(), "web.png"));
    }
    this.widget.getItem().setImage(image);
    webClient.shutdown();
  }
  
  private Image getWebIcon() {
    URL url = null;
    try {
      url = new URL(widget.getUrl());
    } catch (Exception e) {
      return null;
    }
    String host = url.getHost();
    
    Image image = cached.getCachedObject(host);
    if(image != null) return image; 
//    {
//      System.out.println(host + " : " + image);
//      return image;
//    }
    
    String html = widget.getWidget().getText();    
    int start = html.indexOf("link");
    while(start > 0) {
      int end = html.indexOf('>', start+10);
      if(end > start) {
       String link = html.substring(start, end);
       if(link.indexOf("icon") > -1) {
         image = getWebIcon(link);
         if(image != null) {
           cached.putCachedObject(host, image);
           return image;
         }
       }
      }
      start = html.indexOf("link", start+10);
    }
    
    try {
      String address = "http://" + host +"/favicon.ico";
      setHomepage(address);
//      System.out.println(" address "+ address);
      InputStream input = loadContent(address, address);   
      if(input != null) {
        ImageData imgData = new ImageData(input);      
        image = new Image(widget.getDisplay(), imgData);
        cached.putCachedObject(host, image);
        return image;
      }
    } catch (Exception e) {
//      e.printStackTrace();
    }
    
    try {
      String address = "http://www.google.com/s2/u/0/favicons?domain=" + host;
      setHomepage("http://www.google.com");
      InputStream input = loadContent("http://www.google.com", address);
      if(input != null) {
        ImageData imgData = new ImageData(input);      
        image = new Image(widget.getDisplay(), imgData);
        cached.putCachedObject(host, image);
        return image;
      }
    } catch (Exception e) {
    }
    
    return null;
  }
  
  private Image getWebIcon(String link) {
    int start = link.indexOf("href");
    if(start < 0) return null;
    link = link.substring(start+4);
    start = link.indexOf('\"');
    if(start < 0) return null;
    int end = link.indexOf('\"', start+2);
    if(end < 0) return null;
    link = link.substring(start+1, end);
    URLUtils urlUtils = new URLUtils();
    String home = widget.getUrl();
    String address = urlUtils.createURL(home, link);

    try { 
      setHomepage(home);
      InputStream input = loadContent(home, address);     
      ImageData imgData = new ImageData(input);      
      return new Image(widget.getDisplay(), imgData);
    } catch (Exception e) {
      ClientLog.getInstance().setException(null, e);
    }
    return null;
  }
  
  public ByteArrayInputStream loadContent(String homepage, String address) throws Exception {
    HttpGet httpGet = webClient.createGetMethod(address, null);

    if(httpGet == null) return null;
    HttpHost httpHost = null;
    if(address.toLowerCase().startsWith("http://")
        || address.toLowerCase().startsWith("https://")) {
      httpHost = webClient.createHttpHost(address);
    } else {
      httpHost = webClient.createHttpHost(homepage);
    }
    HttpResponse httpResponse = webClient.execute(httpHost, httpGet);

    HttpResponseReader httpResponseReader = new HttpResponseReader();
    byte [] bytes = httpResponseReader.readBody(httpResponse);
    if(bytes == null || bytes.length < 10) return null;
    return new ByteArrayInputStream(bytes);
  }
  
  public void setHomepage(String homepage) throws Exception {
    webClient.setURL(null, new URL(homepage));
    
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
      ClientLog.getInstance().setException(null, e);
    }
  }

}
