/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.link.explorer;

import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.AbortableHttpRequest;
import org.vietspider.net.client.WebClient;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 18, 2007  
 */
public class HttpMethodHandler1 {

  public final static String HTTP_REQUEST_TIMEOUT = "http.request.timeout";

  private AbortableHttpRequest httpRequest;
  private HttpHost httpHost;
  private WebClient webClient;

  public HttpMethodHandler1(WebClient webClient) {
    this.webClient = webClient;
  }

  public WebClient getWebClient() { return webClient; }

  public HttpResponse execute(String address, 
      String referer, List<NameValuePair> params, String charset) throws Exception {
    this.httpHost = webClient.createHttpHost(address); 
    return execute(webClient.createPostMethod(address, referer, params, charset));
  }

  public HttpResponse execute(String address, String referer) throws Exception {
    this.httpHost = webClient.createHttpHost(address);
    return execute(webClient.createGetMethod(address, referer));
  }

  public HttpResponse execute(HttpHost host, AbortableHttpRequest abortedHttpRequest) throws Exception {
    this.httpHost = host;
    return execute(abortedHttpRequest);
  }

  private HttpResponse execute(AbortableHttpRequest httpRequest_) throws Exception {
    this.httpRequest = null;
    HttpResponse httpResponse = null;
    this.httpRequest = httpRequest_;
    if(httpRequest == null) return null;

    httpResponse = webClient.execute(httpHost, (HttpRequest)httpRequest);

    if(httpResponse == null) return null;
    httpRequest = null;
    return httpResponse;
  }


  public void abort() { 
    if(httpRequest != null) httpRequest.abort();
  }

}
