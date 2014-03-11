/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.net.client;

import java.io.InputStream;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicHeader;
import org.vietspider.common.io.GZipIO;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 21, 2009  
 */
public abstract class AbstClientConnector extends DataClientService {

  protected String application = "vietspider"; 

  protected Header userHeader;
  protected String password;

  public final String getUsername() { return userHeader.getValue(); }
  
  public HttpData loadResponse(String path, byte[] bytes, Header...headers) throws Exception {
    //    System.out.println("   remoteURL+path=" + remoteURL+path);
    HttpPost httpPost = RequestManager.getInstance().createPost(webClient, null, url+path);
    manage(httpPost);
    
    httpPost.addHeader(userHeader);
    for(Header header : headers) httpPost.addHeader(header); 
    httpPost.addHeader(new BasicHeader("Content-Type", "text/xml; charset=utf-8"));
    
    int length = bytes.length;
    bytes = new GZipIO().zip(bytes);
    if(length > bytes.length) httpPost.addHeader(new BasicHeader("zip", "true"));
    httpPost.setEntity(new ByteArrayEntity(bytes));
    //    httpPost.setHeader("Content-Length", String.valueOf(bytes.length));

    HttpResponse httpResponse = null;
    try {
      HttpHost httpHost = webClient.createHttpHost(url);
      httpResponse = webClient.execute(httpHost, httpPost);
    } catch (HttpHostConnectException e) {
      loadDefaultURL();
      setURL(url);
      throw e;
    } 
    
    if(httpResponse == null) {
      release(httpPost, httpResponse);
      return null;
    }
    
    return new HttpData(httpPost, httpResponse);
  }

  public final byte[] get(String path) throws Exception {
    HttpGet httpGet = RequestManager.getInstance().createGet(webClient, null, url+path);
    manage(httpGet);
    
    HttpResponse httpResponse = null;
    try {
      HttpHost httpHost = webClient.createHttpHost(url);
      httpResponse = webClient.execute(httpHost, httpGet);
      if(httpResponse == null) return new byte[0];
      if(reader != null) reader.abort();
      reader = new HttpResponseReader();
      return reader.readBody(httpResponse);
    } catch (HttpHostConnectException e) {
      loadDefaultURL();
      setURL(url);
      throw e;
    } finally {
      release(httpGet, httpResponse);
    }
  }
  
  public byte[] readBytes(HttpResponse httpResponse) throws Exception {
    if(reader != null) reader.abort();
    reader = new HttpResponseReader();
    return reader.readBody(httpResponse);
  }
  
  public void postResponse(String path, byte[] bytes, Header...headers) throws Exception {
//  System.out.println("   remoteURL+path=" + remoteURL+path);
    HttpPost httpPost = RequestManager.getInstance().createPost(webClient, null, url+path);
    manage(httpPost);
    if(userHeader != null) httpPost.addHeader(userHeader);
    for(Header header : headers) httpPost.addHeader(header); 
    httpPost.addHeader(new BasicHeader("Content-Type", "text/xml; charset=utf-8"));
    
    int length = bytes.length;
    bytes = new GZipIO().zip(bytes);
    if(length > bytes.length) httpPost.addHeader(new BasicHeader("zip", "true"));
    httpPost.setEntity(new ByteArrayEntity(bytes));
//  httpPost.setHeader("Content-Length", String.valueOf(bytes.length));

    HttpHost httpHost = webClient.createHttpHost(url);
    try {
      HttpResponse httpResponse = webClient.execute(httpHost, httpPost);
      release(httpPost, httpResponse);
    } catch (Exception e) {
      httpPost.abort();
      throw e;
    }
  }
  
  public final byte[] postGZip(String path, byte [] bytes, Header...headers) throws Exception {
    HttpData httpData = loadResponse(path, bytes, headers);
    try {
      return new GZipIO().load(httpData.getStream()) ;
    } finally {
      release(httpData);
    }
  }
  
  public final byte[] post(String path, byte [] bytes, Header...headers) throws Exception {
    HttpPost httpPost = RequestManager.getInstance().createPost(webClient, null, url+path);
    manage(httpPost);
    if(userHeader != null) httpPost.addHeader(userHeader);
    for(Header header : headers) httpPost.addHeader(header); 
    httpPost.addHeader(new BasicHeader("Content-Type", "text/xml; charset=utf-8"));
    
    int length = bytes.length;
    bytes = new GZipIO().zip(bytes);
    if(length > bytes.length) httpPost.addHeader(new BasicHeader("zip", "true"));
    httpPost.setEntity(new ByteArrayEntity(bytes));

    HttpHost httpHost = webClient.createHttpHost(url);
    HttpResponse httpResponse = null;
    try {
      httpResponse = webClient.execute(httpHost, httpPost);
      if(httpResponse == null) return new byte[0];
//    HttpResponse httpResponse = loadPostResponse(path, bytes, headers);
      if(reader != null) reader.abort();
      reader = new HttpResponseReader();
      return reader.readBody(httpResponse);
    } finally {
      release(httpPost, httpResponse);
    }
  }
  
  
  public abstract void setURL(String _url) throws Exception ;

  public abstract void loadDefaultURL();
  
  public final String getRemoteURL() { return url; }

  public final String getApplication() { return application; }

  public final Header getUserHeader() { return userHeader; }

//  public final Hashtable<String, HttpRequest> getCurrentRequests() { return currentRequests; }

  public final WebClient getWebClient() { return webClient; }

  public final String getPassword() { return password; }
  
  public void release(HttpData httpData)  {
    if(httpData == null) return;
    release(httpData.getHttpPost(), httpData.getResponse());
  }
  
  public static class HttpData {
    
    private HttpPost httpPost;
    private HttpResponse httpResponse;
    
    HttpData(HttpPost post, HttpResponse response) {
      this.httpPost = post;
      this.httpResponse = response;
    }

    public HttpPost getHttpPost() { return httpPost; }

    void setHttpPost(HttpPost httpPost) { this.httpPost = httpPost; }

    public InputStream getStream() throws Exception {
//      try {
//        return new GZIPInputStream(httpResponse.getEntity().getContent());
//      } catch (Exception e) {
//        try {
      return httpResponse.getEntity().getContent();
//        } catch (Exception e2) {
//          throw e2;
//        }
//      }
    }
    
    public Header getResponseHeader(String name) {
      return httpResponse.getFirstHeader(name);
    }

    public HttpResponse getResponse() { return httpResponse; }
  }
}
