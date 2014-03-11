/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.net.client;

import java.io.InterruptedIOException;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.RedirectException;
import org.apache.http.client.methods.AbortableHttpRequest;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.vietspider.chars.URLEncoder;
import org.vietspider.common.io.LogService;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 18, 2007  
 */
public class HttpMethodHandler {

  public final static String HTTP_REQUEST_TIMEOUT = "http.request.timeout";

  protected AbortableHttpRequest httpRequest;
  protected HttpHost httpHost;
  protected WebClient webClient;
  protected HttpResponse httpResponse;
  
  protected HttpResponseReader reader = null;
  
  protected long readData = 0l;
  
  public HttpMethodHandler(WebClient webClient) {
    this.webClient = webClient;
  }

  public WebClient getWebClient() { return webClient; }

  public HttpResponse execute(String address,
      String referer, List<NameValuePair> params, String charset) throws Exception {
    try {
      this.httpHost = webClient.createHttpHost(address); 
      return execute(webClient.createPostMethod(address, referer, params, charset));
      //    System.out.println(httpGet.getFirstHeader("User-Agent").getValue().trim());
    } catch (URISyntaxException e) {
      //encode url
      URLEncoder encoder = new URLEncoder();
      address = encoder.encode(address);
      try {
        this.httpHost = webClient.createHttpHost(address);
        return execute(webClient.createPostMethod(address, referer, params, charset));
      } catch (URISyntaxException se) {
        LogService.getInstance().setMessage(se, "1.Get method handler: " + address);
        return null;
      }
    } catch (IllegalArgumentException e) {
      LogService.getInstance().setMessage(e, "2.Get method handler: " + address);
      return null;
    } catch (Exception e) {
      LogService.getInstance().setMessage(e, "3.Get method handler: " + address);
      return null;
    }
  }

  public HttpResponse execute(String address, String referer) throws Exception {
    try {
      //    System.out.println(address);
      this.httpHost = webClient.createHttpHost(address);
      return execute(webClient.createGetMethod(address, referer));
    } catch (UnknownHostException e) {
      throw e;
      //    System.out.println(httpGet.getFirstHeader("User-Agent").getValue().trim());
    } catch (URISyntaxException e) {//encode url
      return executeEncodeURL(address, referer); 
    } catch (IllegalArgumentException e) {
      return executeEncodeURL(address, referer);
    } catch (Exception e) {
      //    e.printStackTrace();
      LogService.getInstance().setMessage(e, "6.Get method handler: " + e.toString() + "-" + address);
      return null;
    }
  }

  private HttpResponse executeEncodeURL (String address, String referer) throws Exception {
    URLEncoder encoder = new URLEncoder();
    address = encoder.encode(address);
    try {
      this.httpHost = webClient.createHttpHost(address);
      return execute(webClient.createGetMethod(address, referer));
    } catch (URISyntaxException se) {
      LogService.getInstance().setMessage(se, "4.Get method handler: " + address);
    } catch (IllegalArgumentException e) {
      LogService.getInstance().setMessage(e, "5.Get method handler: " + address);
    }
    return null;
  }

  public HttpResponse execute(HttpHost host, AbortableHttpRequest abortedHttpRequest) throws Exception {
    this.httpHost = host;
    return execute(abortedHttpRequest);
  }

  private HttpResponse execute(AbortableHttpRequest httpRequest_) throws Exception {
    this.httpRequest = null;
    this.httpResponse = null;
    this.readData = 0l;
    this.httpRequest = httpRequest_;
    if(httpRequest == null) return null;

    try {
//      System.out.println(" chuan bi tiep theo ");
      httpResponse = webClient.execute(httpHost, (HttpRequest)httpRequest);
//      System.out.println(" thay co "+ httpRequest_ + " : "+ httpResponse);
    } catch (RedirectException e) {
      LogService.getInstance().setMessage(e, ((HttpRequest)httpRequest).getRequestLine().getUri());
      return null;
    } catch (IndexOutOfBoundsException e) {
      LogService.getInstance().setMessage(e, ((HttpRequest)httpRequest).getRequestLine().getUri());
      return null;
    } catch (IllegalStateException e) {
      LogService.getInstance().setMessage(e, ((HttpRequest)httpRequest).getRequestLine().getUri());
      return null;
    } catch (SocketException e) {
      LogService.getInstance().setMessage(e, ((HttpRequest)httpRequest).getRequestLine().getUri());
      return null;
    } catch (ConnectionPoolTimeoutException e) {
      LogService.getInstance().setMessage(e, ((HttpRequest)httpRequest).getRequestLine().getUri());
      return null;
    } catch (InterruptedIOException e) { 
      return null;
    } catch (IllegalArgumentException e) {
//      e.printStackTrace();
      LogService.getInstance().setMessage(e, ((HttpRequest)httpRequest).getRequestLine().getUri());        
    } catch (NoHttpResponseException e) {
    } catch (UnknownHostException e) {
      throw e;
    } 
    if(httpResponse == null) return null;
    return httpResponse;
  }

  public void abort() { 
    if(httpRequest == null)  return;
    httpRequest.abort();
    if(reader != null) reader.abort();
//    HttpRequestBase httpRequestBase = (HttpRequestBase) httpRequest;
//    DefaultRequestDirector director = (DefaultRequestDirector) httpRequestBase.getRequestDirector();
//    if(director == null) return;
//    director.abortConnection(); 
  }
  
  public void release() {
    if(httpRequest == null)  return;
    if(reader != null) reader.abort();
    reader = null;
//    HttpRequestBase httpRequestBase = (HttpRequestBase) httpRequest;
//    DefaultRequestDirector director = ((DefaultRequestDirector) httpRequestBase.getRequestDirector());
//    if(director == null) return;
//    director.releaseConnection();
    RequestManager.getInstance().close((HttpRequest)httpRequest);
  }
  
  public byte[] readBody() throws Exception  {
    if(httpResponse == null) return null;
    if(reader != null) reader.abort();
    reader = new HttpResponseReader();
    try {
      byte [] bytes = reader.readBody(httpResponse);
      if(bytes == null) return null;
      readData = bytes.length;
      return bytes;
    } finally {
      release();
    }
  }
  
  public byte[] readBody(HttpResponse response) throws Exception  {
    if(response == null) return null;
    if(reader != null) reader.abort();
    reader = new HttpResponseReader();
    byte [] bytes = reader.readBody(response);
    readData = bytes.length;
    return bytes;
  }
  
  public long getReadData() { return readData; }
  
}
